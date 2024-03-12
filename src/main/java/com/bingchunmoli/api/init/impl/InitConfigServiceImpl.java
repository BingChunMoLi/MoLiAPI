package com.bingchunmoli.api.init.impl;

import com.bingchunmoli.api.config.ApiConfig;
import com.bingchunmoli.api.config.bean.Config;
import com.bingchunmoli.api.config.service.ConfigService;
import com.bingchunmoli.api.init.InitService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * @author moli
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class InitConfigServiceImpl implements InitService {
    private final ConfigService configService;
    private final ApiConfig apiConfig;

    @Override
    public void init() {
        long count = configService.count();
        if (count == 0) {
            doInit();
        }
    }

    private void doInit() {
        Class<? extends ApiConfig> apiConfigClass = apiConfig.getClass();
        List<Config> list = new ArrayList<>(15);
        for (Field field : apiConfigClass.getFields()) {
            String fieldName = field.getName();
            String fieldNameUpperCase = fieldName.substring(0, 1).toUpperCase(Locale.ROOT) + fieldName.substring(1);
            try {
                Method method = apiConfigClass.getMethod("get" + fieldNameUpperCase);
                String value = (String) method.invoke(apiConfig);
                list.add(Config.builder()
                        .key(fieldName)
                        .value(value)
                        .updateTime(LocalDateTime.now())
                        .build());
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }
        configService.saveBatch(list);
    }

    @Override
    public Integer getOrder() {
        return 7;
    }
}
