package com.bingchunmoli.api.config;

import com.baomidou.mybatisplus.test.autoconfigure.MybatisPlusTest;
import com.bingchunmoli.api.config.bean.Config;
import com.bingchunmoli.api.config.mapper.ConfigMapper;
import com.bingchunmoli.api.config.service.ConfigService;
import com.bingchunmoli.api.config.service.impl.ConfigServiceImpl;
import com.bingchunmoli.api.handler.MapperHandler;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

@MybatisPlusTest
@ContextConfiguration(classes = ConfigMapperTest.TestConfig.class)
@TestPropertySource(properties = {
        "spring.sql.init.schema-locations=classpath:init/db/ddl.sql",
        "spring.datasource.url=jdbc:h2:mem:config-tests;MODE=MySQL;DATABASE_TO_LOWER=TRUE;DB_CLOSE_DELAY=-1"
})
class ConfigMapperTest {
    @Autowired
    private ConfigMapper mapper;
    @Autowired
    private ConfigService service;
    @Autowired
    private ApiConfig live;

    @Configuration
    @EnableAutoConfiguration
    @MapperScan("com.bingchunmoli.api.config.mapper")
    @Import({ConfigServiceImpl.class, MapperHandler.class})
    static class TestConfig {
        @Bean
        ApiConfig apiConfig() {
            return new ApiConfig();
        }
    }

    @Test
    void updatesAnExistingRowAndPreservesUnrelatedConfiguration() {
        live.setWeatherKey("old-placeholder");
        final LocalDateTime now = LocalDateTime.now();
        final Config existing = Config.builder().key("weatherKey").value("old-placeholder")
                .createTime(now).updateTime(now).build();
        final Config unrelated = Config.builder().key("unrelated").value("unchanged")
                .createTime(now).updateTime(now).build();
        mapper.insert(existing);
        mapper.insert(unrelated);
        final ApiConfig requested = new ApiConfig();
        BeanUtils.copyProperties(live, requested);
        requested.setWeatherKey("new-placeholder");

        service.updateApiConfig(requested);

        assertEquals("new-placeholder", mapper.selectById(existing.getId()).getValue());
        assertEquals("unchanged", mapper.selectById(unrelated.getId()).getValue());
        assertEquals(2L, mapper.selectCount(null));
    }

    @Test
    void insertsConfigurationWhenTheRowDoesNotExist() {
        live.setWeatherKey(null);
        final ApiConfig requested = new ApiConfig();
        BeanUtils.copyProperties(live, requested);
        requested.setWeatherKey("new-placeholder");

        service.updateApiConfig(requested);

        assertEquals("new-placeholder", mapper.selectList(null).get(0).getValue());
        assertEquals(1L, mapper.selectCount(null));
    }
}
