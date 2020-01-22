package com.example.redis.user;

import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.jupiter.api.Assertions.*;


@RunWith(SpringRunner.class)
@SpringBootTest
class UserServiceTest {

    @Autowired
    private UserService userService;

    private User user;

    @Before
    public void makeDummyUser() {

    }

    @Test
    void findById() {
        user = new User();
        user.setName("더미유저");
        boolean save = userService.save(user);

        assertTrue(save);

        long userNo = user.getUserNo();

        User findUser = userService.findByNo(userNo);
        
        assertEquals(findUser, user);
    }

}