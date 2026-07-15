package com.company.resourcealloc.service;

import java.util.Map;

public interface AIService {
    Map<String, Object> getRecommendations(String query);
    Map<String, Object> detectRisks(String query);
}
