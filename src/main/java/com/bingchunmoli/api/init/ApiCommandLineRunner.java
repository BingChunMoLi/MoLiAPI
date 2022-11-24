package com.bingchunmoli.api.init;

import com.bingchunmoli.api.bean.MailMessage;
import com.bingchunmoli.api.even.MailMessageEven;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 一言，诗词，随机图初始化 仅限prod环境
 * @author BingChunMoLi
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ApiCommandLineRunner implements CommandLineRunner {
    private final ApplicationEventPublisher applicationEventPublisher;
    private final Map<String, InitService> initServiceMap;

    @Override
    public void run(String... args) {
        for (InitService service:initServiceMap.values()) {
            service.init();
        }
        applicationEventPublisher.publishEvent(new MailMessageEven(MailMessage.builder().title("系统初始化完成").body("系统初始化完成, 初始化当前时间:" + LocalDateTime.now()).build()));
    }
}
