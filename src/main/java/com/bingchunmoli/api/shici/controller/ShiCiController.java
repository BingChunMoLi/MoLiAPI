package com.bingchunmoli.api.shici.controller;


import com.bingchunmoli.api.shici.bean.ShiCi;
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
    public ShiCi getShiCi(@PathVariable Integer id) {
        return shiCiService.getById(id);
    }

    //    @GetMapping
//    public List<Shici> getShiCiAll(){
//        return shiciService.list();
//    }
    @GetMapping("random")
    public ShiCi getRandomShiCi() {
        return shiCiService.findRandomShiCi();
    }
}

