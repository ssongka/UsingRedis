package com.example.redis.client.serializer;


public interface RedisSerializer {

    <T> byte[] write(T t);
    <T> T read( byte[] bytes);
}
