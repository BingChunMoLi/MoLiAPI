package com.bingchunmoli.api.push.bean;

import com.bingchunmoli.api.push.bean.enums.AppMessageEnum;
import com.bingchunmoli.api.push.bean.enums.PushMessageEnum;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class AppMessage implements Message{
    private AppMessageEnum appMessageEnum;
    private String deviceToken;
    private String topic = "api";
    private String title;
    private String body;

    @Override
    public String getReceive() {
        return getTopic() == null || getTopic().isEmpty() ? getDeviceToken() : getTopic();
    }

    @Override
    public PushMessageEnum getType() {
        return PushMessageEnum.APP_MESSAGE;
    }
}
