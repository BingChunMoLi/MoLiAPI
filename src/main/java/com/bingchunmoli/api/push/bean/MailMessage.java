package com.bingchunmoli.api.push.bean;

import com.bingchunmoli.api.push.bean.enums.PushMessageEnum;
import lombok.Builder;
import lombok.Data;

/**
 * Mail发送实体
 * @author MoLi
 */
@Data
@Builder
public class MailMessage implements Message{
    /**
     * 来自
     */
    private String from;
    /**
     * 发送至
     */
    private String to;
    /**
     * 标题
     */
    private String title;
    /**
     * 正文
     */
    private String body;

    @Override
    public String getReceive() {
        return to;
    }

    @Override
    public PushMessageEnum getType() {
        return PushMessageEnum.MAIL_MESSAGE;
    }
}
