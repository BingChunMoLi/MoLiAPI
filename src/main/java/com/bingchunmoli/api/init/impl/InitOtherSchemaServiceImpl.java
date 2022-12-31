package com.bingchunmoli.api.init.impl;

import com.bingchunmoli.api.bean.ApiConstant;
import com.bingchunmoli.api.bean.Init;
import com.bingchunmoli.api.bean.enums.DriveType;
import com.bingchunmoli.api.init.InitSqlService;
import com.bingchunmoli.api.utils.InitUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

/**
 * @author MoLi
 */
@Slf4j
@Order(1)
@Service
@RequiredArgsConstructor
public class InitOtherSchemaServiceImpl implements InitSqlService {

    private final InitUtil initUtil;
    private final JdbcTemplate jdbcTemplate;
    public Init init;

    @Override
    public void doInit() {
        init = initUtil.buildInit("other");

        initSchema();
    }

    @Override
    public boolean check() {
        init = initUtil.buildInit(ApiConstant.SHI_CI);
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
        InitSqlService.initDatabaseBySqlPath(jdbcTemplate, init.activeSchemaPath());
    }

    @Override
    public void initDataBySql() {
        InitSqlService.initDatabaseBySqlPath(jdbcTemplate, init.activeDataPath());
    }


}
