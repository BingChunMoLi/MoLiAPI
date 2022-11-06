package com.bingchunmoli.api.thunder.bean;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author MoLi
 */
@Getter
@AllArgsConstructor
public enum DownloadProtocolConstant {
    /**
     * 迅雷下载协议头
     */
    THUNDER("thunder://");
    /**
     * 协议
     */
    private final String protocol;
}
