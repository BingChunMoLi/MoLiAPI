package com.bingchunmoli.api;

import com.bingchunmoli.api.yiyan.mapper.YiYanMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ApiApplicationTests {

    @Autowired
    YiYanMapper yiYanMapper;

    @Test
    void contextLoads() {
    }

}
