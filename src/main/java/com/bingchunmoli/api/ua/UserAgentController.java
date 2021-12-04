package com.bingchunmoli.api.ua;

import cn.hutool.http.useragent.UserAgentUtil;
import com.bingchunmoli.api.bean.ResultVO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author bingchunmoli
 */
@RestController
@RequestMapping("ua")
public class UserAgentController {

    @GetMapping("userAgentInfo")
    public ResultVO getUserAgentInfo(@RequestHeader("user-agent")String userAgent){
        return new ResultVO(UserAgentUtil.parse(userAgent));
    }
}
