package com.bingchunmoli.api.controller;

import cn.hutool.jwt.JWT;
import com.bingchunmoli.api.bean.ResultVO;
import com.bingchunmoli.api.utils.JwtUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author moli
 */
@RestController
public class EmailController {

    @GetMapping("unsubscribe")
    public ResultVO<String> unsubscribe(@RequestParam String to){
        JWT jwt = JwtUtil.parseToken(to);
        //TODO
        return ResultVO.ok("");
    }
}
