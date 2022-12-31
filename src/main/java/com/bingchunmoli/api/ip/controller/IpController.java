package com.bingchunmoli.api.ip.controller;

import cn.hutool.extra.servlet.JakartaServletUtil;
import com.jthinking.common.util.ip.IPInfo;
import com.jthinking.common.util.ip.IPInfoUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 来源IP
 * @author BingChunMoLi
 */
@RestController
public class IpController {
    /**
     * 请求的IP
     *
     * @param request servletRequest
     * @return 当前客户端IP
     */
    @GetMapping("ip")
    public String ip(HttpServletRequest request) {
        return JakartaServletUtil.getClientIP(request, (String[]) null);
    }

    /**
     * 获取当前ip的地址
     * @param request 请求
     * @return 地址
     */
    @GetMapping("address")
    public IPInfo getAddress(HttpServletRequest request){
        return IPInfoUtils.getIpInfo(ip(request));
    }
}
