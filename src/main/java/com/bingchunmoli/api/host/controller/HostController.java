package com.bingchunmoli.api.host.controller;

import com.bingchunmoli.api.host.service.IHostService;
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
 * @author BingChunMoLi
 */

@RestController
@RequiredArgsConstructor
@RequestMapping("host")
public class HostController {

    private final IHostService hostService;

    /**
     * 暂有问题
     * @param type 请求类型
     * @return json的Host序列化
     */
    @GetMapping("json")
    public String getHosts(@RequestParam ArrayList<Integer> type) {
        return hostService.getHosts(type);
    }

    /**
     * raw的host
     * @param type 请求类型
     * @param response servlet响应
     * @throws IOException response返回可能引发异常
     */
    @GetMapping(value = "raw", produces = MediaType.TEXT_HTML_VALUE)
    public void getRaw(@RequestParam ArrayList<Integer> type, HttpServletResponse response) throws IOException {
        response.getOutputStream().print(hostService.getHosts(type));
    }

    @GetMapping("file")
    public void getFile(@RequestParam ArrayList<Integer> type, HttpServletResponse response) throws IOException {
        response.setHeader("Content-Disposition", "attachment;fileName=hosts");
        response.getOutputStream().write(hostService.getHosts(type).getBytes(StandardCharsets.UTF_8));
    }
}

