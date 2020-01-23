package com.example.redis.client.repository;

import com.example.redis.client.RedisClient;
import com.example.redis.client.model.RedisHashEntity;
import com.example.redis.client.serializer.RedisEntityMapper;
import com.example.redis.util.Misc;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


@Slf4j
public abstract class HashOperationRedisRepository <H, K, V extends RedisHashEntity<?>> extends AbstractRedisRepository<H, V> {

	public HashOperationRedisRepository(RedisClient redisClient) {
		super(redisClient);
	}

	private byte[] serializeEntityKey(K key) throws JsonProcessingException {
        return RedisEntityMapper.getMapper().writeValueAsBytes(key);
	}

	protected abstract Class<K> getKeyClass();

	private K deserializeEntityKey(byte[] key) throws IOException {
		return RedisEntityMapper.getMapper().readValue(key, getKeyClass());
	}

	public Map<K, V> queryForHash(final H h) throws RedisEntityNullException {
		Map<K, V> kvMap = redisClient.run(jedis -> {
            Map<K, V> rtnMap = new HashMap<>();
            Map<byte[], byte[]> hgetAll = jedis.hgetAll(getKey(h).getBytes());
            if (MapUtils.isEmpty(hgetAll)) {
                return null;
            }

            ObjectMapper mapper = RedisEntityMapper.getMapper();

            try {
                for (Map.Entry<byte[], byte[]> entry : hgetAll.entrySet()) {
                    K k = deserializeEntityKey(entry.getKey());
                    V v = mapper.readValue(entry.getValue(), getEntry());

                    rtnMap.put(k, v);
                }
            } catch (Exception e) {
                Misc.stackTrace(e);
                return null;
            }

            return rtnMap;
        }, getKey(h), getEntry().getSimpleName() + "::HashGetAll");

		//데이터가 존재 하지 않는다면 예외를 발생 시킨다
		if( kvMap == null && !existsKey(h)) {
			throw new RedisEntityNullException(String.format("Hash Entity not found.. key=%s", getKey(h)));
		}

		return kvMap;
	}

	public V getHash(final H h, final V hash) {
		return redisClient.run(jedis -> {

            try {
                byte[] field = RedisEntityMapper.getMapper().writeValueAsBytes(hash.getEntityKey());
                byte[] fieldValue = jedis.hget(getKey(h).getBytes(), field);

                return RedisEntityMapper.getMapper().readValue( fieldValue, getEntry());
            }
            catch (Exception e) {
                Misc.stackTrace(e);
                return null;
            }
        },  getKey(h), getEntry().getSimpleName() + "::HashGet");
	}

	public boolean setHash(final H h, final V hash) {
		return redisClient.run(jedis -> {
            try {
                byte[] entityKey = RedisEntityMapper.getMapper().writeValueAsBytes(hash.getEntityKey());
                byte[] entity = RedisEntityMapper.getMapper().writeValueAsBytes(hash);

                Long hset = jedis.hset(getKey(h).getBytes(), entityKey, entity);
                return hset != 0L;
            }
            catch (Exception e) {
                Misc.stackTrace(e);
                return false;
            }
        }, getKey(h), getEntry().getSimpleName() + "::HashSet");
	}

	public boolean removeHash(final H h, final V hash) {
		return redisClient.run(jedis -> {
            try {
                byte[] entityKey = RedisEntityMapper.getMapper().writeValueAsBytes(hash.getEntityKey());
                return jedis.hdel( getKey(h).getBytes(), entityKey) != 0;
            }
            catch (Exception e) {
                Misc.stackTrace(e);
                return false;
            }
        }, getKey(h), getEntry().getSimpleName() + "::HashDel");
	}

	public boolean removeAll(final H h) {
		return redisClient.run(jedis -> {
            jedis.del(getKey(h).getBytes());
            return true;
        },  getKey(h), getEntry().getSimpleName() + "::HashAllDel");
	}

	public boolean setAllHash(final H h, final Map<K, V> hashs) {

		final Map<byte[], byte[]> params = serializeHash(hashs);
		if( Misc.isEmptyMap(params))
			return true;

		return redisClient.run(jedis -> {
            String hmset = jedis.hmset(getKey(h).getBytes(), params);

            int ttlSec = getTTLSec();
            if( 0 < ttlSec)
                   jedis.expire(getKey(h), ttlSec);

            return hmset.equals("OK");
        },getKey(h), getEntry().getSimpleName() + "::HashAllSet");
	}

	private Map<byte[], byte[]> serializeHash(Map<K, V> hashs) {
		Map<byte[], byte[]> params = new HashMap<>();
		for (Map.Entry<K, V> entry : hashs.entrySet()) {

			try {
				byte[] key = serializeEntityKey(entry.getKey());
				byte[] value = RedisEntityMapper.getMapper().writeValueAsBytes(entry.getValue());

				params.put(key, value);
			} catch (Exception e ) {
				Misc.stackTrace(e);
			}
		}
		return params;
	}

}
