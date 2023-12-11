package com.bingchunmoli.api.push;


import com.bingchunmoli.api.push.Push;
import com.bingchunmoli.api.push.bean.AppMessage;
import com.bingchunmoli.api.push.bean.Message;
import com.bingchunmoli.api.push.bean.enums.PushMessageEnum;
import com.bingchunmoli.api.exception.ApiAppMessageException;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Notification;
import org.springframework.stereotype.Component;

@Component
public class PushApp implements Push {

    @Override
    public String send(Message message) {
        if (message instanceof AppMessage appMessage) {
            com.google.firebase.messaging.Message fcmMessage = com.google.firebase.messaging.Message.builder()
                    .setNotification(Notification.builder()
                            .setTitle(appMessage.getTitle())
                            .setBody(appMessage.getBody())
                            .build())
                    .setToken(appMessage.getDeviceToken())
                    .setTopic(appMessage.getTopic())
                    .build();
            try {
                return FirebaseMessaging.getInstance().send(fcmMessage);
            } catch (FirebaseMessagingException e) {
                throw new RuntimeException(e);
            }
        }
        throw new ApiAppMessageException("错误的message类型. ");
    }

    @Override
    public boolean support(Message message) {
        return PushMessageEnum.APP_MESSAGE.equals(message.getType());
    }
}
