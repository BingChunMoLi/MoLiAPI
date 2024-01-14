package com.bingchunmoli.api.push;

import com.bingchunmoli.api.push.bean.AppMessage;
import com.bingchunmoli.api.push.bean.enums.AppMessageEnum;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author moli
 */
@Slf4j
@SpringBootTest
public class PushAppTest {
    @Autowired
    private PushApp pushApp;

    @Test
    void send() {
        String send = pushApp.send(new AppMessage().setTitle("test")
                .setBody("body")
                .setTopic("api")
                .setAppMessageEnum(AppMessageEnum.TOPIC));
        log.debug("PushAppTest.send: {}", send);
    }
}
