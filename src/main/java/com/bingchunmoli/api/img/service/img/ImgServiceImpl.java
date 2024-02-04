package com.bingchunmoli.api.img.service.img;

import com.bingchunmoli.api.bean.ApiConstant;
import com.bingchunmoli.api.config.ApiConfig;
import com.bingchunmoli.api.exception.ApiCacheException;
import com.bingchunmoli.api.img.service.ImgService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import java.util.Random;

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
    public BufferedImage getRandomImageByPc() {
        BufferedImage img = null;
        int i = 0;
        while (i < 3) {
            try {
                img = ImageIO.read(getRandomImgByRedis(ApiConstant.PC_IMG));
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
                img = ImageIO.read(getRandomImgByRedis(ApiConstant.MOBILE_IMG));
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
    public File getRandomImgByFileSystem(String path) throws IOException {
        List<Path> list = getImgListByFileSystem(path);
        return list.get(new Random().nextInt(list.size())).toFile();
    }

    @Override
    public File getRandomImgByRedis(String key) {
        Long size = redisTemplate.opsForList().size(key);
        if (size == null) {
            throw new ApiCacheException("随机图缓存失效");
        }
        return new File(String.valueOf(redisTemplate.opsForList().index(key, new Random().nextInt(Math.toIntExact(size)))));
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
