package com.bingchunmoli.api.host.controller;

import com.bingchunmoli.api.host.service.HostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

/**
 * hosts订阅
 * @author BingChunMoLi
 */

@RestController
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
    public String getHosts(@RequestParam(defaultValue = "0") ArrayList<Integer> type) {
        return hostService.getHosts(type);
    }

    /**
     * raw的host
     * @param type 请求类型|0
     * @param response servlet响应
     * @throws IOException response返回可能引发异常
     */
    @GetMapping(value = "raw", produces = MediaType.TEXT_HTML_VALUE)
    public void getRaw(@RequestParam(defaultValue="0") ArrayList<Integer> type, HttpServletResponse response) throws IOException {
        response.setHeader("Content-Type", "text/plain");
        response.getOutputStream().print(hostService.getHosts(type));
    }

    /**
     * 下载文件的hosts
     * @param type 请求了行|0
     * @param response servlet响应
     * @throws IOException response返回可能引发异常
     */
    @GetMapping("file")
    public void getFile(@RequestParam(defaultValue = "0") ArrayList<Integer> type, HttpServletResponse response) throws IOException {
        response.setHeader("Content-Disposition", "attachment;fileName=hosts");
        response.getOutputStream().write(hostService.getHosts(type).getBytes(StandardCharsets.UTF_8));
    }
}

