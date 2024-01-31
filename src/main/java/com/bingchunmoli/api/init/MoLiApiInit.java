package com.bingchunmoli.api.init;

import com.bingchunmoli.api.even.MessageEven;
import com.bingchunmoli.api.push.bean.AppMessage;
import com.bingchunmoli.api.push.bean.enums.AppMessageEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Comparator;
import java.util.Map;

/**
 * 一言，诗词，随机图初始化
 * @author BingChunMoLi
 */
@Slf4j
@Component
@Order(Integer.MIN_VALUE)
@RequiredArgsConstructor
public class MoLiApiInit implements CommandLineRunner {
    private final ApplicationEventPublisher applicationEventPublisher;
    private final Map<String, InitService> initServiceMap;

    @Override
    public void run(String... args) {
        Collection<InitService> initServices = initServiceMap.values().stream().sorted(Comparator.comparingInt(InitService::getOrder)).toList();
        for (InitService service : initServices) {
            service.init();
        }
        applicationEventPublisher.publishEvent(new MessageEven(this, new AppMessage().setAppMessageEnum(AppMessageEnum.TOPIC).setTopic("api").setTitle("系统初始化完成").setBody("系统初始化完成, 初始化当前时间:" + LocalDateTime.now())));
    }
}
