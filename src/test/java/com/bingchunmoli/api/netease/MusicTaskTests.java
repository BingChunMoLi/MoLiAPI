package com.bingchunmoli.api.netease;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author moli
 */
@SpringBootTest
public class MusicTaskTests {
    @Autowired
    MusicService musicService;

    @Test
    void task() {
        musicService.savePlayList();
    }
}
