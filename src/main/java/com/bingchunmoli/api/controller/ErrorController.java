package com.bingchunmoli.api.controller;


import com.bingchunmoli.api.bean.ResultVO;
import com.bingchunmoli.api.bean.enums.CodeEnum;
import com.bingchunmoli.api.bean.enums.NotSupportHttpCode;
import com.bingchunmoli.api.interceptor.RequestTraceIdInterceptor;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Objects;

/**
 * 自定义错误页面与错误信息处理
 * @author bingchunmoli
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class ErrorController implements org.springframework.boot.web.servlet.error.ErrorController {
    private final ResourceLoader resourceLoader;

    /**
     * 友好错误返回页面
     * @param request 请求
     * @param response 响应
     * @return ModelAndView视图
     */
    @Operation(summary = "错误的http状态码生成错误的消息图片")
    @RequestMapping(value = "/error", produces = MediaType.IMAGE_JPEG_VALUE)
    public BufferedImage handleError(HttpServletRequest request, HttpServletResponse response) {
        Object message = request.getAttribute("javax.servlet.error.message");
        Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
        Object path = request.getAttribute("javax.servlet.error.request_uri");
        log.error("错误状态码: {}, 错误消息: {}, 错误路径: {}, traceId: {}, 响应状态码: {}", statusCode, message, path, MDC.get(RequestTraceIdInterceptor.TRACE_ID), response.getStatus());
        if (Arrays.stream(NotSupportHttpCode.values()).filter(v -> Objects.equals(v.getValue(), statusCode)).findFirst().orElse(null) != null) {
            //在不支持友好返回的状态吗中
            log.info("在不支持友好返回的状态吗中");
            BufferedImage img = new BufferedImage(750, 750, BufferedImage.TYPE_INT_RGB);
            Graphics2D graphics = (Graphics2D) img.getGraphics();
            graphics.setColor(Color.BLACK);
            graphics.fillRect(0, 0, 750, 750);
            graphics.setColor(Color.WHITE);
            Font font = new Font("宋体", Font.BOLD, 30);
            graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            graphics.setFont(font);
            graphics.drawString("statusCode: " + statusCode, 10, 20);
            return img;
        }
        if (statusCode == null) {
            log.info("错误状态码为null");
            return null;
        }
        Resource resource = resourceLoader.getResource("classpath:img/" + statusCode + ".jpg");
        try (InputStream inputStream = resource.getInputStream()){
            return ImageIO.read(inputStream);
        } catch (IOException e) {
            log.error("错误状态码: {}, 读取文件路径: {}, 转换图片失败!",statusCode, "classpath:img/" + statusCode + ".jpg");
        }
        return null;
    }


    /**
     * 友好错误信息
     * @param request 请求
     * @param response 响应
     * @return ResultVO<String>
     */
    @Operation(summary = "错误的消息,json格式")
    @RequestMapping(value = "/error", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResultVO<Object> handleErrorBody(HttpServletRequest request, HttpServletResponse response){
        Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
        Object message = request.getAttribute("javax.servlet.error.message");
        Object path = request.getAttribute("javax.servlet.error.request_uri");
        log.error("错误状态码: {},错误消息: {}, 错误路径: {}, traceId: {}, 响应状态码: {}", statusCode, message, path, MDC.get(RequestTraceIdInterceptor.TRACE_ID), response.getStatus());
        if(statusCode == null){
            return new ResultVO<>(CodeEnum.FAILURE, message);
        } else if (statusCode >= HttpStatus.BAD_REQUEST.value() && statusCode <= HttpStatus.INTERNAL_SERVER_ERROR.value()) {
            return new ResultVO<>(CodeEnum.ERROR, message);
        }else if (statusCode >= HttpStatus.INTERNAL_SERVER_ERROR.value()){
            return new ResultVO<>(CodeEnum.FAILURE, message);
        }else {
            return new ResultVO<>(CodeEnum.THIRD, message);
        }
    }


}
