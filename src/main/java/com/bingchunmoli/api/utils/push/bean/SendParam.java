package com.bingchunmoli.api.utils.push.bean;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 发送推送请求的参数信息
 */
@Data
@Accessors(chain = true)
public class SendParam {
    private String method = "GET";
    private String url;
    private Object body = null;
}
