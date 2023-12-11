package com.bingchunmoli.api;

import com.bingchunmoli.api.push.bean.AppMessage;
import com.bingchunmoli.api.push.bean.enums.AppMessageEnum;
import com.bingchunmoli.api.push.PushApp;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author moli
 */
@SpringBootTest
public class PushAppTest {
    @Autowired
    private PushApp pushApp;
    @Test
    void test(){
        String send = pushApp.send(new AppMessage().setTitle("test")
                .setBody("body")
                .setTopic("api")
                .setAppMessageEnum(AppMessageEnum.TOPIC));
        System.out.println(send);
    }
}
