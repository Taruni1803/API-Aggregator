package com.Taruni.Api_Aggregator.controller;

import com.Taruni.Api_Aggregator.dto.AggregatedDataResponse;
import com.Taruni.Api_Aggregator.model.AggregatedData;
import com.Taruni.Api_Aggregator.service.AggregationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/data")
@RequiredArgsConstructor
public class DataController {

    private final AggregationService aggregationService;

    /**
     * Main endpoint to get aggregated data
     * Example: GET /api/data?apiName=open-meteo&city=hyderabad
     */
    @GetMapping
    public ResponseEntity<AggregatedDataResponse> getData(
            @RequestParam String apiName,
            @RequestParam Map<String, String> params) {

        log.info("Request received for API: {} with params: {}", apiName, params);

        AggregatedData data = aggregationService.fetchAndStore(apiName, params);

        AggregatedDataResponse response = AggregatedDataResponse.builder()
                .source(data.getSource())
                .dataType(data.getDataType())
                .cacheKey(data.getCacheKey())
                .rawResponse(data.getRawResponse())
                .normalizedData(data.getNormalizedData())
                .fetchedAt(data.getFetchedAt())
                .expiresAt(data.getExpiresAt())
                .fromCache(false)                    // Will improve later with cache check
                .responseTimeMs(data.getResponseTimeMs())
                .build();

        return ResponseEntity.ok(response);
    }
}