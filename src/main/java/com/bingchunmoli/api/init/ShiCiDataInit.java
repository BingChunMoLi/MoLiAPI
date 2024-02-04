package com.bingchunmoli.api.init;

import com.bingchunmoli.api.shici.service.ShiCiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * 一言，诗词，随机图初始化
 *
 * @author BingChunMoLi
 */
@Slf4j
@Order(5)
@Component
@RequiredArgsConstructor
public class ShiCiDataInit implements CommandLineRunner {
    private final ShiCiService shiCiService;

    @Override
    public void run(String... args) {
        shiCiService.setShiCiList(shiCiService.list());
    }
}
