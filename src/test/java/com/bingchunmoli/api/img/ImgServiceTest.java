package com.bingchunmoli.api.img;

import com.bingchunmoli.api.config.ApiConfig;
import com.bingchunmoli.api.exception.ApiCacheException;
import com.bingchunmoli.api.exception.ApiImgException;
import com.bingchunmoli.api.img.service.img.ImgServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ImgServiceTest {
    private static final String KEY = "images";

    @TempDir
    private Path directory;
    private ListOperations<String, Object> listOperations;
    private ImgServiceImpl service;

    @BeforeEach
    @SuppressWarnings("unchecked")
    void setUp() {
        final RedisTemplate<String, Object> redisTemplate = mock(RedisTemplate.class);
        listOperations = mock(ListOperations.class);
        when(redisTemplate.opsForList()).thenReturn(listOperations);
        service = spy(new ImgServiceImpl(new ApiConfig(), redisTemplate));
    }

    @Test
    void returnsCachedFileWithoutScanningDirectory() {
        final File image = createImage();
        when(listOperations.size(KEY)).thenReturn(1L);
        when(listOperations.index(KEY, 0)).thenReturn(image.getPath());

        assertEquals(image, service.getRandomImageFile(KEY, "missing-directory"));
        assertDoesNotThrow(() -> verify(service, never()).getRandomImgByFileSystem(anyString()));
    }

    @Test
    void fallsBackToDirectoryWhenRedisIsUnavailable() {
        final File image = createImage();
        when(listOperations.size(KEY)).thenThrow(new RedisConnectionFailureException("unavailable"));

        assertEquals(image, service.getRandomImageFile(KEY, directory.toString()));
    }

    @Test
    void fallsBackToDirectoryWhenCacheIsEmpty() {
        final File image = createImage();
        when(listOperations.size(KEY)).thenReturn(0L);

        assertEquals(image, service.getRandomImageFile(KEY, directory.toString()));
        verify(listOperations, never()).index(eq(KEY), anyLong());
    }

    @Test
    void fallsBackWhenCachedFileWasRemoved() {
        final File image = createImage();
        when(listOperations.size(KEY)).thenReturn(1L);
        when(listOperations.index(KEY, 0)).thenReturn(directory.resolve("removed.jpg").toString());

        assertEquals(image, service.getRandomImageFile(KEY, directory.toString()));
    }

    @Test
    void rejectsMissingAndInvalidCacheEntries() {
        assertThrows(ApiCacheException.class, () -> service.getRandomImgByRedis(KEY));
        when(listOperations.size(KEY)).thenReturn(1L);
        assertThrows(ApiCacheException.class, () -> service.getRandomImgByRedis(KEY));
        when(listOperations.index(KEY, 0)).thenReturn(" ");
        assertThrows(ApiCacheException.class, () -> service.getRandomImgByRedis(KEY));
        when(listOperations.index(KEY, 0)).thenReturn(123);
        assertThrows(ApiCacheException.class, () -> service.getRandomImgByRedis(KEY));
    }

    @Test
    void supportsCacheSizesBeyondIntegerRange() {
        final File image = createImage();
        when(listOperations.size(KEY)).thenReturn((long) Integer.MAX_VALUE + 1);
        when(listOperations.index(eq(KEY), anyLong())).thenReturn(image.getPath());

        assertEquals(image, service.getRandomImgByRedis(KEY));
    }

    @Test
    void samplesFilesWithoutLoadingAPathListOrSelectingDirectories() {
        final File image = createImage();
        assertDoesNotThrow(() -> Files.createDirectory(directory.resolve("nested")));

        assertDoesNotThrow(() -> {
            assertEquals(image, service.getRandomImgByFileSystem(directory.toString()));
            verify(service, never()).getImgListByFileSystem(anyString());
        });
    }

    @Test
    void rejectsEmptyDirectoryAndUnconfiguredPath() {
        assertThrows(ApiImgException.class, () -> service.getRandomImgByFileSystem(directory.toString()));
        assertThrows(ApiImgException.class, () -> service.getRandomImgByFileSystem(null));
        assertThrows(ApiImgException.class, () -> service.getRandomImgByFileSystem(" "));
    }

    @Test
    void wrapsMissingDirectoryFailure() {
        assertThrows(ApiImgException.class,
                () -> service.getRandomImageFile(KEY, directory.resolve("missing").toString()));
    }

    private File createImage() {
        return assertDoesNotThrow(() -> Files.write(directory.resolve("image.jpg"), new byte[]{1, 2, 3}).toFile());
    }
}
