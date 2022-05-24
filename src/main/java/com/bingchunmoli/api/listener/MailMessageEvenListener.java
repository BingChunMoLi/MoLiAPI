package com.bingchunmoli.api.listener;

import com.bingchunmoli.api.even.MailMessageEven;
import com.bingchunmoli.api.utils.SendMailUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;


/**
 * @author MoLi
 */
@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnBean(SendMailUtil.class)
public class MailMessageEvenListener implements ApplicationListener<MailMessageEven> {
    private final SendMailUtil sendMailUtil;
    @Value("${spring.mail.enable}")
    private boolean enable;
    @Async
    @Override
    public void onApplicationEvent(MailMessageEven event) {
        if (log.isDebugEnabled()) {
            log.debug("MailMessageEvenListener.onApplicationEvent: {}", event);
        }
        if (!enable) {
            log.debug("MailMessageEvenListener 未启用发消息");
            return;
        }
        if (sendMailUtil.sendEvenMail(event)) {
            log.debug("MailMessageEvenListener.onApplicationEvent发送成功");
        }
    }
}
