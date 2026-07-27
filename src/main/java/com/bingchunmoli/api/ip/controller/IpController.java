package com.bingchunmoli.api.ip.controller;

import com.bingchunmoli.api.bean.ResultVO;
import com.bingchunmoli.api.ip.service.IpService;
import com.jthinking.common.util.ip.IPInfo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 来源IP
 * @author BingChunMoLi
 */
@Tag(name = "ip")
@RestController
@RequiredArgsConstructor
public class IpController {
    private final IpService ipService;

    /**
     * 请求的IP
     *
     * @param request servletRequest
     * @return 当前客户端IP
     */
    @GetMapping("ip")
    @Operation(summary = "当前请求的客户端公网IP")
    public ResultVO<String> ip(final HttpServletRequest request) {
        return ResultVO.ok(ipService.getClientIp(request));
    }

    /**
     * 获取当前ip的地址
     * @param request 请求
     * @return 地址
     */
    @GetMapping("address")
    @Operation(summary = "根据当前请求的公网ip获取ip的地理位置")
    public ResultVO<IPInfo> getAddress(final HttpServletRequest request){
        return ResultVO.ok(ipService.getAddress(request));
    }
}
