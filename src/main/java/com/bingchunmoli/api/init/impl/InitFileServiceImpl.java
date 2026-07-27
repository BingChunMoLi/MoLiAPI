package com.bingchunmoli.api.init.impl;

import com.bingchunmoli.api.config.ApiConfig;
import com.bingchunmoli.api.exception.ApiInitException;
import com.bingchunmoli.api.init.InitService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;

/**
 * @author MoLi
 */

@Slf4j
@Service
@ConditionalOnProperty(prefix = "moli.features", name = "startup-initialization", havingValue = "true")
@RequiredArgsConstructor
public final class InitFileServiceImpl implements InitService {
    private final ApiConfig apiConfig;

    @Override
    public void init() {
        final List<Path> paths = Stream.of(
                        apiConfig.getPcPath(),
                        apiConfig.getMobilePath(),
                        apiConfig.getPath1080(),
                        apiConfig.getUploadTempPath())
                .filter(StringUtils::hasText)
                .map(Paths::get)
                .toList();
        paths.forEach(path -> {
            if (!path.toFile().exists()) {
                try {
                    Files.createDirectories(path);
                } catch (IOException e) {
                    throw new ApiInitException(e);
                }
            }
        });
    }

    @Override
    public Integer getOrder() {
        return 1;
    }
}
