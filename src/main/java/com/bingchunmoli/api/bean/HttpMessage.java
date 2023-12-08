package com.bingchunmoli.api.bean;

import com.bingchunmoli.api.bean.enums.HttpMessageType;
import lombok.Data;

/**
 * @author moli
 */
@Data
public class HttpMessage implements Message {
    private String title;
    private String body;
    private HttpMessageType httpMessageType;
}
