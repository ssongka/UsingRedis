package com.example.redis.client;

import com.example.redis.config.ServerConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;


@Slf4j
@Component
public class RedisClient {
    final int RETRY_COUNT = 2;
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
        Jedis resource = null;
        boolean bOk = false;
        T result = null;
        String exceptionMsg = null;

        for (int i = 0; i < RETRY_COUNT && !bOk; i++) {
            try {
                resource = jedisPool.getResource();

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
            } finally {
                if( resource != null) {
                    resource.close();
                }
            }
        }

        if(!bOk){
            log.error("[RedisClient] Retry run fail.. e={}, key={}, tag={}"
                    , exceptionMsg, key, tag);
            throw new RedisRetryFailException("redis-fail");
        }

        return result;
    }
}

class RedisRetryFailException extends RuntimeException {
    public RedisRetryFailException(String message) {
        super(message);
    }
}
