package com.bingchunmoli.api.push.bean.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

/**
 * @author moli
 */
@Getter
@AllArgsConstructor
public enum PushMessageEnum implements Serializable{
    //邮件消息
    MAIL_MESSAGE(1, "mailMessage"),
    //app消息
    APP_MESSAGE(2, "appMessage"),
    //http消息
    HTTP_MESSAGE(3, "httpMessage");

    /**
     * type
     */
    private final Integer type;
    /**
     * 友好名字
     */
    private final String name;
}
