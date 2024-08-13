package com.bingchunmoli.api.utils;


import cn.hutool.core.util.StrUtil;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.HashMap;
import java.util.Optional;

/**
 * 邮件工具类
 *
 * @author MoLi
 */
@Slf4j
@Component
public class SendMailUtil {
    private final JavaMailSender javaMailSender;
    private final TemplateEngine templateEngine;

    @Getter
    @Value("${spring.mail.username:mailUsername}")
    private String defaultFrom;
    @Value("${spring.mail.defaultTo:mailDefaultTo}")
    private String defaultTo;
    @Value("${spring.mail.enable:false}")
    private boolean enable;

    public SendMailUtil(@Autowired(required = false) final JavaMailSender javaMailSender, final TemplateEngine templateEngine) {
        this.javaMailSender = javaMailSender;
        this.templateEngine = templateEngine;
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

    public void sendHtmlMail(String to, String subject, String template, Context context) throws MessagingException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        HashMap<String, Object> payload = new HashMap<>();
        payload.put("to", to);
        String unsubscribe = JwtUtil.createToken(payload);
        context.setVariable("unsubscribe", unsubscribe);
        mimeMessage.setHeader("List-Unsubscribe-Post", "List-Unsubscribe=One-Click");
        mimeMessage.setHeader("List-Unsubscribe", "https://api.bingchunmoli.com/unsubscribe/?to=" + unsubscribe);
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
        String htmlContent = templateEngine.process(template, context);
        helper.setTo(to);
        helper.setFrom(defaultFrom);
        helper.setSubject(subject);
        helper.setText(htmlContent, true);
        javaMailSender.send(mimeMessage);
    }

    public boolean checkEnable(){
        return  !enable || StrUtil.isEmpty(defaultTo);
    }
}
