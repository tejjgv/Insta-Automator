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
            String localVideoPath = request.get("localFilePath");
            String localThumbnailPath = request.get("localThumbnailPath");

            // Upload to Cloudinary
            String[] publicUrl = cloudinaryService.uploadVideo(localVideoPath, localThumbnailPath);

            // Return the live URL so you can click it!
            return ResponseEntity.ok(Map.of(
                    "status", "Success! ☁️",
                    "video_url", publicUrl[0],
                    "thumbnail_url", publicUrl[1]
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }
}