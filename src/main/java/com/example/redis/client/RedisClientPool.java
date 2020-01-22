package com.example.redis.client;

import com.example.redis.config.ServerConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

@Configuration
public class RedisClientPool {

    private final ServerConfig serverConfig;

    public RedisClientPool(ServerConfig serverConfig) {
        this.serverConfig = serverConfig;
    }

    @Bean
    public JedisPool getJedisPool() {
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(200);
        config.setMaxIdle(100);
        config.setMinIdle(100);
        config.setTestOnBorrow(false);
        config.setBlockWhenExhausted(true);

        String redisIp = serverConfig.getString("REDIS_IP", "127.0.0.1");
        int redisPort = serverConfig.getInt("REDIS_PORT", 6379);

        return new JedisPool(config, redisIp, redisPort);
    }
}
