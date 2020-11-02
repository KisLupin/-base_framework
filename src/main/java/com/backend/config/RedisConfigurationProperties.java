package com.backend.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.Map;

@Data
@ConfigurationProperties(prefix = "cache")
public class RedisConfigurationProperties {

    private long timeoutOfSeconds = 60;
    private int port = 6379;
    private String host = "localhost";
    // Mapping of cacheNames to expire-after-write timeout in seconds
    private Map<String, Long> cacheExpiration = new HashMap<>();
}
