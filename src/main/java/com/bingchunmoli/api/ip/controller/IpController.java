package com.bingchunmoli.api.ip.controller;

import cn.hutool.extra.servlet.ServletUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * @author BingChunMoLi
 */
@RestController
public class IpController {
    @GetMapping("ip")
    public String ip(HttpServletRequest request) {
        return ServletUtil.getClientIP(request, (String[]) null);
    }

}
