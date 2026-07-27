package com.bingchunmoli.api.bing.task;

import cn.hutool.core.date.DateUtil;
import com.bingchunmoli.api.bing.bean.BingImage;
import com.bingchunmoli.api.bing.service.BingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.resilience.annotation.Retryable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 定时获取bing图片
 * @author BingChunMoLi
 */
@Slf4j
@Component
@ConditionalOnProperty(prefix = "moli.features.tasks", name = "bing", havingValue = "true")
@RequiredArgsConstructor
public class BingTask {
    private final BingService bingService;

    @Retryable(delay = 5000L, multiplier = 3)
    @Scheduled(cron = "0 0 0 * * ?")
    public void getBingImage() {
        BingImage bingImageByRemote = bingService.getBingImageByRemote();
        log.info("getBingImage: {}, image: {}", DateUtil.now(), bingImageByRemote);
    }
}
