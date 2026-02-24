package com.ddm.server.bll.services;

import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.common.geo.GeoPoint;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

@Service
@Slf4j
public class GeoCodingService {
    @Value("${geocoding.api_url}")
    private String geocodingApiUrl;
    @Value("${geocoding.api_key}")
    private String geocodingApiKey;

    public GeoPoint geocodeAddress(String address) throws Exception {
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
