package com.bingchunmoli.api.yiyan.controller;

import com.bingchunmoli.api.bean.ApiConstant;
import com.bingchunmoli.api.yiyan.service.IYiYanService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

/**
 * @author BingChunMoLi
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("yiyan")
public class YiYanController {
    private final IYiYanService yiYanService;
    private final RedisTemplate<String, Object> redisTemplate;

    @GetMapping("{id}")
    public Object getYiYan(@PathVariable Integer id) {
        Object yiYan = redisTemplate.opsForList().index(ApiConstant.YI_YAN, id);
        if (yiYan == null) {
            return yiYanService.getById(id);
        }
        return yiYan;
    }

    /**
     * 查询随机一条一言数据
     *
     * @return 一条一言
     */
    @GetMapping("random")
    @CrossOrigin
    public Object getRandomYiYan() {
        return yiYanService.findRandomYiYan();
    }
}
