package com.Taruni.Api_Aggregator.model;
import com.fasterxml.jackson.databind.JsonNode;
import java.util.UUID;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "api_sources")
@Getter
@Setter
@Builder
public class ApiSource {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String name;               // e.g., "open-meteo-weather", "my-news-api"

    @Column(nullable = false)
    private String baseUrl;

    private String apiKey;             // encrypted in production

    private String apiKeyHeaderName;   // e.g., "X-API-KEY" or "Authorization"

    @Column(columnDefinition = "jsonb")
    private JsonNode defaultParams;    // default query params

    @Column(columnDefinition = "jsonb")
    private JsonNode headers;          // custom headers

    private String method;             // GET, POST

    private Integer defaultTtlMinutes;
    
    @Builder.Default
    private boolean isActive = true;

    private String dataType;           // weather, news, stocks, etc.
}