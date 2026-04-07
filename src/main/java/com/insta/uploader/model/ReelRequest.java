package com.insta.uploader.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReelRequest {

    @JsonProperty("video_url")
    private String videoUrl;

    @JsonProperty("caption")
    private String caption;

    @JsonProperty("hashtags")
    private String hashtags;

    @JsonProperty("thumbnail_url")
    private String thumbnailUrl;


    /**
     * Helper to combine caption and hashtags for the API call.
     * Lombok handles the standard getters, but we keep custom logic here.
     */
    public String getFullCaption() {
        String base = caption != null ? caption : "";
        if (hashtags == null || hashtags.isBlank()) {
            return base;
        }
        return base + "\n\n" + hashtags;
    }
}