package com.backend.cache;

import java.time.Duration;
import java.util.Optional;

public interface ICacheData {
    String cacheKey();

    Optional<Duration> ttl();
}
