package com.insta.cloudinary;

import com.insta.cloudinary.CloudinaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/cloudinary")
public class CloudinaryTestController {

    @Autowired
    private CloudinaryService cloudinaryService;

    @PostMapping("/test-upload")
    public ResponseEntity<?> testUpload(@RequestBody Map<String, String> request) {
        try {
            // Grab the file path from the Postman JSON
            String localPath = request.get("localFilePath");

            // Upload to Cloudinary
            String publicUrl = cloudinaryService.uploadVideo(localPath);

            // Return the live URL so you can click it!
            return ResponseEntity.ok(Map.of(
                    "status", "Success! ☁️",
                    "live_url", publicUrl
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }
}