package com.bingchunmoli.api;

import com.bingchunmoli.api.properties.ApiConfig;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
@SpringBootTest
class ApiApplicationTests {
    @Autowired
    private ApiConfig apiConfig;

    @BeforeEach
    void before() {
        if (log.isDebugEnabled()) {
            log.debug("apiKeyProperties: {}", apiConfig);
        }
    }

    @Value("${moli.path}")
    private String initPath;

    @BeforeEach
    void init() throws IOException {
        Path path = Paths.get(initPath);
        if (!path.toFile().exists()) {
            Files.createDirectories(path);
        }
    }

    @Test
    void contextLoads() {
    }

}
