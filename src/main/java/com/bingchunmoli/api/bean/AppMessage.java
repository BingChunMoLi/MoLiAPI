package com.bingchunmoli.api.bean;

import com.bingchunmoli.api.bean.enums.AppMessageEnum;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class AppMessage implements Message{
    private AppMessageEnum appMessageEnum;
    private String deviceToken;
    private String topic = "api";
    private String title;
    private String body;
}
