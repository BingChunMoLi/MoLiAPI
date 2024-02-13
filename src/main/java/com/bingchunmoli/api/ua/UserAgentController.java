package com.bingchunmoli.api.ua;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.useragent.UserAgent;
import cn.hutool.http.useragent.UserAgentUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "userAgent")
@RequestMapping("ua")
public class UserAgentController {

    /**
     * 从请求头中获取user-agent
     * @param userAgent user-agent
     * @return UserAgent
     */
    @GetMapping("userAgentInfo")
    @Operation(summary = "获取当前请求头中的user-agent并解析")
    public UserAgent getUserAgentInfo(@RequestHeader("user-agent") String userAgent) {
        return UserAgentUtil.parse(userAgent);
    }

    /**
     * 从请求参数中获取 userAgent
     * @param userAgent userAgent
     * @return userAgent
     */
    @GetMapping("userAgentInfoByParam")
    @Operation(summary = "指定userAgent的参数进行解析")
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
    @Operation(summary = "自动识别,如果请求参数不为空使用请求参数的useragent否则使用请求头")
    public UserAgent getUserAgent(String userAgent, @RequestHeader("user-agent") String userAgentByHeader) {
        if (StrUtil.isBlank(userAgent)) {
            return UserAgentUtil.parse(userAgent);
        }
        return UserAgentUtil.parse(userAgentByHeader);
    }

}
