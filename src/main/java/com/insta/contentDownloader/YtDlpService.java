package com.insta.contentDownloader;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class YtDlpService {

    private static final Logger log = LoggerFactory.getLogger(YtDlpService.class);
    private static ObjectMapper objectMapper = null;
    private static final String YT_DLP_PATH = "C:\\Users\\tejes\\AppData\\Local\\Microsoft\\WinGet\\Links\\yt-dlp.exe";

    public YtDlpService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        // Ensure the downloads directory exists
        new File("downloads").mkdirs();
    }

    public static class DownloadResult {
        public String localFilePath;
        public String title;
        public String description;
    }

    public DownloadResult downloadVideo(String url) {
        String downloadId = UUID.randomUUID().toString();
        String outputTemplate = "downloads/" + downloadId + ".%(ext)s";

        try {
            log.info("Starting download for URL: {}", url);

            ProcessBuilder pb = new ProcessBuilder(
                    YT_DLP_PATH,
                    "--ffmpeg-location", "C:\\Users\\tejes\\AppData\\Local\\Microsoft\\WinGet\\Links\\",  // folder, not the exe
                    "-f", "bestvideo[ext=mp4]+bestaudio[ext=m4a]/bestvideo+bestaudio/best",
                    "--merge-output-format", "mp4",
                    "--write-info-json",
                    "--no-playlist",
                    "-o", outputTemplate,
                    url
            );

            pb.redirectErrorStream(true);
            Process process = pb.start();

            StringBuilder outputLog = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    log.debug("yt-dlp [{}]: {}", downloadId, line);
                    outputLog.append(line).append("\n");
                }
            }

            int exitCode = process.waitFor();
            if (exitCode != 0) {
                throw new RuntimeException("yt-dlp failed (exit " + exitCode + "):\n" + outputLog);
            }

            // Find the downloaded video file dynamically
            File downloadDir = new File("downloads");
            File[] matchingFiles = downloadDir.listFiles(
                    (dir, name) -> name.startsWith(downloadId)
                            && !name.endsWith(".info.json")
                            && !name.endsWith(".part")
            );

            if (matchingFiles == null || matchingFiles.length == 0) {
                throw new RuntimeException("Download completed but video file not found. yt-dlp output:\n" + outputLog);
            }

            String videoFile = matchingFiles[0].getPath();
            String jsonFile = "downloads/" + downloadId + ".info.json";

            if (!new File(jsonFile).exists()) {
                throw new RuntimeException("Metadata file missing. yt-dlp output:\n" + outputLog);
            }

            String jsonContent = new String(Files.readAllBytes(Paths.get(jsonFile)));
            JsonNode jsonNode = objectMapper.readTree(jsonContent);

            DownloadResult result = new DownloadResult();
            result.localFilePath = videoFile;
            result.title = jsonNode.path("title").asText("No Title");
            result.description = jsonNode.path("description").asText("No Description");

            log.info("Download complete. File: {}", videoFile);
            return result;

        } catch (Exception e) {
            throw new RuntimeException("Failed to download video", e);
        }
    }
}