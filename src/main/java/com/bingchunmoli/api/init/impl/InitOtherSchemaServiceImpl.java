package com.bingchunmoli.api.init.impl;

import com.bingchunmoli.api.bean.Init;
import com.bingchunmoli.api.bean.enums.DriveType;
import com.bingchunmoli.api.init.InitSqlService;
import com.bingchunmoli.api.utils.InitUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ResourceLoader;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;

/**
 * @author MoLi
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class InitOtherSchemaServiceImpl implements InitSqlService {

    private final InitUtil initUtil;
    private final JdbcTemplate jdbcTemplate;
    private final DataSource dataSource;
    private final ResourceLoader resourceLoader;

    public Init init;
    @Value("${spring.profiles.active}")
    private String profile;

    @Override
    public void doInit() {
        initSchema();
    }

    @Override
    public boolean check() {
        init = initUtil.buildInit("other", profile);
        if (init.driveType().getType() == DriveType.MYSQL.getType()) {
            return Boolean.TRUE.equals(jdbcTemplate.query("SHOW TABLES LIKE 'bing_image';", rs -> {
                while (rs.next()) {
                    return true;
                }
                return false;
            }));
        } else if (DriveType.H2.getType() == init.driveType().getType()) {
            return Boolean.TRUE.equals(jdbcTemplate.query("SHOW TABLES", rs -> {
                while (rs.next()) {
                    if ("bing_image".equals(rs.getString(1))) {
                        return true;
                    }
                }
                return false;
            }));
        }
        return true;
    }

    @Override
    public void initSchema() {
        InitSqlService.initDatabaseBySqlPath(dataSource, resourceLoader, init.activeSchemaPath());
    }

    @Override
    public void initDataBySql() {
        InitSqlService.initDatabaseBySqlPath(dataSource, resourceLoader, init.activeDataPath());
    }


}
