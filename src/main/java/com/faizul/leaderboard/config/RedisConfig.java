package com.faizul.leaderboard.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializationContext;

//@Configuration
public class RedisConfig {

//    @Bean
//    public ReactiveRedisTemplate<String,String> reactiveRedisTemplate(ReactiveRedisConnectionFactory connectionFactory){
//        RedisSerializationContext<String,String> serializationContext = RedisSerializationContext.string();
//
//        return new ReactiveRedisTemplate<>(
//                connectionFactory,
//                serializationContext
//        );
//    }
}
