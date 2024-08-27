package com.bingchunmoli.api.init.impl;

import com.bingchunmoli.api.init.InitService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.ResultSet;

/**
 * @author MoLi
 */
@Slf4j
@Service
@Deprecated
@RequiredArgsConstructor
public class InitSqlServiceImpl implements InitService {
    private final DataSource dataSource;
    private final JdbcTemplate jdbcTemplate;
    @Value("${moli.init.sqlPath:classpath:/init/db/init.sql}")
    private Resource sqlResource;

    @Override
    public void init() {
        if (sqlResource != null && sqlResource.exists()) {
            Boolean query = jdbcTemplate.query("SHOW TABLES", ResultSet::next);
            if (Boolean.TRUE.equals(query)) {
                //存在表跳过初始化
                return;
            }
            new ResourceDatabasePopulator(sqlResource).execute(dataSource);
        }
    }

    @Override
    public Integer getOrder() {
        return 5;
    }

}
