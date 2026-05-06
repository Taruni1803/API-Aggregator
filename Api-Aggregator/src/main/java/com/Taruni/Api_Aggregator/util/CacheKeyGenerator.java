package com.Taruni.Api_Aggregator.util;

import java.util.Map;

public class CacheKeyGenerator {

    public static String generate(String apiName, Map<String, String> params) {
        StringBuilder key = new StringBuilder(apiName.toLowerCase().trim());

        if (params != null) {
            params.forEach((k, v) -> 
                key.append(":").append(k.toLowerCase()).append("=").append(v.toLowerCase().trim())
            );
        }

        return key.toString();
    }
}
