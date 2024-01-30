package com.bingchunmoli.api.yiyan.controller;

import cn.hutool.core.util.StrUtil;
import com.bingchunmoli.api.exception.ApiInitException;
import com.bingchunmoli.api.yiyan.bean.YiYan;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.MappingJsonFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;

/**
 * 一言
 * @author BingChunMoLi
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("yiyan")
public class YiYanController {
    private final List<YiYan> list = new ArrayList<>(500);
    private final ObjectMapper objectMapper;
    @Value("${moli.init.yiYanJsonPath:init/data}")
    private String yiYanJsonPath;

    @PostConstruct
    public void init(){
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
            if (!file.getName().endsWith(".json")) {
                break;
            }
            MappingJsonFactory factory = new MappingJsonFactory();
            try (JsonParser parser = factory.createParser(file)) {
                JsonToken token = parser.nextToken();
                if (token != JsonToken.START_ARRAY) {
                    if (log.isErrorEnabled()) {
                        log.error("解析出错, file: {}", file);
                    }
                    break;
                }
                token = parser.nextToken();
                while (token != JsonToken.END_ARRAY) {
                    JsonNode x = parser.readValueAsTree();
                    YiYan yiYan = objectMapper.treeToValue(x, YiYan.class);
                    list.add(yiYan);
                    token = parser.nextToken();
                }

            } catch (IOException e) {
                throw new ApiInitException(e);
            }
        }
    }

    /**
     * 根据ID获取一言
     * @param id id|1
     * @return 一言数据
     */
    @GetMapping("{id}")
    public Optional<YiYan> getYiYan(@PathVariable Integer id) {
        return list.stream().filter(v-> Objects.equals(v.getId().longValue(), id.longValue())).findFirst();
    }

    /**
     * 查询随机一条一言数据
     *
     * @return 一条一言
     */
    @GetMapping("random")
    public YiYan getRandomYiYan() {
        return list.get(new Random().nextInt(list.size()));
    }
}
