package com.bingchunmoli.api.listener;

import com.bingchunmoli.api.bean.Message;
import com.bingchunmoli.api.even.MessageEven;
import com.bingchunmoli.api.exception.ApiMessageException;
import com.bingchunmoli.api.utils.push.Push;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Map;

@Slf4j
@Component
public class MessageEvenListener implements ApplicationListener<MessageEven> {
    private final Collection<Push> pushList;

    public MessageEvenListener(Map<String, Push> map) {
        this.pushList = map.values();
    }

    @Override
    public void onApplicationEvent(@NotNull MessageEven event) {
        Message message = event.getMessage();
        Push push = pushList.stream().filter(v -> v.support(message)).findFirst().orElseThrow(() -> new ApiMessageException("没有找到合适的推送渠道"));
        push.send(message);
    }
}
