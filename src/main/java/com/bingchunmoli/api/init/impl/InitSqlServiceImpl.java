package com.bingchunmoli.api.init.impl;

import cn.hutool.core.util.StrUtil;
import com.bingchunmoli.api.exception.ApiInitException;
import com.bingchunmoli.api.init.InitService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.stream.Stream;

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
            Path path = Path.of(sqlPath);
            if (!path.toFile().exists()) {
                if (log.isWarnEnabled()) {
                    log.warn("sqlPath: {} 不存在, 跳过了初始化", sqlPath);
                }
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
            try (Stream<Path> walk = Files.walk(path)){
                String finalDriverName = driverName;
                walk.filter(v -> v.toString().endsWith(".sql"))
                    .filter(v -> v.toString().contains(finalDriverName)).forEach(v -> {
                        Resource resource = new FileSystemResource(v);
                        new ResourceDatabasePopulator(resource).execute(dataSource);
                    });
            } catch (IOException e) {
                if (log.isErrorEnabled()) {
                    log.error("读取初始化sql出现问题, path: {}", path);
                }
                throw new ApiInitException(e);
            }

        }
    }

}
