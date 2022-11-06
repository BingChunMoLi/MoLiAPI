package com.bingchunmoli.api.bean;

import lombok.Builder;
import lombok.Data;

/**
 * Mail发送实体
 * @author MoLi
 */
@Data
@Builder
public class MailMessage {
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

}
