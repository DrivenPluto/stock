package com.pluto.stock;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * @author pluto
 * @date 7/28/23 22:53
 * @description:
 */
@SpringBootTest
public class TestRedisTemplate {

    @Autowired
    private RedisTemplate redisTemplate;

    @Test
    public void test(){
        redisTemplate.opsForValue().set("name","zhangsna");
        Object name = redisTemplate.opsForValue().get("name");
        System.out.println(name);
    }
}
