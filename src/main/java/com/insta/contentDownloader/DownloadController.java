package com.insta.contentDownloader;

import com.insta.contentDownloader.DownloadRequest;
import com.insta.contentDownloader.YtDlpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/downloads")
public class DownloadController {

    @Autowired
    private YtDlpService ytDlpService;

    @PostMapping("/fetch")
    public ResponseEntity<?> fetchVideo(@RequestBody DownloadRequest request) {
        try {
            // This will block until the download is fully complete
            YtDlpService.DownloadResult result = ytDlpService.downloadVideo(request.getUrl());
            return ResponseEntity.ok(result);

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
        }
    }
}