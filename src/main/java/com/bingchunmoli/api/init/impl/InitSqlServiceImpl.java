package com.bingchunmoli.api.init.impl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.StrUtil;
import com.bingchunmoli.api.exception.ApiInitException;
import com.bingchunmoli.api.init.InitService;
import com.bingchunmoli.api.utils.ResourceDatabasePopulatorUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import javax.sql.DataSource;
import java.io.File;
import java.io.FileNotFoundException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author MoLi
 */
@Slf4j
@Service
@Order(3)
@RequiredArgsConstructor
public class InitSqlServiceImpl implements InitService {
    private final DataSource dataSource;
    private final JdbcTemplate jdbcTemplate;
    @Value("${moli.init.sqlPath}")
    private String sqlPath;

    @Override
    public void init() {
        if (StrUtil.isNotEmpty(sqlPath)) {
            Boolean query = jdbcTemplate.query("SHOW TABLES LIKE '%yi_yan%'", ResultSet::next);
            if (Boolean.TRUE.equals(query)) {
                //存在yi_yan表跳过初始化
                return;
            }
            Resource resource = new FileSystemResource(Path.of(sqlPath));
            new ResourceDatabasePopulator(resource).execute(dataSource);
        }
    }

}
