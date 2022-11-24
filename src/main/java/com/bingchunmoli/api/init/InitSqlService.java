package com.bingchunmoli.api.init;

import cn.hutool.core.util.StrUtil;
import com.bingchunmoli.api.exception.ApiInitException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.ResourceUtils;

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



    static void initDatabaseBySqlPath(JdbcTemplate jdbcTemplate, String activeSqlPath) {
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
}
