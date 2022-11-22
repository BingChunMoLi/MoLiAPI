package com.bingchunmoli.api.init;

import com.bingchunmoli.api.bean.ApiConstant;
import com.bingchunmoli.api.bean.Init;
import com.bingchunmoli.api.exception.ApiInitException;
import com.bingchunmoli.api.utils.InitUtil;
import com.bingchunmoli.api.utils.RedisUtil;
import com.bingchunmoli.api.yiyan.bean.YiYan;
import com.bingchunmoli.api.yiyan.service.YiYanService;
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
public class InitYiYanDataServiceImpl implements InitDataService<YiYan> {
    private final RedisUtil redisUtil;
    private final JdbcTemplate jdbcTemplate;
    private final InitUtil initUtil;
    private final YiYanService yiYanService;
    private Init init;

    @Override
    public void doInit() {
        List<YiYan> yiYans = Collections.emptyList();
        if (init.driveType().getType() == 0) {
            yiYans = readAll();
        }else {
            initSchema();
            initDataBySql();
            yiYanService.list();
        }
        if (init.cacheEnable()) {
            redisUtil.setList(ApiConstant.YI_YAN, yiYans);
        }else {
            throw new ApiInitException("暂不支持无数据库,无缓存的情况下初始化");
        }
    }

    @Override
    public boolean check() {
        init = initUtil.buildInit();
        return Boolean.TRUE.equals(jdbcTemplate.query("SHOW TABLES LIKE 'yi_yan';", rs -> {
            while (rs.next()) {
                return true;
            }
            return false;
        }));
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
                    CREATE TABLE `yi_yan` (
                                              `id` int NOT NULL,
                                              `uuid` char(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
                                              `hitokoto` char(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
                                              `type` char(5) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
                                              `from` char(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
                                              `from_who` char(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
                                              `creator` char(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
                                              `creator_uid` int DEFAULT NULL,
                                              `reviewer` int DEFAULT NULL,
                                              `commit_from` char(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
                                              `created_at` char(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
                                              `length` int DEFAULT NULL,
                                              `deleted` int DEFAULT '0',
                                              `create_time` datetime DEFAULT NULL,
                                              `update_time` datetime DEFAULT NULL,
                                              `version` int DEFAULT NULL,
                                              PRIMARY KEY (`id`) USING BTREE
                                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;
                                """;
        }
        jdbcTemplate.execute(sql);
    }

}
