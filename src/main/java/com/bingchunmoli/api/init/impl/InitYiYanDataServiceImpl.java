package com.bingchunmoli.api.init.impl;

import cn.hutool.core.util.StrUtil;
import com.bingchunmoli.api.exception.ApiInitException;
import com.bingchunmoli.api.init.InitService;
import com.bingchunmoli.api.yiyan.bean.YiYan;
import com.bingchunmoli.api.yiyan.service.YiYanService;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.MappingJsonFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@Order(4)
@RequiredArgsConstructor
public class InitYiYanDataServiceImpl implements InitService {
    private final ObjectMapper objectMapper;
    private final YiYanService yiYanService;
    @Value("${moli.init.yiYanJsonPath}")
    private String yiYanJsonPath;

    @Override
    public void init() {
        if (StrUtil.isEmpty(yiYanJsonPath)) {
            log.info("没有配置yiYanJsonPath, 跳过初始化一言json文件");
            return;
        }
        Path path = Paths.get(yiYanJsonPath);
        File[] files = path.toFile().listFiles();
        if (files == null) {
            log.error("没有可以初始化的一言json数据");
            return;
        }
        for (File file: files) {
            MappingJsonFactory factory = new MappingJsonFactory();
            try (JsonParser parser = factory.createParser(file)) {
                JsonToken token = parser.nextToken();
                if (token != JsonToken.START_ARRAY) {
                    return;
                }
                token = parser.nextToken();
                List<YiYan> list = new ArrayList<>(300);
                while (token != JsonToken.END_ARRAY) {
                    JsonNode x = parser.readValueAsTree();
                    YiYan yiYan = objectMapper.treeToValue(x, YiYan.class);
                    list.add(yiYan);
                    token = parser.nextToken();
                }
                yiYanService.saveBatch(list);
            } catch (IOException e) {
                throw new ApiInitException(e);
            }
        }
    }

}
