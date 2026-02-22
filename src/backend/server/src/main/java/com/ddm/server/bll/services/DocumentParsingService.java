package com.ddm.server.bll.services;

import com.ddm.server.bll.contracts.IDocumentParsingService;
import com.ddm.server.bll.dtos.upload.SecurityDocumentInfo;
import com.ddm.server.bll.dtos.upload.SecurityDocumentUploadRequest;
import com.ddm.server.dll.models.SecurityDocument;
import com.ddm.server.dll.models.enums.ThreatLevel;
import com.ddm.server.dll.repositories.es.SecurityDocumentIndexRepository;
import com.ddm.server.dll.repositories.postgres.SecurityDocumentRepository;
import lombok.extern.log4j.Log4j;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.Loader;
import org.hibernate.Hibernate;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
public class DocumentParsingService implements IDocumentParsingService {

    @Value("${rustfs.bucket}")
    private String securityDocumentBucket;
    @Value("${geocoding.api_url}")
    private String geocodingApiUrl;
    @Value("${geocoding.api_key}")
    private String geocodingApiKey;
    private final S3Client s3;
    private final Pattern forensicsPersonPattern = Pattern.compile("Nadležni forenzičar: (.+)", Pattern.UNICODE_CHARACTER_CLASS);
    private final Pattern cirtEntityPattern = Pattern.compile("Uzorak podnesen od strane: (.+)", Pattern.UNICODE_CHARACTER_CLASS);
    private final Pattern cirtEntityAddressPattern = Pattern.compile("Adresa entiteta koji je podneo uzorak: (.+)", Pattern.UNICODE_CHARACTER_CLASS);
    private final Pattern threatNamePattern = Pattern.compile("Naziv pretnje: (.+)", Pattern.UNICODE_CHARACTER_CLASS);
    private final Pattern descriptionPattern = Pattern.compile("Opis ponašanja: (.+)", Pattern.UNICODE_CHARACTER_CLASS);
    private final Pattern threatLevelPattern = Pattern.compile("Nivo pretnje: (.+)", Pattern.UNICODE_CHARACTER_CLASS);
    private final Pattern hashPattern = Pattern.compile("Heš uzorka: (.+)", Pattern.UNICODE_CHARACTER_CLASS);
    private final SecurityDocumentRepository securityDocumentRepository;
    private final SecurityDocumentIndexRepository securityDocumentIndexRepository;

    public DocumentParsingService(SecurityDocumentRepository documentRepository,
                                  SecurityDocumentIndexRepository indexRepository,
                                  @Value("${rustfs.username}") String rustFsUsername,
                                  @Value("${rustfs.password}") String rustFsPassword,
                                  @Value("${rustfs.url}") String rustFsUrl){

        this.securityDocumentRepository = documentRepository;
        this.securityDocumentIndexRepository = indexRepository;
        this.s3 = S3Client.builder().endpointOverride(URI.create(rustFsUrl))
                .region(Region.US_EAST_1)
                .credentialsProvider(
                        StaticCredentialsProvider.create(
                                AwsBasicCredentials.create(rustFsUsername, rustFsPassword))).forcePathStyle(true).build();
    }

    @Override
    public SecurityDocumentInfo parseAndSave(SecurityDocumentUploadRequest uploadRequest) throws Exception {
        // Step 1 - parse document
        SecurityDocument newDocument = parseDocument(uploadRequest.getDocument());

        // Step 2 - upload document
        String documentExtension = StringUtils.getFilenameExtension(
                uploadRequest.getDocument().getOriginalFilename()
        );
        String documentKey = UUID.randomUUID().toString() + "." + documentExtension;
        try{
            s3.putObject(
                    PutObjectRequest.builder().bucket(this.securityDocumentBucket).key(documentKey).build(),
                    RequestBody.fromInputStream(uploadRequest.getDocument().getInputStream(), uploadRequest.getDocument().getSize())
            );
        }catch (IOException e){
            log.error("Unable to upload document to the server {}", uploadRequest.getDocument().getOriginalFilename());
            throw new Exception("Unable to upload document to the server. Aborting request");
        }

        // Step 3 - save entry in the database
        newDocument.setDocumentKey(documentKey);
        newDocument = this.securityDocumentRepository.save(newDocument);

        return new SecurityDocumentInfo(newDocument);
    }

    @Override
    public void delete(Long id) {
        SecurityDocument document = this.securityDocumentRepository.getReferenceById(id);
        if(document == null)
            return;

        this.s3.deleteObject(
                DeleteObjectRequest.builder().bucket(this.securityDocumentBucket).key(document.getDocumentKey()).build()
        );
        this.securityDocumentRepository.deleteById(id);
    }

    @Override
    public SecurityDocumentInfo index(SecurityDocumentInfo documentInfo) {
        // Step 1 - save new info
        SecurityDocument document = this.securityDocumentRepository.getReferenceById(documentInfo.getId());
        document.setThreatName(documentInfo.getThreatName());
        document.setDocumentContent(documentInfo.getDocumentContent());
        document.setThreatLevel(documentInfo.getThreatLevel());
        document.setThreatDescription(documentInfo.getThreatDescription());
        document.setOrgAddress(documentInfo.getOrgAddress());
        document.setOrgName(documentInfo.getOrgName());
        document.setFullName(documentInfo.getFullName());
        document.setThreatSampleHash(documentInfo.getThreatSampleHash());
        this.securityDocumentRepository.save(document);

        document = (SecurityDocument) Hibernate.unproxy(document);
        // Step 2 - geolocate
        try {
            document.setGeoPoint(this.geocodeAddress(document.getOrgAddress()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // Step 3 - index in ES
        this.securityDocumentIndexRepository.save(document);
        return new SecurityDocumentInfo(document);
    }

    private SecurityDocument parseDocument(MultipartFile file){
        try(PDDocument document = Loader.loadPDF(file.getBytes())){
            PDFTextStripper stripper = new PDFTextStripper();
            String text = stripper.getText(document);

            Matcher forensicsPersonMatcher = this.forensicsPersonPattern.matcher(text);
            forensicsPersonMatcher.find();

            Matcher cirtEntityMatcher = this.cirtEntityPattern.matcher(text);
            cirtEntityMatcher.find();

            Matcher cirtEntityAddressMatcher = this.cirtEntityAddressPattern.matcher(text);
            cirtEntityAddressMatcher.find();

            Matcher threatNameMatcher = this.threatNamePattern.matcher(text);
            threatNameMatcher.find();

            Matcher threatLevelMatcher = this.threatLevelPattern.matcher(text);
            threatLevelMatcher.find();

            Matcher hashPatternMatcher = this.hashPattern.matcher(text);
            hashPatternMatcher.find();

            Matcher descriptionMatcher = this.descriptionPattern.matcher(text);
            descriptionMatcher.find();

            SecurityDocument doc = new SecurityDocument();
            doc.setFullName(forensicsPersonMatcher.group(1));
            doc.setOrgName(cirtEntityMatcher.group(1));
            doc.setOrgAddress(cirtEntityAddressMatcher.group(1));
            doc.setThreatName(threatNameMatcher.group(1));
            doc.setThreatLevel(this.parseThreatLevel(threatLevelMatcher.group(1)));
            doc.setThreatSampleHash(hashPatternMatcher.group(1));
            doc.setThreatDescription(descriptionMatcher.group(1));
            doc.setDocumentContent(text);

            return doc;

        }catch (Exception e) {
            log.error("Failed parsing the uploaded document {}", file.getOriginalFilename());
        }

        return new SecurityDocument();
    }

    private ThreatLevel parseThreatLevel(String input){
        switch (input.trim().toLowerCase()){
            case "kritičan":
                return ThreatLevel.CRITICAL;

            case "visok":
                return ThreatLevel.HIGH;

            case "srednji":
                return ThreatLevel.MEDIUM;

            case "nizak":
                return ThreatLevel.LOW;

            default:
                return null;
        }
    }

    private GeoPoint geocodeAddress(String address) throws Exception {
        try(HttpClient client = HttpClient.newHttpClient()){
            String requestUrl = String.format("%s?q=%s&api_key=%s", this.geocodingApiUrl, URLEncoder.encode(address, StandardCharsets.UTF_8), this.geocodingApiKey);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(requestUrl))
                    .GET().build();

            String result = client.send(request, HttpResponse.BodyHandlers.ofString()).body();
            JSONArray jsonResults = new JSONArray(result);
            if (!jsonResults.isEmpty()) {
                JSONObject firstMatch = jsonResults.getJSONObject(0);
                return new GeoPoint(firstMatch.getDouble("lat"), firstMatch.getDouble("lon"));
            }
            throw new Exception("unlucky");
        }catch (Exception e){
            log.error("Unable to geocode address before indexing");
            throw new Exception("Unable to geocode address before indexing");
        }
    }
}
