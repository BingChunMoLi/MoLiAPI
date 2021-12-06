package com.bingchunmoli.api.cache;

import com.bingchunmoli.api.utils.ApplicationContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.cache.Cache;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author bingchunmoli
 **/
@Slf4j
public class RedisCache implements Cache {
    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    private final String id;
    private RedisTemplate redisTemplate;
    private static final long EXPIRE_TIME_IN_MINUTES = 30;

    public RedisCache(String id) {
        if (id == null) {
            throw new IllegalArgumentException("Cache instances require an ID");
        }
        this.id = id;
    }

    @Override
    public String getId() {
        return id;
    }

    /**
     * Put query result to redis
     *
     * @param key
     * @param value
     */
    @Override
    public void putObject(Object key, Object value) {
        try {
            RedisTemplate redisTemplate = getRedisTemplate();
            redisTemplate.opsForValue().set(key, value);
            log.debug("Put query result to redis");
        } catch (Throwable t) {
            log.error("Redis put failed", t);
        }
    }

    /**
     * Get cached query result from redis
     *
     * @param key
     * @return
     */
    @Override
    public Object getObject(Object key) {
        try {
            RedisTemplate redisTemplate = getRedisTemplate();
            log.debug("Get cached query result from redis");
            return redisTemplate.opsForValue().get(key);
        } catch (Throwable t) {
            log.error("Redis get failed, fail over to db", t);
            return null;
        }
    }

    /**
     * Remove cached query result from redis
     *
     * @param key
     * @return
     */
    @Override
    @SuppressWarnings("unchecked")
    public Object removeObject(Object key) {
        try {
            RedisTemplate redisTemplate = getRedisTemplate();
            redisTemplate.delete(key);
            log.debug("Remove cached query result from redis");
        } catch (Throwable t) {
            log.error("Redis remove failed", t);
        }
        return null;
    }

    /**
     * Clears this cache instance
     */
    @Override
    public void clear() {
        RedisTemplate redisTemplate = getRedisTemplate();
        redisTemplate.execute((RedisCallback) connection -> {
            connection.flushDb();
            return null;
        });
        log.debug("Clear all the cached query result from redis");
    }

    /**
     * This method is not used
     *
     * @return
     */
    @Override
    public int getSize() {
        return 0;
    }

    @Override
    public ReadWriteLock getReadWriteLock() {
        return readWriteLock;
    }

    private RedisTemplate getRedisTemplate() {
        if (redisTemplate == null) {
            redisTemplate = ApplicationContextHolder.getBean("redisTemplate");
        }
        return redisTemplate;
    }
}