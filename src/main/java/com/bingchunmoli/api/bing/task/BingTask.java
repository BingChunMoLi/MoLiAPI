package com.bingchunmoli.api.bing.task;

import cn.hutool.core.date.DateUtil;
import com.bingchunmoli.api.bing.bean.BingImage;
import com.bingchunmoli.api.bing.service.IBingService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @author BingChunMoLi
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class BingTask {
    private final RedisTemplate<String, Object> redisTemplate;
    private final IBingService bingService;

    @Retryable(value = Exception.class, backoff = @Backoff(delay = 5000L, multiplier = 3))
    @Scheduled(cron = "0 0 0 * * ?")
    public void getBingImage() throws JsonProcessingException {
        BingImage bingImageByRemote = bingService.getBingImageByRemote();
        log.info("getBingImage: {}, image: {}", DateUtil.now(), bingImageByRemote);
    }

    @Recover
    public void logBingImageError(Exception e) {
        log.error("获取bing图片异常, data: " + DateUtil.now() + "\n", e);
    }
}
