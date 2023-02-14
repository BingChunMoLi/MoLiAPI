package com.bingchunmoli.api.init;

import com.bingchunmoli.api.bean.enums.DriveType;
import com.bingchunmoli.api.exception.ApiInitException;
import com.bingchunmoli.api.utils.ResourceDatabasePopulatorUtil;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ResourceLoader;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

/**
 * @author MoLi
 */
public interface InitSqlService extends InitService{

    Logger log = LoggerFactory.getLogger(InitSqlService.class);

    static boolean checkDataBaseIsExist(DriveType driveType, JdbcTemplate jdbcTemplate, String api) {
        if (driveType.getType() == DriveType.MYSQL.getType()) {
            return Boolean.TRUE.equals(jdbcTemplate.query("SHOW DATABASES LIKE '" + api + "';", rs -> {
                while (rs.next()) {
                    return true;
                }
                jdbcTemplate.execute("create schema api");
                return false;
            }));
        } else if (DriveType.H2.getType() == driveType.getType()) {
            return Boolean.TRUE.equals(jdbcTemplate.query("SHOW DATABASES", rs -> {
                while (rs.next()) {
                    if (api.equals(rs.getString(1))) {
                        return true;
                    }
                }
                jdbcTemplate.execute("create schema api");
                return false;
            }));
        }else {
            if (log.isErrorEnabled()) {
                log.error("不受支持的数据库类型,driveName: {}", driveType.getDriveName());
            }
        }
        return true;
    }

    /**
     * 检查是否已经初始化过等异常情况
     *
     * @return 如果初始化过为true 否则为false
     */
    boolean check() throws ApiInitException;


    @Override
    default void init(){
        if (check()) {
            return;
        }
        doInit();
    }

    /**
     * 真正的Init
     */
    void doInit();

    /**
     * 初始化结构
     */
    void initSchema();

    /**
     * 读取数据从sql
     */
    void initDataBySql();


    /**
     * 读取sql文件并执行(初始化动作)
     * @param dataSource 数据源
     * @param resourceLoader 资源加载器
     * @param activeSqlPath sql文件路径
     */
    static void initDatabaseBySqlPath(final DataSource dataSource, final ResourceLoader resourceLoader, final String activeSqlPath) {
        ResourceDatabasePopulatorUtil.executeSqlByClassPathFile(dataSource, resourceLoader, activeSqlPath);
    }

    static boolean checkTableIsExist(final @NotNull DriveType driveType, @NotNull final JdbcTemplate jdbcTemplate, @NotNull final String tableName) {
        checkDataBaseIsExist(driveType, jdbcTemplate, "api");
        if (driveType.getType() == DriveType.MYSQL.getType()) {
            return Boolean.TRUE.equals(jdbcTemplate.query("SHOW TABLES LIKE '" + tableName + "';", rs -> {
                while (rs.next()) {
                    return true;
                }
                return false;
            }));
        } else if (DriveType.H2.getType() == driveType.getType()) {
            return Boolean.TRUE.equals(jdbcTemplate.query("SHOW TABLES", rs -> {
                while (rs.next()) {
                    if (tableName.equals(rs.getString(1))) {
                        return true;
                    }
                }
                return false;
            }));
        }else {
            if (log.isErrorEnabled()) {
                log.error("不受支持的数据库类型,driveName: {}", driveType.getDriveName());
            }
        }
        return true;
    }
}
