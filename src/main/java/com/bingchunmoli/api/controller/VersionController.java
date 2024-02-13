package com.bingchunmoli.api.controller;

import com.bingchunmoli.api.bean.ResultVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 版本接口
 * @author MoLi
 */
@RestController
@Tag(name = "版本")
public class VersionController {

    @Value("${moli.version}")
    private String version;

    @GetMapping("version")
    @Operation(summary = "获取当前版本")
    public ResultVO<String> getVersion(){
        return ResultVO.ok(version);
    }

}
