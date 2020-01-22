package com.example.redis.client.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;


@Data
public abstract class RedisHashEntity<T> {

    @JsonIgnore
    public abstract T getEntityKey();
}
