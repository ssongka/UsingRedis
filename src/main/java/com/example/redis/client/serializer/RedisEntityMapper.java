package com.example.redis.client.serializer;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;


public class RedisEntityMapper {
    private static ObjectMapper objectMapper;

    static {
        objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        objectMapper.enableDefaultTyping();
    }

    public static ObjectMapper getMapper() {
        return objectMapper;
    }
}
