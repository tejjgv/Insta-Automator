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
    private Cloudinary cloudinary;

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
    public String[] uploadVideo(String localVideoPath, String localThumbnailPath) {
        log.info("Starting upload to Cloudinary for file: {}", localVideoPath);
        try {
            File fileToUpload = new File(localVideoPath);
            if (!fileToUpload.exists()) {
                throw new RuntimeException("Local file not found: " + localVideoPath);
            }

            // The "resource_type", "video" parameter is CRITICAL!
            // If you forget this, Cloudinary treats it as an image and it breaks.
            Map uploadResult = cloudinary.uploader().upload(fileToUpload, ObjectUtils.asMap(
                    "resource_type", "video"
            ));

            String c_videoUrl = uploadResult.get("secure_url").toString();

            File thumbnailFile = new File(localThumbnailPath);
            if (!thumbnailFile.exists()) {
                throw new RuntimeException("Thumbnail file not found: " + thumbnailFile);
            }

            Map thumbUpload = cloudinary.uploader().upload(thumbnailFile, ObjectUtils.asMap(
                    "resource_type", "image"
            ));

            String c_thumbnailUrl = thumbUpload.get("secure_url").toString();

            String[] urls = {c_videoUrl, c_thumbnailUrl};
            log.info("Thumbnail uploaded: {}", c_thumbnailUrl);

            log.info("Successfully uploaded to Cloudinary! URL: {}", c_videoUrl + "," + c_thumbnailUrl);

            return urls;

        } catch (Exception e) {
            throw new RuntimeException("Failed to upload video to Cloudinary", e);
        }
    }
}