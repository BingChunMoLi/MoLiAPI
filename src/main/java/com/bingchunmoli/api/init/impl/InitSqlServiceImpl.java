package com.bingchunmoli.api.init.impl;

import cn.hutool.core.util.StrUtil;
import com.bingchunmoli.api.init.InitService;
import com.bingchunmoli.api.utils.ResourceDatabasePopulatorUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;

/**
 * @author MoLi
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class InitSqlServiceImpl implements InitService {
    private final DataSource dataSource;
    private final ResourceLoader resourceLoader;
    @Value("${moli.init.sqlPath}")
    private String sqlPath;

    @Override
    public void init() {
        if (StrUtil.isNotEmpty(sqlPath)) {
            ResourceDatabasePopulatorUtil.executeSqlByClassPathFile(dataSource, resourceLoader, sqlPath);
        }
    }

}
