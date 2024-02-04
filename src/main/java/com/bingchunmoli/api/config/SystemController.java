package com.bingchunmoli.api.config;

import com.bingchunmoli.api.bean.ResultVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/system")
public class SystemController {
    private final ApiConfig apiConfig;

    /**
     * 查询当前的配置项
     *
     * @return 配置项
     */
    @GetMapping
    public ResultVO<ApiConfig> systemConfig() {
        return ResultVO.ok(apiConfig);
    }

    /**
     * 直接修改配置,功能待定
     *
     * @param apiConfig
     * @return null
     */
    @PostMapping
    public ResultVO<Boolean> updateSystemConfig(@RequestBody ApiConfig apiConfig) {
        return ResultVO.ok(null);
    }
}
