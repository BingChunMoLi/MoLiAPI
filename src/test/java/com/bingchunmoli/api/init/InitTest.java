package com.bingchunmoli.api.init;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Map;

@Slf4j
@SpringBootTest
public class InitTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private Map<String, InitService> initServiceMap;

    @Test
    void context(){
        for (InitService service:initServiceMap.values()) {
            service.init();
        }
    }
}
