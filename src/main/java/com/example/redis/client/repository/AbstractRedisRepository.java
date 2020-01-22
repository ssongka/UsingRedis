package com.example.redis.client.repository;

import com.example.redis.client.RedisClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public abstract class AbstractRedisRepository<K, T> {


    protected RedisClient redisClient;

    @Autowired
	public AbstractRedisRepository(RedisClient redisClient) {
        this.redisClient = redisClient;
	}

    /**
     * REDIS KEY 를 반환한다
     * @param key
     * @return
     */
	protected abstract String getKey(K key);

    /**
     *  저장될 Entity의 Class를 반환한다
     * @return
     */
	protected abstract Class<T> getEntry();

	/***
	 * 해당 엔트리의 만료 시간(초단위)을 반환한다
	 * -1 일경우 expire 함수를 실행하지 않는다
	 * @return
	 */
	protected abstract int getTTLSec();

    protected boolean existsKey(final K key)  {
        return redisClient.run(jedis -> jedis.exists(getKey(key)), getKey(key), getEntry().getSimpleName() + "::Exists");
    }
}
