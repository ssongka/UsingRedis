package com.example.redis.client;

import redis.clients.jedis.Jedis;


public interface RedisCallback<T> {
    T execute(Jedis jedis);
}
