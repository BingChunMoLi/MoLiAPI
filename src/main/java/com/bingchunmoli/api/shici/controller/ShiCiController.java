package com.bingchunmoli.api.shici.controller;


import com.bingchunmoli.api.bean.ApiConstant;
import com.bingchunmoli.api.shici.service.IShiCiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author 冰纯茉莉
 * @since 2020-11-11
 */
@RestController
@RequestMapping("shici")
public class ShiCiController {
    @Autowired
    IShiCiService shiCiService;

    @Autowired
    RedisTemplate<String,Object> redisTemplate;


    @GetMapping("{id}")
    public Object getShiCi(@PathVariable Integer id) {
        Object shiCi = redisTemplate.opsForList().index(ApiConstant.SHI_CI, id);
        if (shiCi == null) {
            return shiCiService.getById(id);
        }
        return shiCi;
    }

    /**
     * 从缓存中读取一条随机诗词，如果不存在从数据库读取
     *
     * @return 返回一条诗词
     */
    @GetMapping("random")
    public Object getRandomShiCi() {
        return shiCiService.findRandomShiCi();
    }
}

