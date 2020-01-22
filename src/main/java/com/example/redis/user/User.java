package com.example.redis.user;

import com.example.redis.client.model.RedisEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;


@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@NoArgsConstructor
public class User extends RedisEntity {

    public User(boolean isDatabaseLoaded) {
        this.isDatabaseLoaded = isDatabaseLoaded;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long userNo;
    
    private String name;

}
