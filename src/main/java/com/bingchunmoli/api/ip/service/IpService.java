package com.bingchunmoli.api.ip.service;

import cn.hutool.extra.servlet.JakartaServletUtil;
import com.jthinking.common.util.ip.IPInfo;
import com.jthinking.common.util.ip.IPInfoUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

@Service
public class IpService {

    public String getClientIp(final HttpServletRequest request) {
        return JakartaServletUtil.getClientIP(request, (String[]) null);
    }

    public IPInfo getAddress(final HttpServletRequest request) {
        return IPInfoUtils.getIpInfo(getClientIp(request));
    }
}
