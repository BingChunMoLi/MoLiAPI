package com.bingchunmoli.api.init.impl;

import com.bingchunmoli.api.exception.ApiInitException;
import com.bingchunmoli.api.init.InitService;
import com.bingchunmoli.api.properties.ApiConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * @author MoLi
 */

@Slf4j
@Service
@Order(1)
@RequiredArgsConstructor
public final class InitFileServiceImpl implements InitService {
    private final ApiConfig apiConfig;

    @Override
    public void init() {
        List<Path> list = new ArrayList<>();
        list.add(Paths.get(apiConfig.getPcPath()));
        list.add(Paths.get(apiConfig.getMobilePath()));
        list.add(Paths.get(apiConfig.getPath1080()));
        list.add(Paths.get(apiConfig.getUploadTempPath()));
        list.forEach(v ->{
            if (!v.toFile().exists()) {
                try {
                    Files.createDirectories(v);
                } catch (IOException e) {
                    throw new ApiInitException(e);
                }
            }
        });
    }
}
