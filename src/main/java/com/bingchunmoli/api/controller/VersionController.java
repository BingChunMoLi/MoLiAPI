package com.bingchunmoli.api.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 版本接口
 * @author MoLi
 */
@RestController
public class VersionController {

    @Value("${moli.version}")
    private String version;

    @GetMapping("version")
    public String getVersion(){
        return version;
    }
}
