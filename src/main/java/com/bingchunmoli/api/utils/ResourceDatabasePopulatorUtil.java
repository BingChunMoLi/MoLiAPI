package com.bingchunmoli.api.utils;

import org.springframework.core.io.ResourceLoader;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

import javax.sql.DataSource;

public class ResourceDatabasePopulatorUtil {
    private ResourceDatabasePopulatorUtil() {}

    /**
     * 读取sql文件并批量执行
     * @param dataSource 数据源
     * @param resourceLoader 资源加载器
     * @param activeSqlPath sql文件路径
     */
    public static void executeSqlByClassPathFile(final DataSource dataSource, final ResourceLoader resourceLoader, final String activeSqlPath) {
        ResourceDatabasePopulator resourceDatabasePopulator = new ResourceDatabasePopulator(resourceLoader.getResource(activeSqlPath));
        resourceDatabasePopulator.execute(dataSource);
    }
}
