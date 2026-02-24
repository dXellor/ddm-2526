package com.ddm.server.bll.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

@Service
public class SentanceTransformService {

    @Value("${sts.url}")
    private String stsUrl;
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper;

    public SentanceTransformService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public float[] embedText (String text) throws Exception {
        String json = objectMapper.writeValueAsString(
                java.util.Map.of("content", text)
        );
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(this.stsUrl))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        List<Double> embeddingList = objectMapper.readValue(response.body(), List.class);
        float[] embedding = new float[embeddingList.size()];
        for (int i = 0; i < embeddingList.size(); i++) {
            embedding[i] = embeddingList.get(i).floatValue();
        }
        return embedding;
    }
}
