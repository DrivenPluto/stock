package com.pluto.stock.config;

import com.pluto.stock.utils.IdWorker;
import com.pluto.stock.utils.ParserStockInfoUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * @author pluto
 * @date 7/27/23 20:58
 * @description:
 */
@Configuration
public class CommonConfig {

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
    @Bean
    public IdWorker idWorker(){
        return new IdWorker(2L,1L);
    }

    @Bean
    public ParserStockInfoUtil parserStockInfoUtil(IdWorker idWorker){
        return new ParserStockInfoUtil(idWorker);
    }
}
