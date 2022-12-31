package com.bingchunmoli.api.init.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.bingchunmoli.api.bean.ApiConstant;
import com.bingchunmoli.api.bean.Init;
import com.bingchunmoli.api.init.InitDataService;
import com.bingchunmoli.api.init.InitSqlService;
import com.bingchunmoli.api.utils.InitUtil;
import com.bingchunmoli.api.utils.RedisUtil;
import com.bingchunmoli.api.yiyan.bean.YiYan;
import com.bingchunmoli.api.yiyan.service.YiYanService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

/**
 * @author MoLi
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class InitYiYanDataServiceImpl implements InitDataService<YiYan> {
    private final RedisUtil redisUtil;
    private final JdbcTemplate jdbcTemplate;
    private final InitUtil initUtil;
    private final YiYanService yiYanService;
    @Getter
    private Init init;

    @Override
    public void doInit() {
        List<YiYan> yiYans = Collections.emptyList();
        if (init.driveType().getType() == 0) {
            yiYans = readAll();
        }else {
            initSchema();
            initDataBySql();
            yiYans = yiYanService.list();
        }
        if (CollectionUtil.isNotEmpty(yiYans)) {
            redisUtil.setList(ApiConstant.YI_YAN, yiYans);
        }else {
            if (log.isWarnEnabled()) {
                log.warn("一言数据为空");
            }
        }
    }

    @Override
    public boolean check() {
        init = initUtil.buildInit(ApiConstant.YI_YAN);
        return InitSqlService.checkTableIsExist(init.driveType(), jdbcTemplate, ApiConstant.YI_YAN_TABLE_NAME);
    }

    @Override
    public void initSchema() {
        InitSqlService.initDatabaseBySqlPath(jdbcTemplate, init.activeSchemaPath());
    }

    @Override
    public void initDataBySql() {
        InitSqlService.initDatabaseBySqlPath(jdbcTemplate, init.activeDataPath());
    }

    @Override
    public List<YiYan> readAll() {
        return readAllDataByFile();
    }

    @Override
    public List<YiYan> readAllDataByFile() {
        try {
            return readAllDataByFile(Paths.get(ResourceUtils.getURL(ApiConstant.YI_YAN_DATA_PATH).toURI()));
        } catch (URISyntaxException | FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<YiYan> readAllDataByFile(Path path) {
        List<YiYan> list = new ArrayList<>();
        try (Stream<String> stream = Files.lines(path)) {
            stream.forEach(v -> list.add(YiYan.builder().hitokoto(v).build()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return list;
    }
}
