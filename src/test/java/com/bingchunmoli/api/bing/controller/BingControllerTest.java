package com.bingchunmoli.api.bing.controller;

import com.bingchunmoli.api.bing.bean.BingImage;
import com.bingchunmoli.api.bing.bean.BingImageVO;
import com.fasterxml.jackson.core.JsonProcessingException;
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
    void getBingAllTest() throws JsonProcessingException {
        BingImage image = bingController.getAllBingImage();
        log.info("getBingAllTest: {}", image);
    }

    @Test
    void getBingCnTest() throws JsonProcessingException {
        BingImageVO image = bingController.cnBingImage();
        log.info("getBingCnTest: {}", image);
    }

    @Test
    void getBingEnTest() throws JsonProcessingException {
        BingImageVO image = bingController.enBingImage();
        log.info("getBingEnTest: {}", image);
    }

}
