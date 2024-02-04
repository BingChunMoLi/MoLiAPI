package com.bingchunmoli.api.yiyan.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bingchunmoli.api.exception.ApiInitException;
import com.bingchunmoli.api.yiyan.bean.YiYan;
import com.bingchunmoli.api.yiyan.mapper.YiYanMapper;
import com.bingchunmoli.api.yiyan.service.YiYanService;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.MappingJsonFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;

/**
 * @author BingChunMoLi
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class YiYanServiceImpl extends ServiceImpl<YiYanMapper, YiYan> implements YiYanService {
    private final ObjectMapper objectMapper;
    private final List<YiYan> list = new ArrayList<>(500);
    @Value("${moli.init.yiYanJsonPath:init/data}")
    private String yiYanJsonPath;

    @PostConstruct
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
        for (File file : files) {
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


    @Override
    public YiYan findRandomYiYan() {
        if (list.isEmpty()) {
            return baseMapper.findRandom();
        }
        return list.get(new Random().nextInt(list.size()));
    }

    /**
     * 根据 ID 查询，返回一个Option对象
     *
     * @param id 主键ID
     * @return {@link Optional}
     */
    @Override
    public Optional<YiYan> getOptById(Serializable id) {
        if (list.isEmpty()) {
            return super.getOptById(id);
        }
        return list.stream().filter(v -> Objects.equals(v.getId(), Long.valueOf(String.valueOf(id)))).findFirst();
    }
}
