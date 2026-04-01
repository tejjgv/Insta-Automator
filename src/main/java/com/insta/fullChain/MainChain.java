package com.insta.fullChain;

import java.io.File;
import java.util.UUID;

import com.insta.cloudinary.CloudinaryService;
import com.insta.contentDownloader.YtDlpService;
import com.insta.contentDownloader.YtDlpService.DownloadResult;
import com.insta.uploader.service.InstagramService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class MainChain {
    private final YtDlpService ytDlpService;
    private final CloudinaryService cloudinaryService;
    private final InstagramService instagramService;

    public MainChain(YtDlpService ytDlpService,
                     CloudinaryService cloudinaryService,
                     InstagramService instagramService) {
        this.ytDlpService = ytDlpService;
        this.cloudinaryService = cloudinaryService;
        this.instagramService = instagramService;
    }

    public void startChain(String instaUrl){
        log.info("Starting chain");

            // 1. Download content from the URL
      DownloadResult result = ytDlpService.downloadVideo(instaUrl);
      String DownloadedFilePath = result.localFilePath;

      String cloudinaryUrl =  CloudinaryService.uploadVideo(DownloadedFilePath);
        log.info("Cloudinary URL: {}", cloudinaryUrl);

      String caption = buildCaption(result);

      log.info("Publishing to Instagram");
        String jobId = UUID.randomUUID().toString();

       instagramService.processFullReelFlowAsync(jobId, cloudinaryUrl, caption);

         // 4. Cleanup local files
        log.info("Cleaning up local files");
          cleanupFiles(DownloadedFilePath);
    }

    private String buildCaption(DownloadResult result) {

        String title = "";
        String desc = result.description != null ? result.description : "";

        return title + "\n\n" + desc;
    }

    private String sanitize(String input) {
        if (input == null) return "";

        return input
                .replace('\u00A0', ' ')   // remove NBSP (your main issue)
                .replaceAll("\\p{C}", "") // remove control chars
                .trim();
    }

    private void cleanupFiles(String filePath) {

        try {
            File file = new File(filePath);

            if (!file.exists()) return;

            String baseName = file.getName().split("\\.")[0]; // abc123
            File dir = file.getParentFile();

            File[] relatedFiles = dir.listFiles((d, name) ->
                    name.startsWith(baseName)
            );

            if (relatedFiles != null) {
                for (File f : relatedFiles) {
                    if (f.delete()) {
                        log.info("🧹 Deleted file: {}", f.getAbsolutePath());
                    } else {
                        log.warn("⚠️ Failed to delete: {}", f.getAbsolutePath());
                    }
                }
            }

        } catch (Exception e) {
            log.warn("Cleanup failed for: {}", filePath, e);
        }
    }
}
