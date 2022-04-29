package com.bingchunmoli.api.ip.controller;

import cn.hutool.extra.servlet.ServletUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * 来源IP
 * @author BingChunMoLi
 */
@RestController
public class IpController {
    /**
     * 请求的IP
     * @param request servletRequest
     * @return  当前客户端IP
     */
    @GetMapping("ip")
    public String ip(HttpServletRequest request) {
        return ServletUtil.getClientIP(request, (String[]) null);
    }

}
