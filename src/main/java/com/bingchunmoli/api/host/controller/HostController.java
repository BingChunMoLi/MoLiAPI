package com.bingchunmoli.api.host.controller;

import com.bingchunmoli.api.bean.ResultVO;
import com.bingchunmoli.api.controller.advice.RawResponse;
import com.bingchunmoli.api.host.service.HostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

/**
 * hosts订阅
 * @author BingChunMoLi
 */
@RestController
@Tag(name = "去广告host订阅,推荐配合switchHost")
@RequiredArgsConstructor
@RequestMapping("host")
public class HostController {

    private final HostService hostService;

    /**
     * 根据参数获取hosts
     * @param type 请求类型|0
     * @return json的Host序列化
     */
    @GetMapping("json")
    @Operation(summary = "根据type获取hosts")
    public ResultVO<String> getHosts(@RequestParam(defaultValue = "0") final ArrayList<Integer> type) {
        return ResultVO.ok(hostService.getHosts(type));
    }

    /**
     * raw的host
     * @param type 请求类型|0
     * @return hosts文本
     */
    @RawResponse
    @GetMapping(value = "raw", produces = MediaType.TEXT_PLAIN_VALUE)
    @Operation(summary = "在线预览的host")
    public ResponseEntity<String> getRaw(@RequestParam(defaultValue = "0") final ArrayList<Integer> type) {
        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_PLAIN)
                .body(hostService.getHosts(type));
    }

    /**
     * 下载文件的hosts
     * @param type 请求了行|0
     * @return hosts文件
     */
    @RawResponse
    @GetMapping(value = "file", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    @Operation(summary = "生成hosts文件并下载")
    public ResponseEntity<byte[]> getFile(@RequestParam(defaultValue = "0") final ArrayList<Integer> type) {
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"hosts\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(hostService.getHosts(type).getBytes(StandardCharsets.UTF_8));
    }
}

