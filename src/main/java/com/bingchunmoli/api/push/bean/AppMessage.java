package com.bingchunmoli.api.push.bean;

import com.bingchunmoli.api.app.bean.PushTypeEnum;
import com.bingchunmoli.api.push.bean.enums.AppMessageEnum;
import com.bingchunmoli.api.push.bean.enums.PushMessageEnum;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class AppMessage implements Message {
    private AppMessageEnum appMessageEnum;
    private String deviceToken;
    private PushTypeEnum pushType = PushTypeEnum.FCM;
    private String topic;
    private String title;
    private String body;

    @Override
    public String getReceive() {
        return getDeviceToken() == null || getDeviceToken().isEmpty() ? getTopic() : getDeviceToken();
    }

    public void setDefaultTopic() {
        this.topic = "api";
    }

    @Override
    public PushMessageEnum getType() {
        return PushMessageEnum.APP_MESSAGE;
    }
}
