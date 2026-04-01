package com.insta.uploader.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

@Service
public class InstagramService {

    private static final Logger log = LoggerFactory.getLogger(InstagramService.class);
    private static final String BASE_URL = "https://graph.instagram.com/v23.0/";

    @Value("${instagram.access-token}")
    private String accessToken;

    @Value("${instagram.user-id}")
    private String userId;

    private final RestTemplate restTemplate;

    // 1. Inject RestTemplate via Builder (Best Practice for timeouts/configuration)
    public InstagramService(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    // 2. Add @Async and accept the jobId
    @Async("reelTaskExecutor")
    public void processFullReelFlowAsync(String jobId, String videoUrl, String caption) {
        log.info("Starting background Reel processing for Job ID: {}", jobId);
        log.info("Job {} | videoUrl='{}' | caption='{}'", jobId, videoUrl, caption);

        try {
            updateJobStatus(jobId, "PROCESSING", "Creating media container...");
            String creationId = createMediaContainer(videoUrl, caption);

            updateJobStatus(jobId, "PROCESSING", "Waiting for Instagram to process the video...");
            waitForProcessing(creationId);

            updateJobStatus(jobId, "PROCESSING", "Publishing Reel to timeline...");
            publishMedia(creationId);

            updateJobStatus(jobId, "COMPLETED", "Reel published successfully!");
            log.info("Successfully completed Job ID: {}", jobId);

        } catch (Exception e) {
            // 3. Catch ALL exceptions in the background thread and log to DB
            log.error("Job ID {} failed: {}", jobId, e.getMessage(), e);
            updateJobStatus(jobId, "FAILED", e.getMessage());
        }
    }

    private String createMediaContainer(String videoUrl, String caption) {
        // For Instagram Graph API, passing the token in the URL is generally safest
        String url = UriComponentsBuilder.fromHttpUrl(BASE_URL + userId + "/media")
                                         .queryParam("access_token", accessToken)
                                         .toUriString();

        Map<String, String> body = Map.of(
                "video_url", videoUrl,
                "caption", caption != null ? caption : "",
                "media_type", "REELS"
        );

        try {
            Map response = restTemplate.postForObject(url, body, Map.class);
            if (response == null || !response.containsKey("id")) {
                throw new RuntimeException("Media container creation failed. API Response: " + response);
            }
            return response.get("id").toString();
        } catch (RestClientException e) {
            throw new RuntimeException("Failed to reach Instagram API during container creation", e);
        }
    }

    private void waitForProcessing(String creationId) {
        String statusUrl = BASE_URL + creationId;
        boolean isReady = false;
        int attempts = 0;
        int maxAttempts = 12; // 12 attempts * 10 seconds = 2 minutes max wait

        while (!isReady && attempts < maxAttempts) {
            try {
                // Wait 10 seconds before the first check and between subsequent checks
                Thread.sleep(10000);

                String finalUrl = UriComponentsBuilder.fromHttpUrl(statusUrl)
                                                      .queryParam("fields", "status_code")
                                                      .queryParam("access_token", accessToken)
                                                      .toUriString();

                Map response = restTemplate.getForObject(finalUrl, Map.class);
                if (response == null) throw new RuntimeException("Received null response while checking status");

                String status = (String) response.get("status_code");
                log.debug("Container {} status: {}", creationId, status);

                if ("FINISHED".equalsIgnoreCase(status)) {
                    isReady = true;
                } else if ("ERROR".equalsIgnoreCase(status)) {
                    throw new RuntimeException("Instagram processing failed for this video (Format issue or corrupt file).");
                }
                attempts++;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Polling thread was interrupted", e);
            }
        }
        if (!isReady) throw new RuntimeException("Instagram video processing timed out after " + (maxAttempts * 10) + " seconds.");
    }

    private void publishMedia(String creationId) {
        String url = UriComponentsBuilder.fromHttpUrl(BASE_URL + userId + "/media_publish")
                                         .queryParam("access_token", accessToken)
                                         .toUriString();

        Map<String, String> body = Map.of("creation_id", creationId);

        restTemplate.postForObject(url, body, Map.class);
    }

    // --- MOCK DATABASE METHOD ---
    private void updateJobStatus(String jobId, String status, String message) {
        // In reality, this would be: jobRepository.updateStatus(jobId, status, message);
        log.info("DB UPDATE -> Job: {} | Status: {} | Message: {}", jobId, status, message);
    }
}