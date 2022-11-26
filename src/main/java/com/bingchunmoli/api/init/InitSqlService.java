package com.bingchunmoli.api.init;

import cn.hutool.core.util.StrUtil;
import com.bingchunmoli.api.bean.enums.DriveType;
import com.bingchunmoli.api.exception.ApiInitException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.ResourceUtils;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author MoLi
 */
public interface InitSqlService extends InitService{

    Logger log = LoggerFactory.getLogger(InitSqlService.class);

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
     * @return 数据列表
     */
    void initDataBySql();


    /**
     * 读取sql文件并执行(初始化动作)
     * @param jdbcTemplate jdbcTemplate
     * @param activeSqlPath sql文件路径
     */
    static void initDatabaseBySqlPath(final JdbcTemplate jdbcTemplate,final String activeSqlPath) {
        String sql = "";
        try {
            Path path = Paths.get(ResourceUtils.getURL(activeSqlPath).toURI());
            if (path.toFile().exists()) {
                sql = Files.readString(path, StandardCharsets.UTF_8);
            }
        } catch (IOException | URISyntaxException e) {
            throw new ApiInitException(e);
        }
        if (StrUtil.isNotBlank(sql)) {
            jdbcTemplate.execute(sql);
        }
    }

    static boolean checkTableIsExist(final @NotNull DriveType driveType, @NotNull final JdbcTemplate jdbcTemplate, @NotNull final String tableName) {
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
