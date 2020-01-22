package com.example.redis.config;

import org.springframework.data.repository.CrudRepository;


public interface KeyValueRepository extends CrudRepository<KeyValue, String> {
}
