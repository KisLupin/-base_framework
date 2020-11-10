package com.backend.cache;

import com.backend.common.Constant;
import lombok.Getter;
import lombok.Setter;

import java.time.Duration;
import java.util.Optional;

@Getter
@Setter
public class UserCache implements ICacheData{
    private String token;
    private Integer userId;
    private String username;
    private Duration ttl;

    @Override
    public String cacheKey() {
        return Constant.TOKEN_PREFIX + token;
    }

    @Override
    public Optional<Duration> ttl() {
        return Optional.of(ttl);
    }

    public UserCache(String token, Integer userId, String username, Duration ttl) {
        super();
        this.token = token;
        this.userId = userId;
        this.username = username;
        this.ttl = ttl;
    }

    public UserCache(String token, Integer userId, String username) {
        super();
        this.token = token;
        this.userId = userId;
        this.username = username;
    }

    public UserCache() {
        super();
    }
}
