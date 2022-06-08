package com.bingchunmoli.api.ip.controller;

import cn.hutool.core.io.IoUtil;
import cn.hutool.extra.servlet.ServletUtil;
import com.jthinking.common.util.ip.IPInfo;
import com.jthinking.common.util.ip.IPInfoUtils;
import org.lionsoul.ip2region.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

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
        return ServletUtil.getClientIP(request, (String[]) null);
    }

    /**
     * 获取请求IP的地址
     *
     * @param request servletRequest
     * @return 地址|未知
     * @throws DbMakerConfigException 地址数据库配置异常
     * @throws IOException            内存异常
     */
    @GetMapping("v4Address")
    public String getV4Address(HttpServletRequest request) throws DbMakerConfigException, IOException {
        String ip = ip(request);
        DbSearcher dbSearcher = new DbSearcher(new DbConfig(), IoUtil.readBytes(this.getClass().getClassLoader().getResourceAsStream("ip2region.db")));
        String region;
        if (Util.isIpAddress(ip)) {
            DataBlock dataBlock = dbSearcher.memorySearch(ip);
            region = dataBlock.getRegion();
            return region;
        }
        return "unknown";
    }

    @GetMapping("address")
    public IPInfo getAddress(HttpServletRequest request){
        return IPInfoUtils.getIpInfo(ip(request));
    }
}
