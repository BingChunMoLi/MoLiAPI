package com.bingchunmoli.api.utils;


import com.bingchunmoli.api.bean.MailMessage;
import com.bingchunmoli.api.even.MailMessageEven;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;

/**
 * 邮件工具类
 *
 * @author MoLi
 */
@Component
@RequiredArgsConstructor
public class SendMailUtil {
    private final JavaMailSender javaMailSender;
    @Value("${spring.mail.username}")
    private String defaultFrom;
    @Value("${spring.mail.defaultTo}")
    private String defaultTo;


    /**
     * 根据事件源发送邮件
     *
     * @param mailMessageEven mailMessageEven事件含有MailMessage实体
     * @return 是否成功
     */
    public boolean sendEvenMail(MailMessageEven mailMessageEven) {
        MailMessage message = (MailMessage) mailMessageEven.getSource();
        Optional<String> fromOptional = Optional.ofNullable(message.getFrom());
        Optional<String> toOptional = Optional.ofNullable(message.getTo());
        String from = fromOptional.orElse(defaultFrom);
        String to = toOptional.orElse(defaultTo);
        return sendMail(from, to, message.getTitle(), message.getBody() + "\n\n\t\t 触发时间:" + LocalDateTime.ofInstant(Instant.ofEpochMilli(mailMessageEven.getTimestamp()), ZoneId.systemDefault()));
    }


    /**
     * 发送默认defaultFrom和默认defaultTo
     *
     * @param title 标题
     * @param body  正文
     * @return 是否成功
     */
    public boolean sendMail(String title, String body) {
        return sendMail(defaultFrom, defaultTo, title, body);
    }


    /**
     * @param from  来源邮箱，一般为了防止伪造需要和登录邮箱相同
     * @param to    发送到的邮箱
     * @param title 标题
     * @param body  内容
     * @return 是否成功
     */
    public boolean sendMail(String from, String to, String title, String body) {
        SimpleMailMessage simpleMail = new SimpleMailMessage();
        simpleMail.setFrom(from);
        simpleMail.setTo(to);
        simpleMail.setSubject(title);
        simpleMail.setText(body);
        javaMailSender.send(simpleMail);
        return true;
    }
}
