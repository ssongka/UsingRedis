package com.example.redis.client;

import com.example.redis.config.ServerConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;


@Slf4j
@Component
public class RedisClient {

    private final JedisPool jedisPool;
    private final ServerConfig serverConfig;

    public RedisClient(JedisPool jedisPool, ServerConfig serverConfig) {
        this.jedisPool = jedisPool;
        this.serverConfig = serverConfig;

        testOnConnection();
    }

    private void testOnConnection() {
        try (Jedis resource = this.jedisPool.getResource()) {
            String ping = resource.ping();
            assert ping.equalsIgnoreCase("PONG");
        }
    }

    public <T> T run(final RedisCallback<T> callback, final String key, final String tag) {
        boolean bOk = false;
        T result = null;
        String exceptionMsg = null;
        int retryCount = getRedisRetryCount();

        for (int i = 0; i < retryCount && !bOk; i++) {
            try(Jedis resource = jedisPool.getResource()) {
                if(log.isDebugEnabled()) {
                    log.debug("[Redis] key={}, tag={}", key, tag);
                }
                //콜백 호출
                result = callback.execute(resource);
                bOk = true;
            }
            catch (Exception e) {  //강제 retry시도
                exceptionMsg = e.getMessage();
                try { Thread.sleep(100); } catch (InterruptedException ignored) { }
            }
        }

        if(!bOk){
            log.error("[RedisClient] Retry run fail.. e={}, key={}, tag={}"
                    , exceptionMsg, key, tag);
            throw new RedisRetryFailException("redis-fail");
        }

        return result;
    }

    private int getRedisRetryCount() {
        return serverConfig.getInt("REDIS_RETRY_COUNT", 2);
    }
}

class RedisRetryFailException extends RuntimeException {
    public RedisRetryFailException(String message) {
        super(message);
    }
}
