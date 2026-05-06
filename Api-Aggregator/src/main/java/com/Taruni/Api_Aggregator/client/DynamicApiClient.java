package com.Taruni.Api_Aggregator.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.Taruni.Api_Aggregator.model.ApiSource;
import com.Taruni.Api_Aggregator.exception.ApiNotFoundException;
import com.Taruni.Api_Aggregator.repository.ApiSourceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.Duration;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class DynamicApiClient {

    private final WebClient.Builder webClientBuilder;
    private final ApiSourceRepository apiSourceRepository;

    public JsonNode fetchData(String apiName, Map<String, String> userParams) {
        ApiSource source = apiSourceRepository.findByName(apiName)
                .orElseThrow(() -> new ApiNotFoundException("API Source not found: " + apiName));

        WebClient webClient = buildWebClient(source);

        String finalUrl = buildFinalUrl(source, userParams);

        log.info("Calling external API: {} | URL: {}", apiName, finalUrl);

        return webClient.method(HttpMethod.valueOf(source.getMethod()))
                .uri(finalUrl)
                .headers(headers -> addCustomHeaders(headers, source))
                .retrieve()
                .bodyToMono(JsonNode.class)
                .timeout(Duration.ofSeconds(12))
                .block();
    }

    private WebClient buildWebClient(ApiSource source) {
        return webClientBuilder
                .baseUrl(source.getBaseUrl())
                .build();
    }

    private String buildFinalUrl(ApiSource source, Map<String, String> userParams) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(source.getBaseUrl());

        // Add default params from ApiSource
        if (source.getDefaultParams() != null) {
            source.getDefaultParams().properties().forEach(entry -> 
                builder.queryParam(entry.getKey(), entry.getValue().asText())
            );
        }

        // Override / add user provided params
        if (userParams != null) {
            userParams.forEach(builder::queryParam);
        }

        return builder.build().toUriString();
    }

    private void addCustomHeaders(HttpHeaders headers, ApiSource source) {
        if (source.getApiKey() != null && source.getApiKeyHeaderName() != null) {
            headers.add(source.getApiKeyHeaderName(), source.getApiKey());
        }

        if (source.getHeaders() != null) {
            source.getHeaders().properties().forEach(entry -> 
                headers.add(entry.getKey(), entry.getValue().asText())
            );
        }
    }
}