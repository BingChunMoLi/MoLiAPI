package com.bingchunmoli.api.push.bean;

import com.bingchunmoli.api.push.bean.enums.HttpMessageType;
import com.bingchunmoli.api.push.bean.enums.PushMessageEnum;
import lombok.Data;

/**
 * @author moli
 */
@Data
public class HttpMessage implements Message {
    private String title;
    private String body;
    private HttpMessageType httpMessageType;

    @Override
    public String getReceive() {
        return "ME";
    }

    @Override
    public PushMessageEnum getType() {
        return PushMessageEnum.HTTP_MESSAGE;
    }
}
