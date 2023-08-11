package com.pluto.stock.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * @author Driven_Pluto
 * @date 8/11/23 23:36
 * @description: 配置HttpClientConfig客户端工具bean
 */
@Configuration
public class HttpClientConfig {
    /**
     * 定义RestTemplate模版bean
     * @return
     */
    @Bean
    public RestTemplate restTemplate(){
        return new RestTemplate();
    }
}
