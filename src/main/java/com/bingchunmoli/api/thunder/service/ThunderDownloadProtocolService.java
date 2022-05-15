package com.bingchunmoli.api.thunder.service;

/**
 * @author MoLi
 */
public interface ThunderDownloadProtocolService {
    /**
     * 转换迅雷下载协议链接至原始链接
     * @param thunderURL 迅雷协议链接|thunder://QUFodHRwOi8vdG9vbC5sdS90ZXN0LnppcFpa
     * @return 原始链接|http://tool.lu/test.zip
     */
    String toRaw(String thunderURL);

    /**
     * 原始协议转换为迅雷协议
     * @param rawURL 原始协议|http://tool.lu/test.zip
     * @return 迅雷协议|thunder://QUFodHRwOi8vdG9vbC5sdS90ZXN0LnppcFpa
     */
    String toThunder(String rawURL);
}
