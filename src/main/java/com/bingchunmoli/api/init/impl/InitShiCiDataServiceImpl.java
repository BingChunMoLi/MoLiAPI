package com.bingchunmoli.api.init.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.bingchunmoli.api.bean.ApiConstant;
import com.bingchunmoli.api.bean.Init;
import com.bingchunmoli.api.bean.enums.DriveType;
import com.bingchunmoli.api.init.InitDataService;
import com.bingchunmoli.api.init.InitSqlService;
import com.bingchunmoli.api.shici.bean.ShiCi;
import com.bingchunmoli.api.shici.service.ShiCiService;
import com.bingchunmoli.api.utils.InitUtil;
import com.bingchunmoli.api.utils.RedisUtil;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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
import java.util.List;
import java.util.stream.Stream;

/**
 * @author MoLi
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class InitShiCiDataServiceImpl implements InitDataService<ShiCi> {
    private final RedisUtil redisUtil;
    private final JdbcTemplate jdbcTemplate;
    private final InitUtil initUtil;
    private final ShiCiService shiCiService;
    @Value("spring.profiles.active")
    private String profile;
    @Getter
    private Init init;

    @Override
    public void doInit() {
        List<ShiCi> shiCis;
        if (init.driveType().getType() == DriveType.NONE.getType()) {
             shiCis = readAll();
        } else {
            initSchema();
            initDataBySql();
            shiCis = shiCiService.list();
        }
        if (CollectionUtil.isNotEmpty(shiCis)) {
            redisUtil.setList(ApiConstant.SHI_CI, shiCis);
        }else {
            if (log.isWarnEnabled()) {
                log.warn("诗词数据为空");
            }
        }
    }

    @Override
    public boolean check() {
        init = initUtil.buildInit(ApiConstant.SHI_CI, profile);
        return InitSqlService.checkTableIsExist(init.driveType(), jdbcTemplate, ApiConstant.SHI_CI_TABLE_NAME);
    }

    @Override
    public void initDataBySql() {
        InitSqlService.initDatabaseBySqlPath(jdbcTemplate, init.activeDataPath(), profile);
    }

    @Override
    public void initSchema() {
        InitSqlService.initDatabaseBySqlPath(jdbcTemplate, init.activeSchemaPath(), profile);
    }


    @Override
    public List<ShiCi> readAll() {
        return readAllDataByFile();
    }

    @Override
    public List<ShiCi> readAllDataByFile() {
        try {
            return readAllDataByFile(Paths.get(ResourceUtils.getURL(ApiConstant.YI_YAN_DATA_PATH).toURI()));
        } catch (URISyntaxException | FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<ShiCi> readAllDataByFile(Path path) {
        List<ShiCi> list = new ArrayList<>();
        try (Stream<String> stream = Files.lines(path)) {
            stream.forEach(v -> list.add(ShiCi.builder().content(v).build()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return list;
    }
}
