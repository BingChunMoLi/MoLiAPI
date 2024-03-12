package com.bingchunmoli.api.config.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bingchunmoli.api.config.ApiConfig;
import com.bingchunmoli.api.config.bean.Config;
import com.bingchunmoli.api.config.mapper.ConfigMapper;
import com.bingchunmoli.api.config.service.ConfigService;
import com.bingchunmoli.api.exception.ApiException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Objects;

/**
 * @author MoLi
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ConfigServiceImpl extends ServiceImpl<ConfigMapper, Config> implements ConfigService {
    private final ApiConfig apiConfig;

    @Override
    public void updateApiConfig(ApiConfig apiConfig) {
        Class<? extends ApiConfig> apiConfigClass = apiConfig.getClass();
        for (Field field : apiConfigClass.getDeclaredFields()) {
//            field.setAccessible(true);
            String fieldName = field.getName();
            String upperCaseFieldName = fieldName.substring(0, 1).toUpperCase(Locale.ROOT) + fieldName.substring(1);
            try {
                Method getMethod = apiConfigClass.getMethod("get" + upperCaseFieldName);
                Object paramValue = getMethod.invoke(apiConfig);
                Object systemValue = getMethod.invoke(this.apiConfig);
                if (!Objects.equals(paramValue, systemValue)) {
                    Method setMethod = apiConfigClass.getMethod("set" + upperCaseFieldName, String.class);
                    setMethod.invoke(this.apiConfig, paramValue);
                    Config config = getOneOpt(new LambdaQueryWrapper<Config>().eq(Config::getKey, fieldName))
                            .orElse(Config.builder()
                                    .key(fieldName)
                                    .value((String) paramValue)
                                    .updateTime(LocalDateTime.now())
                                    .build());
                    saveOrUpdate(config);
                }
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                throw new ApiException(e);
            }

        }
    }
}




