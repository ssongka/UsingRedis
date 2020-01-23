package com.example.redis.client.repository;

import com.example.redis.client.RedisClient;
import com.example.redis.client.model.RedisEntity;
import com.example.redis.client.serializer.RedisEntityMapper;
import com.example.redis.util.Misc;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;


@Slf4j
public abstract class SetOperationRedisRepository<K, T extends RedisEntity> extends AbstractRedisRepository<K, T> {

	public SetOperationRedisRepository(RedisClient redisClient) {
		super(redisClient);
	}

	public T queryForObject(final K k) throws RedisEntityNullException {
        T run = redisClient.run(jedis -> {
            byte[] bytes = jedis.get(getKey(k).getBytes());

            if (Misc.isEmpty(bytes)) {
                log.debug("[Redis][GET][{}] Return Object is Empty.. usn={}, key={} ", getEntry(), k, getKey(k));
                return null;
            }

            try {
                return RedisEntityMapper.getMapper().readValue(bytes, getEntry());
            } catch (IOException e) {
                Misc.stackTrace(e);
                log.error("[Redis][GET][{}] ReadValue fail.. usn={}", getEntry(), k);
                return null;
            }
        }, getKey(k), getEntry().getSimpleName() + "::GET");

        //데이터가 존재 하지 않는다면 예외를 발생 시킨다
        if( run == null && !existsKey(k)) {
            throw new RedisEntityNullException("SetEntity Not Found.. key=" + getKey(k));
        }

        return run;
    }

	public boolean saveObject(final K key, final T t) {
		return redisClient.run(jedis -> {
            try {
                //flag update
                t.setDatabaseLoaded(true);

                byte[] bytes = RedisEntityMapper.getMapper().writeValueAsBytes(t);

                String set = jedis.set(getKey(key).getBytes(), bytes);

                if(!set.equals("OK")) {
                    log.error("[Redis][SAVE] set fail.. rtn={}, usn={}", set, key);
                    return false;
                }

                if( getTTLSec() != -1) {
                    jedis.expire(getKey(key), getTTLSec());
                }

                return true;
            } catch (JsonProcessingException e) {
                Misc.stackTrace(e);
                log.error("[Redis][SAVE][{}] Save Entry fail.. usn={}", getEntry(), key);
                return false;
            }
        }, getKey(key), getEntry().getSimpleName() + "::SAVE");
	}

	public boolean removeObject(final K key) {
		return redisClient.run(jedis -> {
            Long del = jedis.del(getKey(key).getBytes());
            return 0 != del;
        }, getKey(key), getEntry().getSimpleName() + "::DEL");
	}
}
