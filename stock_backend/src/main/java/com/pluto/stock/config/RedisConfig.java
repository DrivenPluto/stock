package com.pluto.stock.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * @author pluto
 * @date 7/28/23 22:37
 * @description:
 */
@Configuration
public class RedisConfig {
    @Bean
    public RedisTemplate<Object,Object> redisTemplate(RedisConnectionFactory redisConnectionFactory){
        RedisTemplate<Object, Object> template = new RedisTemplate();
        template.setConnectionFactory(redisConnectionFactory);
        //设置redis中key的序列化
        template.setKeySerializer(new StringRedisSerializer());
        //设置hash中的redis序列化
        template.setHashKeySerializer(new StringRedisSerializer());
        return template;
    }
}
