package com.insta.uploader.controller;

import com.insta.uploader.model.ReelRequest;
import com.insta.uploader.service.InstagramService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/reels")
public class ReelController {

    @Autowired
    private InstagramService instagramService;

    @PostMapping("/publish")
    public ResponseEntity<Map<String, String>> publishReel(@RequestBody ReelRequest request) {
        // 1. Generate a tracking ID
        String jobId = UUID.randomUUID().toString();

        String cleanCaption = sanitize(request.getFullCaption());


        // 3. Hand off to Spring's managed async execution
        instagramService.processFullReelFlowAsync(jobId, new String[]{request.getVideoUrl(),request.getThumbnailUrl()}, request.getFullCaption());
        // 4. Return the ID so the client can check the status later
        return ResponseEntity.accepted().body(Map.of(
                "message", "Reel processing started.",
                "jobId", jobId
        ));
    }

    private String sanitize(String input) {
        if (input == null) return "";

        return input
                .replace('\u00A0', ' ')   // remove NBSP (your main issue)
                .replaceAll("\\p{C}", "") // remove control chars
                .trim();
    }
}