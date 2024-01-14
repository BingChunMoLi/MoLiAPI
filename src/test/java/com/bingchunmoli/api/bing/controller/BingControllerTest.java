package com.bingchunmoli.api.bing.controller;

import com.bingchunmoli.api.bing.bean.BingImage;
import com.bingchunmoli.api.bing.bean.BingImageVO;
import com.bingchunmoli.api.bing.bean.enums.BingEnum;
import com.bingchunmoli.api.bing.service.BingService;
import com.bingchunmoli.api.utils.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.TimeUnit;

@Slf4j
@SpringBootTest
public class BingControllerTest {

    @Autowired
    private BingController bingController;

    @Autowired
    private BingService bingService;
    @Autowired
    private RedisUtil redisUtil;

    @BeforeEach
    void beforeGetBing() {
        BingImageVO cnBingImageVO = bingService.getBingImageByRemote(BingEnum.CN_BING);
        BingImageVO enBingImageVO = bingService.getBingImageByRemote(BingEnum.EN_BING);
        BingImage bingImage = new BingImage(cnBingImageVO, enBingImageVO);
        redisUtil.setObject(BingEnum.CN_BING.getKey(), cnBingImageVO, 1, TimeUnit.DAYS);
        redisUtil.setObject(BingEnum.EN_BING.getKey(), enBingImageVO, 1, TimeUnit.DAYS);
        redisUtil.setObject(BingEnum.ALL_BING.getKey(), bingImage, 1, TimeUnit.DAYS);
    }

    @AfterEach
    void after(){
        redisUtil.deleteObject(BingEnum.CN_BING.getKey());
        redisUtil.deleteObject(BingEnum.EN_BING.getKey());
        redisUtil.deleteObject(BingEnum.ALL_BING.getKey());
    }

    @Test
    void getBingAllTest() {
        BingImage image = bingController.getAllBingImage().getData();
        log.debug("getBingAllTest: {}", image);
    }

    @Test
    void getBingCnTest() {
        BingImageVO image = bingController.cnBingImage().getData();
        log.debug("getBingCnTest: {}", image);
    }

    @Test
    void getBingEnTest() {
        BingImageVO image = bingController.enBingImage().getData();
        log.debug("getBingEnTest: {}", image);
    }

}
