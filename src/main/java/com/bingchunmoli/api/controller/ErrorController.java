package com.bingchunmoli.api.controller;


import com.bingchunmoli.api.bean.ResultVO;
import com.bingchunmoli.api.bean.enums.CodeEnum;
import com.bingchunmoli.api.bean.enums.NotSupportHttpCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;

/**
 * 错误页面与错误信息处理
 * @author bingchunmoli
 */
@Slf4j
@RestController
public class ErrorController implements org.springframework.boot.web.servlet.error.ErrorController {

    /**
     * 有好错误返回页面
     * @param request 请求
     * @param response 响应
     * @return ModelAndView视图
     */
    @RequestMapping(value = "/error", produces = MediaType.IMAGE_JPEG_VALUE)
    public BufferedImage handleError(HttpServletRequest request, HttpServletResponse response) {
        Object message = request.getAttribute("javax.servlet.error.message");
        Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
        Object path = request.getAttribute("javax.servlet.error.request_uri");
        log.error("错误状态码: {},错误消息: {}, 错误路径: {}, 响应状态码: {}", statusCode, message, path, response.getStatus());
        if (Arrays.stream(NotSupportHttpCode.values()).filter(v -> v.getValue() == statusCode).findFirst().orElse(null) != null) {
            //在不支持友好返回的状态吗中
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
        File file = null;
        try {
            file = ResourceUtils.getFile("classpath:img/" + statusCode + ".jpg");
        } catch (FileNotFoundException e) {
            log.error("错误状态码: {}, 读取文件路径: {}, 获取文件失败!",statusCode, "classpath:img/" + statusCode + ".jpg");
        }
        try {
            if (file == null) {
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            }
            assert file != null;
            return ImageIO.read(file);
        } catch (IOException e) {
            log.error("错误状态码: {}, 读取文件路径: {}, 转换图片失败!",statusCode, "classpath:img/" + statusCode + ".jpg");
        }
        return null;
    }


    @RequestMapping(value = "/error", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResultVO<Object> handleErrorBody(HttpServletRequest request, HttpServletResponse response){
        Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
        Object message = request.getAttribute("javax.servlet.error.message");
        Object path = request.getAttribute("javax.servlet.error.request_uri");
        log.error("错误状态码: {},错误消息: {}, 错误路径: {}, 响应状态码: {}", statusCode, message, path, response.getStatus());
        if (statusCode >= HttpStatus.BAD_REQUEST.value() && statusCode <= HttpStatus.INTERNAL_SERVER_ERROR.value()) {
            return new ResultVO<>(CodeEnum.ERROR, message);
        }else if (statusCode >= HttpStatus.INTERNAL_SERVER_ERROR.value()){
            return new ResultVO<>(CodeEnum.FAILURE, message);
        }else {
            return new ResultVO<>(CodeEnum.THIRD, message);
        }
    }


}
