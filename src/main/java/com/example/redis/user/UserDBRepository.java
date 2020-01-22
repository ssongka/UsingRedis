package com.example.redis.user;

import org.springframework.data.repository.CrudRepository;


public interface UserDBRepository extends CrudRepository<User, Long> {

}
