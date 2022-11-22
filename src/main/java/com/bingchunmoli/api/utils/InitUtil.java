package com.bingchunmoli.api.utils;

import com.bingchunmoli.api.bean.ApiConstant;
import com.bingchunmoli.api.bean.Init;
import com.bingchunmoli.api.bean.enums.DriveType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.SQLException;

/**
 * @author MoLi
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class InitUtil {
    private final RedisUtil redisUtil;
    private final JdbcTemplate jdbcTemplate;

    public Init buildInit() {
        try {
            if (jdbcTemplate.getDataSource().getConnection().getMetaData().getDriverName().toUpperCase().contains(DriveType.MYSQL.getDriveName())) {
                return new Init(DriveType.MYSQL, redisUtil.isEnable(), ApiConstant.YI_YAN_SCHEMA_PATH_MYSQL, ApiConstant.YI_YAN_DATA_PATH_MYSQL);
            }else {
                return new Init(DriveType.H2, redisUtil.isEnable(), ApiConstant.YI_YAN_SCHEMA_PATH_H2, ApiConstant.YI_YAN_DATA_PATH_H2);
            }
        } catch (SQLException e) {
            log.warn("初始化时没有数据库", e);
            return new Init(DriveType.NONE, redisUtil.isEnable(), null, null);
        }
    }

}
