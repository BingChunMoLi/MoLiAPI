package com.bingchunmoli.api.ip.controller;

import cn.hutool.extra.servlet.JakartaServletUtil;
import com.jthinking.common.util.ip.IPInfo;
import com.jthinking.common.util.ip.IPInfoUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 来源IP
 * @author BingChunMoLi
 */
@Tag(name = "ip")
@RestController
public class IpController {
    /**
     * 请求的IP
     *
     * @param request servletRequest
     * @return 当前客户端IP
     */
    @GetMapping("ip")
    @Operation(summary = "当前请求的客户端公网IP")
    public String ip(HttpServletRequest request) {
        return JakartaServletUtil.getClientIP(request, (String[]) null);
    }

    /**
     * 获取当前ip的地址
     * @param request 请求
     * @return 地址
     */
    @GetMapping("address")
    @Operation(summary = "根据当前请求的公网ip获取ip的地理位置")
    public IPInfo getAddress(HttpServletRequest request){
        return IPInfoUtils.getIpInfo(ip(request));
    }
}
