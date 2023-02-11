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
    private final JdbcTemplate jdbcTemplate;

    public Init buildInit(String plantForm, String profile) {
        String activePlantFrom = plantForm.toLowerCase();
        DriveType driveType;
        try {
            driveType = jdbcTemplate.getDataSource().getConnection().getMetaData().getDriverName().toUpperCase().contains(DriveType.MYSQL.getDriveName()) ? DriveType.MYSQL : DriveType.H2;
        } catch (SQLException e) {
            log.warn("初始化时没有数据库", e);
            String activeDataFilePath = plantForm.equals(ApiConstant.YI_YAN) ? ApiConstant.YI_YAN_DATA_PATH : ApiConstant.SHI_CI_DATA_PATH;
            return new Init(DriveType.NONE,activeDataFilePath, null, null, profile);
        }
        String activeSchemaPath = ApiConstant.SCHEMA_PATH_PREFIX + driveType.getDriveName().toLowerCase() + "-" + activePlantFrom + ApiConstant.SQL_PATH_SUFFIX;
        String activeDataPath = ApiConstant.DATA_PATH_PREFIX + driveType.getDriveName().toLowerCase() + "-" + activePlantFrom + ApiConstant.SQL_PATH_SUFFIX;
        return new Init(driveType, null, activeSchemaPath, activeDataPath, profile);
    }

}
