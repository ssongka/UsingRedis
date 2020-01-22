package com.example.redis.client.repository;

import com.example.redis.client.RedisClient;

import java.util.Set;

public abstract class SortedSetOpertaionRedisRepository extends AbstractRedisRepository<String, Long> {

    public SortedSetOpertaionRedisRepository(RedisClient redisClient) {
        super(redisClient);
    }

    protected Double zscore(final String key, final long member) {
        return redisClient.run(jedis -> jedis.zscore( key, String.valueOf(member)), key, "::ZSCORE");
    }

    protected Long zadd(final String key, final long score, final long member) {
        return redisClient.run(jedis -> jedis.zadd(key, score, String.valueOf(member)), key, "::ZADD");
    }

    protected Set<String> zrevrange(final String key, final long start, final long end ) {
        return redisClient.run(jedis -> jedis.zrevrange( key, start, end), key, "::ZREVRANGE");
    }

    protected Long zrem(final String key, final long member) {
        return redisClient.run(jedis -> jedis.zrem(key, String.valueOf(member)), key, "::ZREM");
    }


}
