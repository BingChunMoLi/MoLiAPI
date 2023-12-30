package com.bingchunmoli.api.init.impl;

import cn.hutool.core.util.StrUtil;
import com.bingchunmoli.api.exception.ApiInitException;
import com.bingchunmoli.api.init.InitService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.io.File;
import java.nio.file.Path;
import java.sql.ResultSet;
import java.sql.SQLException;

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
            String driverName;
            try {
                driverName = dataSource.getConnection().getMetaData().getDriverName().toLowerCase();
            } catch (SQLException e) {
                log.error("获取数据库类型出错");
                throw new ApiInitException(e);
            }
            if (driverName.contains("mysql")) {
                driverName = "mysql";
            }
            if (driverName.contains("h2")) {
                driverName = "h2";
            }
            new ResourceDatabasePopulator(new ClassPathResource(sqlPath + File.separator + driverName + ".sql")).execute(dataSource);
        }
    }

    @Override
    public Integer getOrder() {
        return 5;
    }

}
