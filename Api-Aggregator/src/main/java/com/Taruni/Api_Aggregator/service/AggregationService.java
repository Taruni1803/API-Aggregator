package com.Taruni.Api_Aggregator.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.Taruni.Api_Aggregator.client.DynamicApiClient;
import com.Taruni.Api_Aggregator.model.AggregatedData;
import com.Taruni.Api_Aggregator.model.ApiSource;
import com.Taruni.Api_Aggregator.repository.AggregatedDataRepository;
import com.Taruni.Api_Aggregator.repository.ApiSourceRepository;
import com.Taruni.Api_Aggregator.util.CacheKeyGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class AggregationService {

    private final DynamicApiClient dynamicApiClient;
    private final AggregatedDataRepository aggregatedDataRepository;
    private final ApiSourceRepository apiSourceRepository;

    /**
     * Main method to fetch data with caching awareness
     */
    public AggregatedData fetchAndStore(String apiName, Map<String, String> params) {
        String cacheKey = CacheKeyGenerator.generate(apiName, params);

        // Check if we already have fresh data in DB
        AggregatedData existing = aggregatedDataRepository.findByCacheKey(cacheKey).orElse(null);

        if (existing != null && existing.getExpiresAt().isAfter(Instant.now())) {
            log.info("Returning data from database cache for key: {}", cacheKey);
            return existing;
        }

        // Fetch from external API using Dynamic Client
        JsonNode rawResponse = dynamicApiClient.fetchData(apiName, params);

        // Normalize the data (optional but useful for frontend)
        JsonNode normalizedData = normalizeResponse(apiName, rawResponse);

        ApiSource source = apiSourceRepository.findByNameAndIsActive(apiName, true)
                .orElseThrow(() -> new RuntimeException("ApiSource not found: " + apiName));

        AggregatedData aggregatedData = AggregatedData.builder()
                .source(apiName)
                .dataType(source.getDataType())
                .cacheKey(cacheKey)
                .rawResponse(rawResponse)
                .normalizedData(normalizedData)
                .fetchedAt(Instant.now())
                .expiresAt(Instant.now().plus(source.getDefaultTtlMinutes() != null ? source.getDefaultTtlMinutes() : 60, ChronoUnit.MINUTES))
                .responseTimeMs(0L)
                .statusCode(200)
                .build();

        return aggregatedDataRepository.save(aggregatedData);
    }

    /**
     * Simple normalization - you can expand this later
     */
    private JsonNode normalizeResponse(String apiName, JsonNode raw) {
        // TODO: Add more normalization logic as you add more APIs
        return raw; // For now, return as-is
    }
}