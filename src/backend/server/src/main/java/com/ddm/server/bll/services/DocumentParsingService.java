package com.ddm.server.bll.services;

import com.ddm.server.bll.contracts.IDocumentParsingService;
import com.ddm.server.bll.dtos.upload.SecurityDocumentInfo;
import com.ddm.server.bll.dtos.upload.SecurityDocumentUploadRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import javax.swing.text.Document;
import java.io.IOException;
import java.net.URI;

@Service
public class DocumentParsingService implements IDocumentParsingService {

    @Value("${rustfs.bucket}")
    private String securityDocumentBucket;

    private final S3Client s3;

    public DocumentParsingService(@Value("${rustfs.username}") String rustfsUsername,
                                  @Value("${rustfs.password}") String rustfsPassword,
                                  @Value("${rustfs.bucket}") String securityDocumentBucket){

        this.s3 = S3Client.builder().endpointOverride(URI.create("http://localhost:8000"))
                .region(Region.US_EAST_1)
                .credentialsProvider(
                        StaticCredentialsProvider.create(
                                AwsBasicCredentials.create(rustfsUsername, rustfsPassword))).forcePathStyle(true).build();
    }

    @Override
    public SecurityDocumentInfo parseAndSave(SecurityDocumentUploadRequest uploadRequest) throws IOException {
        // Step 1 - parse document

        // Step 2 - upload document
        s3.putObject(
                PutObjectRequest.builder().bucket(this.securityDocumentBucket).key(uploadRequest.getDocument().getOriginalFilename()).build(),
                RequestBody.fromInputStream(uploadRequest.getDocument().getInputStream(), uploadRequest.getDocument().getSize())
        );

        // Step 3 - save entry in the database

        return null;
    }

    @Override
    public void delete(String uuid) {

    }

    @Override
    public SecurityDocumentInfo index(SecurityDocumentInfo documentInfo) {
        return null;
    }
}
