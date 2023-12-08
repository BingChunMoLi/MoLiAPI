package com.bingchunmoli.api.utils.push;

import com.bingchunmoli.api.bean.MailMessage;
import com.bingchunmoli.api.bean.Message;
import com.bingchunmoli.api.exception.ApiMessageException;
import com.bingchunmoli.api.utils.SendMailUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PushMail implements Push{
    private final SendMailUtil sendMailUtil;

    @Override
    public String send(Message message) {
        if (message instanceof MailMessage mailMessage) {
            return String.valueOf(sendMailUtil.sendMail(mailMessage.getFrom(), mailMessage.getTo(), mailMessage.getTitle(), mailMessage.getBody()));
        }
        throw new ApiMessageException("错误的mailMessage类型");
    }

    @Override
    public boolean support(Message message) {
        return message instanceof MailMessage;
    }

}
