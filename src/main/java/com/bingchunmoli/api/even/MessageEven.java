package com.bingchunmoli.api.even;

import com.bingchunmoli.api.push.bean.Message;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class MessageEven extends ApplicationEvent {
    private final Message message;

    public MessageEven(Object source, Message message) {
        super(source);
        this.message = message;
    }
}
