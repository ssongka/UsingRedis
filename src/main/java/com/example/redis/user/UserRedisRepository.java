package com.example.redis.user;

import com.example.redis.client.RedisClient;
import com.example.redis.client.repository.SetOperationRedisRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;


@Repository
public class UserRedisRepository extends SetOperationRedisRepository<Long, User> {

    public UserRedisRepository(@Autowired RedisClient redisClient) {
        super(redisClient);
    }

    @Override
    protected String getKey(Long key) {
        return String.format("User:%d", key);
    }

    @Override
    protected Class<User> getEntry() {
        return User.class;
    }

    @Override
    protected int getTTLSec() {
        return 60 * 5; //5분
    }
}
