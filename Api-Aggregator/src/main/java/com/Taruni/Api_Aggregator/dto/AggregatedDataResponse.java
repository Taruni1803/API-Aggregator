package com.Taruni.Api_Aggregator.dto;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AggregatedDataResponse {

    private String source;
    private String dataType;
    private String cacheKey;
    private JsonNode rawResponse;
    private JsonNode normalizedData;
    private Instant fetchedAt;
    private Instant expiresAt;
    private boolean fromCache;
    private Long responseTimeMs;
}