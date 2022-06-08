package com.bingchunmoli.api;

import com.bingchunmoli.api.properties.ApiKeyProperties;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
@Slf4j
@SpringBootTest
class ApiApplicationTests {
    @Autowired
    private ApiKeyProperties apiKeyProperties;

    @BeforeEach
    void before(){
        if (log.isDebugEnabled()) {
            log.debug("apiKeyProperties: {}", apiKeyProperties);
        }
    }

    @Test
    void contextLoads() {
    }

}
