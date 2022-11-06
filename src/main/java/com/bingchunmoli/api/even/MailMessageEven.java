package com.bingchunmoli.api.even;

import com.bingchunmoli.api.bean.MailMessage;
import org.springframework.context.ApplicationEvent;

/**
 * 邮件消息事件
 * @author MoLi
 */
public class MailMessageEven extends ApplicationEvent {
    public MailMessageEven(MailMessage mailMessage) {
        super(mailMessage);
    }
}
