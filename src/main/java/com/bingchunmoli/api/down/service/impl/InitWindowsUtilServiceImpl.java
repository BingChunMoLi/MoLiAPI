package com.bingchunmoli.api.down.service.impl;


import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.StreamProgress;
import cn.hutool.core.util.ZipUtil;
import cn.hutool.http.Header;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import com.bingchunmoli.api.down.config.SystemConfig;
import com.bingchunmoli.api.down.service.InitUtilService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.StringJoiner;
import java.util.concurrent.CountDownLatch;

/**
 * @author MoLi
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class InitWindowsUtilServiceImpl implements InitUtilService {

    @Override
    public void initBaiduPCS(CountDownLatch countDownLatch) {
        String baiduPCS = "https://github.com/felixonmars/BaiduPCS-Go/releases/download/v3.6.2/BaiduPCS-Go-v3.6.2-windows-x64.zip";
        File file = downAndUnzip(baiduPCS, SystemConfig.getBaiduPCSPath());
        if (file == null) {
            return;
        }
        StringJoiner sj = new StringJoiner(File.separator);
        String old = sj.add(file.getAbsolutePath())
                .add("BaiduPCS-Go-v3.6.2-windows-x64")
                .add("BaiduPCS-Go.exe").toString();
        sj = new StringJoiner(File.separator);
        String newFile = sj.add(file.getAbsolutePath()).add("BaiduPCS-Go.exe").toString();
        FileUtil.move(Paths.get(old), Paths.get(SystemConfig.getBaiduPCSPath()), true);
        log.info("old: {}, new: {}", old, newFile);
        sj = new StringJoiner(File.separator);
        String del = sj.add(file.getAbsolutePath())
                .add("BaiduPCS-Go-v3.6.2-windows-x64").toString();
        FileUtil.del(del);
        countDownLatch.countDown();
    }

    @Override
    public void initBBDown(CountDownLatch countDownLatch) {
        String bbDown = "https://github.com/nilaoda/BBDown/releases/download/1.5.3/BBDown_1.5.3_20220514_win-x64.zip";
        downAndUnzip(bbDown, SystemConfig.getBBDownPath());
        countDownLatch.countDown();
    }

    @Override
    public void initRclone(CountDownLatch countDownLatch) {
        String rclone = "https://github.com/rclone/rclone/releases/download/v1.59.1/rclone-v1.59.1-windows-amd64.zip";
        File file = downAndUnzip(rclone, SystemConfig.getRclonePath());
        StringJoiner sj = new StringJoiner(File.separator);
        String old = sj.add(file.getAbsolutePath())
                .add("rclone-v1.59.1-windows-amd64")
                .add("rclone.exe").toString();
        sj = new StringJoiner(File.separator);
        String newFile = sj.add(file.getAbsolutePath()).add("rclone.exe").toString();
        FileUtil.move(Paths.get(old), Paths.get(SystemConfig.getRclonePath()), true);
        log.info("old: {}, new: {}", old, newFile);
        sj = new StringJoiner(File.separator);
        String del = sj.add(file.getAbsolutePath())
                .add("rclone-v1.59.1-windows-amd64").toString();
        FileUtil.del(del);
        countDownLatch.countDown();
    }

    @Override
    public void initYouGet(CountDownLatch countDownLatch) {
        //https://github.com/soimort/you-get/releases/download/v0.4.1620/you-get-0.4.1620.tar.gz
        log.info("you-get 暂不初始化");
        countDownLatch.countDown();
    }

    private File downAndUnzip(String url, String path) {
        File file = Paths.get(path).toFile();
        if (file.exists()) {
            return null;
        }
        String location = buildRequest(url);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        HttpUtil.download(location, os, false, new StreamProgress() {

            @Override
            public void start() {
                log.info("开始下载。。。。");
            }

            @Override
            public void progress(long total, long progressSize) {
                log.info("已下载：{}", FileUtil.readableFileSize(progressSize));
            }

            @Override
            public void finish() {
                log.info("下载完成！");
            }
        });
        InputStream is = new ByteArrayInputStream(os.toByteArray());
        return ZipUtil.unzip(is, file.getParentFile(), null);
    }

    private String buildRequest(String url) {
        HttpRequest request = HttpRequest.get(url);
        request.header(Header.USER_AGENT, "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/104.0.0.0 Safari/537.36");
        request.header("authority", "github.com");
        request.header("sec-ch-ua", "\"Chromium\";v=\"104\", \" Not A;Brand\";v=\"99\", \"Google Chrome\";v=\"104\"");
        request.header("sec-ch-ua-mobile", "?0");
        request.header("sec-ch-ua-platform", "\"Windows\"");
        try (HttpResponse execute = request.execute()) {
            return execute.header("Location");
        }
    }
}
