package com.bingchunmoli.api.push;


import com.bingchunmoli.api.push.bean.Message;

public interface Push {

    /**
     * 发送消息
     * @param message 消息
     * @return 结果
     */
    String send(Message message);

    /**
     * 是否支持该消息的处理
     * @param message 消息
     * @return true即为支持
     */
    boolean support(Message message);

    /**
     * 当前推送渠道是否已开启并配置正确
     * @return true即为已开启并配置正确
     */
    default boolean isEnable() {
        return true;
    }
}