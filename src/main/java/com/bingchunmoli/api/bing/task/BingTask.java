package com.bingchunmoli.api.bing.task;

import cn.hutool.core.date.DateUtil;
import com.bingchunmoli.api.bean.MailMessage;
import com.bingchunmoli.api.bing.bean.BingImage;
import com.bingchunmoli.api.bing.service.BingService;
import com.bingchunmoli.api.even.MessageEven;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 定时获取bing图片
 * @author BingChunMoLi
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class BingTask {
    private final BingService bingService;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Retryable(retryFor = Exception.class, backoff = @Backoff(delay = 5000L, multiplier = 3))
    @Scheduled(cron = "0 0 0 * * ?")
    public void getBingImage() {
        BingImage bingImageByRemote = bingService.getBingImageByRemote();
        log.info("getBingImage: {}, image: {}", DateUtil.now(), bingImageByRemote);
    }

    @Recover
    public void logBingImageError(Exception e) {
        log.error("获取bing图片异常, data: " + DateUtil.now() + "\n", e);
            applicationEventPublisher.publishEvent(new MessageEven(this, MailMessage.builder()
                    .title("和获取bing图片异常")
                    .body(e.toString()).build()));
    }
}
