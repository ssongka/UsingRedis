package com.example.redis.config;

import org.springframework.stereotype.Component;
import org.springframework.util.NumberUtils;


@Component
public class ServerConfig {

    private final KeyValueRepository keyValueRepository;

    public ServerConfig(KeyValueRepository keyValueRepository) {
        this.keyValueRepository = keyValueRepository;
    }

    public String getString( String key, String defaultValue) {
        return this.keyValueRepository.findById(key)
                .map(KeyValue::getValue)
                .orElse(defaultValue);
    }

    public int getInt( String key, int defaultValue) {
        String s = getString(key, String.valueOf(defaultValue));
        return NumberUtils.parseNumber(s, Integer.class);
    }

    public long getLong(String key, long defaultValue) {
        String s = getString(key, String.valueOf(defaultValue));
        return NumberUtils.parseNumber(s, Long.class);
    }
}
