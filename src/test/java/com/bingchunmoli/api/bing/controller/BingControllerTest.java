package com.bingchunmoli.api.bing.controller;

import com.bingchunmoli.api.bing.bean.BingImage;
import com.bingchunmoli.api.bing.bean.BingImageVO;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@Slf4j
@SpringBootTest
public class BingControllerTest {

    @Autowired
    private BingController bingController;

    @Test
    void getBingAllTest() {
        BingImage image = bingController.getAllBingImage().getData();
        log.info("getBingAllTest: {}", image);
    }

    @Test
    void getBingCnTest() {
        BingImageVO image = bingController.cnBingImage().getData();
        log.info("getBingCnTest: {}", image);
    }

    @Test
    void getBingEnTest() {
        BingImageVO image = bingController.enBingImage().getData();
        log.info("getBingEnTest: {}", image);
    }

}
