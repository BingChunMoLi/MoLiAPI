package com.bingchunmoli.api.thunder.controller;

import cn.hutool.core.util.StrUtil;
import com.bingchunmoli.api.exception.ApiParamException;
import com.bingchunmoli.api.thunder.bean.DownloadProtocolConstant;
import com.bingchunmoli.api.thunder.service.ThunderDownloadProtocolService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author MoLi
 */
@RestController
@RequestMapping("thunder")
@RequiredArgsConstructor
public class ThunderDownloadProtocolController {
    private final ThunderDownloadProtocolService thunderDownloadProtocolService;

    /**
     * 转换迅雷下载协议链接至原始链接
     * @param thunderURL 迅雷协议链接|thunder://QUFodHRwOi8vdG9vbC5sdS90ZXN0LnppcFpa
     * @return 原始链接|http://tool.lu/test.zip
     */
    @GetMapping("toRaw")
    public String toRaw(String thunderURL){
        if (StrUtil.isBlank(thunderURL)) {
            throw new ApiParamException("请求参数为空");
        }
        if (!(thunderURL.startsWith(DownloadProtocolConstant.THUNDER.getProtocol()))) {
            throw new ApiParamException("请求参数错误，迅雷链接不正确, 识别的迅雷链接为:thunder://");
        }
        return thunderDownloadProtocolService.toRaw(thunderURL);
    }

    /**
     * 原始协议转换为迅雷协议
     * @param rawURL 原始协议|http://tool.lu/test.zip
     * @return 迅雷协议|thunder://QUFodHRwOi8vdG9vbC5sdS90ZXN0LnppcFpa
     */
    @GetMapping("toThunder")
    public String toThunder(String rawURL){
        if (StrUtil.isBlank(rawURL)) {
            throw new ApiParamException("请求参数为空");
        }
        return thunderDownloadProtocolService.toThunder(rawURL);
    }
}
