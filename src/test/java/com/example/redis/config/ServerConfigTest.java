package com.example.redis.config;

import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@SpringBootTest
class ServerConfigTest {

    @Autowired
    private ServerConfig serverConfig;

    @Autowired
    private KeyValueRepository keyValueRepository;

    @Test
    void getString() {
        String someKey = serverConfig.getString("someKey", "1");
        Assert.assertSame(someKey, "1");

        keyValueRepository.save(new KeyValue( "someKey", "2", true ));

        someKey = serverConfig.getString("someKey", "1");
        Assert.assertEquals("2", someKey);
    }
}