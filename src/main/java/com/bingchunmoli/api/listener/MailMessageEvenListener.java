package com.bingchunmoli.api.listener;

import com.bingchunmoli.api.even.MailMessageEven;
import com.bingchunmoli.api.utils.SendMailUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;


/**
 * @author MoLi
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MailMessageEvenListener implements ApplicationListener<MailMessageEven> {
    private final SendMailUtil sendMailUtil;

    @Async
    @Override
    public void onApplicationEvent(MailMessageEven event) {
        if (log.isDebugEnabled()) {
            log.debug("MailMessageEvenListener.onApplicationEvent: {}", event);
        }
        if (sendMailUtil.sendEvenMail(event)) {
            log.debug("MailMessageEvenListener.onApplicationEvent发送成功");
        }
    }
}
