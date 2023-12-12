package com.bingchunmoli.api.push.bean;

import com.bingchunmoli.api.push.bean.enums.PushMessageEnum;

/**
 * 消息父类
 */
public interface Message {

    /**
     * 消息的标题
     * @return 标题
     */
    String getTitle();

    /**
     * 消息正文
     * @return 正文
     */
    String getBody();

    /**
     * 消息的接收人
     * @return 接收人
     */
    String getReceive();

    /**
     * 消息的类型
     * @return 类型
     */
    PushMessageEnum getType();
}
