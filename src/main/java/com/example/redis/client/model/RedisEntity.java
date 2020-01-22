package com.example.redis.client.model;

import lombok.Data;


@Data
public abstract class RedisEntity {
    protected boolean isDatabaseLoaded;
}
