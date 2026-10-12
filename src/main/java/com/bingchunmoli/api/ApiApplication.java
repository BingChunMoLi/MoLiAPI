package com.bingchunmoli.api;

import com.bingchunmoli.api.config.NativeRuntimeHints;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.ImportRuntimeHints;
import org.springframework.resilience.annotation.EnableResilientMethods;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Indexed;

/**
 * @author BingChunMoLi
 */
@Indexed
@EnableAsync
@EnableResilientMethods
@EnableAspectJAutoProxy(exposeProxy = true)
@SpringBootApplication
@ImportRuntimeHints(NativeRuntimeHints.class)
@MapperScan(basePackages = "com.bingchunmoli.**.mapper", sqlSessionTemplateRef = "sqlSessionTemplate")
public class ApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApiApplication.class, args);
    }

}
