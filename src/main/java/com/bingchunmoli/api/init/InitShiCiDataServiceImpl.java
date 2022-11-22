package com.bingchunmoli.api.init;

import com.bingchunmoli.api.bean.ApiConstant;
import com.bingchunmoli.api.bean.Init;
import com.bingchunmoli.api.exception.ApiInitException;
import com.bingchunmoli.api.shici.bean.ShiCi;
import com.bingchunmoli.api.shici.service.ShiCiService;
import com.bingchunmoli.api.utils.InitUtil;
import com.bingchunmoli.api.utils.RedisUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
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
public class InitShiCiDataServiceImpl implements InitDataService<ShiCi> {
    private final RedisUtil redisUtil;
    private final JdbcTemplate jdbcTemplate;
    private final InitUtil initUtil;
    private final ShiCiService shiCiService;
    private Init init;

    @Override
    public void doInit() {
        List<ShiCi> shiCis = Collections.emptyList();
        if (init.driveType().getType() == 0) {
             shiCis = readAll();
        } else {
            initSchema();
            initDataBySql();
            shiCis = shiCiService.list();
        }
        if (init.cacheEnable()) {
            redisUtil.setList(ApiConstant.SHI_CI, shiCis);
        } else {
            throw new ApiInitException("暂不支持无数据库,无缓存的情况下初始化");
        }
    }

    @Override
    public boolean check() {
        init = initUtil.buildInit();
        return Boolean.TRUE.equals(jdbcTemplate.query("SHOW TABLES LIKE 'shi_ci';", rs -> {
            while (rs.next()) {
                return true;
            }
            return false;
        }));
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

    @Override
    public void initDataBySql() {
        Path path = null;
        try {
            path = Paths.get(ResourceUtils.getURL(init.activeDataPath()).toURI());
        } catch (FileNotFoundException | URISyntaxException e) {
            throw new ApiInitException(e);
        }
        initDataBySql(path);
    }

    @Override
    public void initDataBySql(Path path) {
        String sql = null;
        try {
            sql = Files.readString(path, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new ApiInitException(e);
        }
        jdbcTemplate.execute(sql);
    }

    @Override
    public void initSchema() {
        String sql = "";
        try {
            Path path = Paths.get(ResourceUtils.getURL(init.activeSchemaPath()).toString());
            sql = Files.readString(path, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new ApiInitException(e);
        }
        if (sql.isBlank()) {
            sql = """
                    -- api.shi_ci definition
                    CREATE TABLE `shi_ci`
                    (
                        `id`          int NOT NULL AUTO_INCREMENT,
                        `content`     varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
                        `origin`      varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
                        `author`      varchar(10) CHARACTER SET utf8 COLLATE utf8_general_ci  DEFAULT NULL,
                        `category`    varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
                        `deleted`     int                                                     DEFAULT '0' ,
                        `create_time` datetime                                                DEFAULT NULL,
                        `update_time` datetime                                                DEFAULT NULL,
                        `version`     int                                                     DEFAULT NULL,
                        PRIMARY KEY (`id`) USING BTREE
                    ) ENGINE = InnoDB AUTO_INCREMENT = 4001 DEFAULT CHARSET = utf8 ROW_FORMAT = DYNAMIC;
                     """;
        }
        jdbcTemplate.execute(sql);
    }

}
