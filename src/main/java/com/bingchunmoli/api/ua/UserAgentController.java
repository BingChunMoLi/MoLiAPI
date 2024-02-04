package com.bingchunmoli.api.ua;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.useragent.UserAgent;
import cn.hutool.http.useragent.UserAgentUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * UserAgent
 * @author bingchunmoli
 */
@RestController
@RequestMapping("ua")
public class UserAgentController {

    /**
     * 从请求头中获取user-agent
     * @param userAgent user-agent
     * @return UserAgent
     */
    @GetMapping("userAgentInfo")
    public UserAgent getUserAgentInfo(@RequestHeader("user-agent") String userAgent) {
        return UserAgentUtil.parse(userAgent);
    }

    /**
     * 从请求参数中获取 userAgent
     * @param userAgent userAgent
     * @return userAgent
     */
    @GetMapping("userAgentInfoByParam")
    public UserAgent getUserAgentInfoByParam(@RequestParam String userAgent) {
        return UserAgentUtil.parse(userAgent);
    }

    /**
     * 从请求参数中获取，获取不到就获取请求头中的userAgent
     * @param userAgent 请求参数的userAgent
     * @param userAgentByHeader 请求头的userAgent
     * @return 优先请求参数的userAgent
     */
    @GetMapping("userAgent")
    public UserAgent getUserAgent(String userAgent, @RequestHeader("user-agent") String userAgentByHeader) {
        if (StrUtil.isBlank(userAgent)) {
            return UserAgentUtil.parse(userAgent);
        }
        return UserAgentUtil.parse(userAgentByHeader);
    }

}
