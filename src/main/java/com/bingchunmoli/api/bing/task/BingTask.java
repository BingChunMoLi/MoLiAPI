package com.bingchunmoli.api.bing.task;

import cn.hutool.core.date.DateUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.bingchunmoli.api.bing.bean.BingImage;
import com.bingchunmoli.api.bing.bean.BingImageVO;
import com.bingchunmoli.api.bing.bean.enums.BingEnum;
import com.bingchunmoli.api.bing.service.IBingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * @author BingChunMoLi
 */
@Slf4j
@Component
public class BingTask {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private IBingService bingService;

    @Scheduled(cron = "* 1 0 * * *")
    public void getBingImage() {
        String cnBingImage = HttpUtil.get("https://www.bing.com/HPImageArchive.aspx?n=1&mkt=$PSCulture&idx=0&ensearch=0&format=js");
        String enBingImage = HttpUtil.get("https://www.bing.com/HPImageArchive.aspx?n=1&mkt=$PSCulture&idx=0&ensearch=1&format=js");
        BingImageVO cnBingImageVO = JSON.parseObject(cnBingImage, BingImageVO.class);
        BingImageVO enBingImageVO = JSON.parseObject(enBingImage, BingImageVO.class);
        BingImage bingImage = new BingImage(cnBingImageVO, enBingImageVO);
        bingService.save(bingImage);
        redisTemplate.opsForValue().set(BingEnum.CNBING.getKey(), cnBingImageVO, 1, TimeUnit.DAYS);
        redisTemplate.opsForValue().set(BingEnum.ENBING.getKey(), enBingImageVO, 1, TimeUnit.DAYS);
        redisTemplate.opsForValue().set(BingEnum.ALLBING.getKey(), bingImage, 1, TimeUnit.DAYS);
        log.info("getBingImage:" + DateUtil.now());
    }
}
