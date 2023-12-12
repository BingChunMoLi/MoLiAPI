package com.bingchunmoli.api.utils;


import cn.hutool.core.util.StrUtil;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * 邮件工具类
 *
 * @author MoLi
 */
@Component
public class SendMailUtil {
    private final JavaMailSender javaMailSender;
    @Getter
    @Value("${spring.mail.username:mailUsername}")
    private String defaultFrom;
    @Value("${spring.mail.defaultTo:mailDefaultTo}")
    private String defaultTo;
    @Value("${spring.mail.enable:false}")
    private boolean enable;

    public SendMailUtil(@Autowired(required = false) JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
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
        if (checkEnable()) {
            return false;
        }
        SimpleMailMessage simpleMail = new SimpleMailMessage();
        simpleMail.setFrom(Optional.ofNullable(from).orElse(defaultFrom));
        simpleMail.setTo(Optional.ofNullable(to).orElse(defaultTo));
        simpleMail.setSubject(title);
        simpleMail.setText(body);
        javaMailSender.send(simpleMail);
        return true;
    }

    /**
     * 发送html格式的邮件
     *
     * @param to      发送人
     * @param title   标题
     * @param content 内容
     */
    public void sendHtmlMail(String to, String title, String content) throws MessagingException {
        if (checkEnable()) {
            return;
        }
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setFrom(defaultFrom);
        helper.setTo(to);
        helper.setSubject(title);
        helper.setText(content, true);
        javaMailSender.send(message);
    }

    public boolean checkEnable(){
        return  !enable || StrUtil.isEmpty(defaultTo);
    }
}
