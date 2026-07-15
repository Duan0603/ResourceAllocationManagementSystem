package com.company.resourcealloc.controller;

import com.company.resourcealloc.dto.AIQueryRequest;
import com.company.resourcealloc.service.AIService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AIController {

    private final AIService aiService;

    @PostMapping("/recommend")
    public ResponseEntity<Map<String, Object>> getRecommendations(@RequestBody AIQueryRequest request) {
        return ResponseEntity.ok(aiService.getRecommendations(request.query()));
    }

    @PostMapping("/risk-detect")
    public ResponseEntity<Map<String, Object>> detectRisks(@RequestBody AIQueryRequest request) {
        return ResponseEntity.ok(aiService.detectRisks(request.query()));
    }
}
