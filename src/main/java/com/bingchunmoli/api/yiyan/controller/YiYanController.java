package com.bingchunmoli.api.yiyan.controller;

import com.bingchunmoli.api.yiyan.bean.YiYan;
import com.bingchunmoli.api.yiyan.service.YiYanService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

/**
 * 一言
 * @author BingChunMoLi
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("yiyan")
public class YiYanController {
    private final YiYanService yiYanService;


    /**
     * 根据ID获取一言
     * @param id id|1
     * @return 一言数据
     */
    @GetMapping("{id}")
    public Optional<YiYan> getYiYan(@PathVariable Integer id) {
        return yiYanService.getOptById(id);
    }

    /**
     * 查询随机一条一言数据
     *
     * @return 一条一言
     */
    @GetMapping("random")
    public YiYan getRandomYiYan() {
        return yiYanService.findRandomYiYan();
    }
}
