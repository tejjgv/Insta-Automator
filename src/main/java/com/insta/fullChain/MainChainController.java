package com.insta.fullChain;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/insta")
public class MainChainController
{
    private final MainChain mainChain;

    public MainChainController(MainChain mainChain) {
        this.mainChain = mainChain;
    }

    @PostMapping("/publish")
    public ResponseEntity<?> publishReel(@RequestBody Map<String, String> body) {
        String url = body.get("url");
        mainChain.startChain(url);
        return ResponseEntity.ok("Reel Uploaded Successfully! ");
    }
}
