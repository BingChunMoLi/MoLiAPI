package com.bingchunmoli.api.ua;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.useragent.UserAgent;
import cn.hutool.http.useragent.UserAgentUtil;
import org.springframework.web.bind.annotation.*;

/**
 * @author bingchunmoli
 */
@RestController
@RequestMapping("ua")
public class UserAgentController {

    @GetMapping("userAgentInfo")
    public UserAgent getUserAgentInfo(@RequestHeader("user-agent")String userAgent){
        return UserAgentUtil.parse(userAgent);
    }

    @GetMapping("userAgentInfoByParam")
    public UserAgent getUserAgentInfoByParam(@RequestParam String userAgent){
        return UserAgentUtil.parse(userAgent);
    }

    @GetMapping("userAgent")
    public UserAgent getUserAgent(String userAgent, @RequestHeader("user-agent") String userAgentByHeader){
        if (StrUtil.isBlank(userAgent)) {
            return UserAgentUtil.parse(userAgent);
        }
        return UserAgentUtil.parse(userAgentByHeader);
    }

}
