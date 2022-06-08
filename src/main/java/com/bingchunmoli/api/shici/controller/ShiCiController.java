package com.bingchunmoli.api.shici.controller;


import com.bingchunmoli.api.shici.service.IShiCiService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 诗词
 * @author 冰纯茉莉
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("shici")
public class ShiCiController {
    private final IShiCiService shiCiService;

    /**
     * 指定ID诗词
     * @param id id|1
     * @return 诗词|com.bingchunmoli.api.shici.bean.ShiCi
     * @response com.bingchunmoli.api.shici.bean.ShiCi
     */
    @GetMapping("{id}")
    public Object getShiCi(@PathVariable Integer id) {
        return shiCiService.getShiCi(id);
    }

    /**
     * 从缓存中读取一条随机诗词，如果不存在从数据库读取
     * @return 返回一条诗词|com.bingchunmoli.api.shici.bean.ShiCi
     */
    @GetMapping("random")
    public Object getRandomShiCi() {
        return shiCiService.findRandomShiCi();
    }
}

