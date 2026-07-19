package com.bingchunmoli.api.push;

import com.bingchunmoli.api.app.bean.PushTypeEnum;
import com.bingchunmoli.api.exception.ApiAppMessageException;
import com.bingchunmoli.api.push.bean.AppMessage;
import com.bingchunmoli.api.push.bean.Message;
import com.bingchunmoli.api.push.bean.enums.PushMessageEnum;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Notification;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.stereotype.Component;


@Slf4j
@Component
@ConditionalOnClass(name = "com.google.firebase.messaging.FirebaseMessaging")
public class PushApp implements Push {

    @Override
    public String send(final Message message) {
        if (!(message instanceof AppMessage appMessage)) {
            throw new ApiAppMessageException("错误的 message 类型");
        }
        final com.google.firebase.messaging.Message.Builder builder = com.google.firebase.messaging.Message.builder()
                .setNotification(Notification.builder()
                        .setTitle(appMessage.getTitle())
                        .setBody(appMessage.getBody())
                        .build());
        if (appMessage.getDeviceToken() != null && !appMessage.getDeviceToken().isBlank()) {
            builder.setToken(appMessage.getDeviceToken());
        } else {
            builder.setTopic(appMessage.getTopic());
        }
        try {
            return FirebaseMessaging.getInstance().send(builder.build());
        } catch (FirebaseMessagingException e) {
            log.error("推送 FCM 失败", e);
            throw new ApiAppMessageException("推送 FCM 失败");
        }
    }

    @Override
    public boolean support(final Message message) {
        if (!PushMessageEnum.APP_MESSAGE.equals(message.getType())) {
            return false;
        }
        if (message instanceof AppMessage appMessage) {
            return appMessage.getTopic() != null || PushTypeEnum.FCM.equals(appMessage.getPushType());
        }
        return false;
    }

    @Override
    public boolean isEnable() {
        return !com.google.firebase.FirebaseApp.getApps().isEmpty();
    }
}
