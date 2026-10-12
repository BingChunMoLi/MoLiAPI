package com.bingchunmoli.api.img.service.img;

import com.bingchunmoli.api.bean.ApiConstant;
import com.bingchunmoli.api.config.ApiConfig;
import com.bingchunmoli.api.exception.ApiCacheException;
import com.bingchunmoli.api.exception.ApiImgException;
import com.bingchunmoli.api.img.service.ImgService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author BingChunMoLi
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ImgServiceImpl implements ImgService {

    private final ApiConfig apiConfig;
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public File getRandomImageFile(final String key, final String path) {
        try {
            final File cachedFile = getRandomImgByRedis(key);
            if (cachedFile.isFile() && cachedFile.canRead()) {
                return cachedFile;
            }
        } catch (DataAccessException | ApiCacheException exception) {
            log.debug("Random image cache unavailable for {}", key, exception);
        }
        try {
            return getRandomImgByFileSystem(path);
        } catch (IOException exception) {
            throw new ApiImgException(exception);
        }
    }

    @Override
    public BufferedImage getRandomImageByPc() {
        BufferedImage img = null;
        int i = 0;
        while (i < 3) {
            try {
                img = ImageIO.read(getRandomImgByRedis(ApiConstant.REDIS_PC_IMG_KEY));
            } catch (IOException e) {
                log.error("readPcImage", e);
            }
            if (img != null) {
                return img;
            }
            i++;
        }
        i = 0;
        while (i < 3) {
            try {
                return ImageIO.read(getRandomImgByFileSystem(apiConfig.getPcPath()));
            } catch (IOException e) {
                log.error("read pcImage", e);
            }
            i++;
        }
        return img;
    }

    @Override
    public BufferedImage getRandomImageMobile() {
        BufferedImage img = null;
        int i = 0;
        while (i < 3) {
            try {
                img = ImageIO.read(getRandomImgByRedis(ApiConstant.REDIS_MOBILE_IMG_KEY));
            } catch (IOException e) {
                log.error("readMobileImg", e);
            }
            if (img != null) {
                return img;
            }
            i++;
        }
        i = 0;
        while (i < 3) {
            try {
                return ImageIO.read(getRandomImgByFileSystem(apiConfig.getMobilePath()));
            } catch (IOException e) {
                log.error("readMobileImg", e);
            }finally {
                i++;
            }
        }
        return img;
    }



    @Override
    public File getRandomImgByFileSystem(final String path) throws IOException {
        if (path == null || path.isBlank()) {
            throw new ApiImgException("随机图路径未配置");
        }
        Path selected = null;
        long count = 0;
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(path))) {
            for (final Path candidate : stream) {
                final boolean readableFile = Files.isRegularFile(candidate) && Files.isReadable(candidate);
                if (readableFile && ThreadLocalRandom.current().nextLong(++count) == 0) {
                    selected = candidate;
                }
            }
        }
        if (selected == null) {
            throw new ApiImgException("随机图目录中没有可读取的文件");
        }
        return selected.toFile();
    }

    @Override
    public File getRandomImgByRedis(final String key) {
        final Long size = redisTemplate.opsForList().size(key);
        if (size == null || size <= 0) {
            throw new ApiCacheException("随机图缓存失效");
        }
        final Object cachedPath = redisTemplate.opsForList().index(key, ThreadLocalRandom.current().nextLong(size));
        if (!(cachedPath instanceof String imagePath) || imagePath.isBlank()) {
            throw new ApiCacheException("随机图缓存失效");
        }
        return new File(imagePath);
    }

    @Override
    public List<Path> getImgListByFileSystem(String path) throws IOException {
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(path))) {
            List<Path> list = new ArrayList<>();
            for (Path p : stream) {
                if (!Files.isDirectory(p, LinkOption.NOFOLLOW_LINKS)) {
                    list.add(p);
                }
            }
            return list;
        } catch (NoSuchFileException e) {
            log.warn("path:" + path + "--- 没有文件", e);
        }
        return Collections.emptyList();
    }
}
