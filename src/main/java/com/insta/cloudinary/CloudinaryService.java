package com.insta.cloudinary;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.io.File;
import java.util.Map;

@Service
public class CloudinaryService {

    private static final Logger log = LoggerFactory.getLogger(CloudinaryService.class);
    private static Cloudinary cloudinary;

    @Value("${cloudinary.cloud-name}")
    private String cloudName;

    @Value("${cloudinary.api-key}")
    private String apiKey;

    @Value("${cloudinary.api-secret}")
    private String apiSecret;

    // This runs automatically when Spring starts to configure Cloudinary
    @PostConstruct
    public void initializeCloudinary() {
        cloudinary = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", cloudName,
                "api_key", apiKey,
                "api_secret", apiSecret
        ));
    }

    /**
     * Uploads a local video file to Cloudinary and returns the public URL.
     */
    public static String uploadVideo(String localFilePath) {
        log.info("Starting upload to Cloudinary for file: {}", localFilePath);
        try {
            File fileToUpload = new File(localFilePath);
            if (!fileToUpload.exists()) {
                throw new RuntimeException("Local file not found: " + localFilePath);
            }

            // The "resource_type", "video" parameter is CRITICAL!
            // If you forget this, Cloudinary treats it as an image and it breaks.
            Map uploadResult = cloudinary.uploader().upload(fileToUpload, ObjectUtils.asMap(
                    "resource_type", "video"
            ));

            String publicUrl = uploadResult.get("secure_url").toString();
            log.info("Successfully uploaded to Cloudinary! URL: {}", publicUrl);

            return publicUrl;

        } catch (Exception e) {
            throw new RuntimeException("Failed to upload video to Cloudinary", e);
        }
    }
}