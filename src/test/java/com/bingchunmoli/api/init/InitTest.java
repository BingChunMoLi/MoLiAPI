package com.bingchunmoli.api.init;

import com.bingchunmoli.api.exception.ApiInitException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.ResourceUtils;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.Map;

@Slf4j
@SpringBootTest
public class InitTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void test2() throws IOException, URISyntaxException, SQLException {
        String location = "classpath:db/schema-mysql-yiyan.sql";
        URL url = ResourceUtils.getURL(location);
        Path path = Paths.get(url.toURI());
        String sql = "";
        try {
            sql = Files.readString(path, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new ApiInitException(e);
        }
        System.out.println(sql);
    }
    @Autowired
    private Map<String, InitService> initServiceMap;

    @Test
    void context(){
        for (InitService service:initServiceMap.values()) {
            service.init();
        }
    }
}
