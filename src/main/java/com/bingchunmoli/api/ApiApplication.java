package com.bingchunmoli.api;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Indexed;

/**
 * @author BingChunMoLi
 */
@Indexed
@EnableAsync
@EnableRetry
@EnableCaching
@EnableScheduling
@EnableAspectJAutoProxy(exposeProxy = true)
@SpringBootApplication
@MapperScan("com.bingchunmoli.**.mapper")
public class ApiApplication {

    public static void main(String[] args) {
        System.setProperty("spring.config.additional-location","optional:file:/soft/.api/config/");
        SpringApplication.run(ApiApplication.class, args);
    }

}
