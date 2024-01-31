package com.bingchunmoli.api.init.impl;

import cn.hutool.core.util.StrUtil;
import com.bingchunmoli.api.init.InitService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.nio.file.Path;
import java.sql.ResultSet;

/**
 * @author MoLi
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class InitSqlServiceImpl implements InitService {
    private final DataSource dataSource;
    private final JdbcTemplate jdbcTemplate;
    @Value("${moli.init.sqlPath:}")
    private String sqlPath;

    @Override
    public void init() {
        Path sqlPath = Path.of(this.sqlPath);
        if (StrUtil.isNotEmpty(this.sqlPath)) {
            Boolean query = jdbcTemplate.query("SHOW TABLES", ResultSet::next);
            if (Boolean.TRUE.equals(query)) {
                //存在表跳过初始化
                return;
            }
            new ResourceDatabasePopulator(new ClassPathResource(sqlPath + "init.sql")).execute(dataSource);
        }
    }

    @Override
    public Integer getOrder() {
        return 5;
    }

}
