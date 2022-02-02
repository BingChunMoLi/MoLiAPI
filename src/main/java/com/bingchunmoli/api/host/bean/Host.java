package com.bingchunmoli.api.host.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author BingChunMoLi
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Host {
    private Integer id;
    private String ip;
    private String domain;
    private String source;

}
