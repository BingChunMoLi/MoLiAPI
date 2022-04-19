package com.bingchunmoli.api.img.service.img;

import com.bingchunmoli.api.bean.ApiConstant;
import com.bingchunmoli.api.exception.ApiCacheException;
import com.bingchunmoli.api.img.service.IImgService;
import com.bingchunmoli.api.properties.ApiKeyProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author BingChunMoLi
 */
@Service
@RequiredArgsConstructor
public class ImgServiceImpl implements IImgService {

    private final ApiKeyProperties apiKeyProperties;
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public BufferedImage getRandomImageByPc() {
        BufferedImage img = null;
        int i = 0;
        while (i < 3) {
            try {
                img = ImageIO.read(getRandomImgByRedis(ApiConstant.PC_IMG));
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (img != null) {
                return img;
            }
            i++;
        }
        i = 0;
        while (i < 3) {
            try {
                return ImageIO.read(getRandomImgByFileSystem(apiKeyProperties.getPcPath()));
            } catch (IOException e) {
                e.printStackTrace();
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
                e.printStackTrace();
            }
            if (img != null) {
                return img;
            }
            i++;
        }
        i = 0;
        while (i < 3) {
            try {
                return ImageIO.read(getRandomImgByFileSystem(apiKeyProperties.getMobilePath()));
            } catch (IOException e) {
                e.printStackTrace();
            }
            i++;
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
        DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(path));
        List<Path> list = new ArrayList<>();
        for (Path p:stream) {
            if (!Files.isDirectory(p, LinkOption.NOFOLLOW_LINKS)) {
                list.add(p);
            }
        }
        return list;
    }
}
