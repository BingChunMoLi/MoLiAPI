package com.bingchunmoli.api.yiyan.controller;

import com.bingchunmoli.api.bean.ApiConstant;
import com.bingchunmoli.api.yiyan.service.IYiYanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

/**
 * @author BingChunMoLi
 */
@RestController
@RequestMapping("yiyan")
public class YiYanController {

    @Autowired
    private IYiYanService yiyanService;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @GetMapping("{id}")
    public Object getYiYan(@PathVariable Integer id) {
        Object yiYan = redisTemplate.opsForList().index(ApiConstant.YI_YAN, id);
        if (yiYan == null) {
            return yiyanService.getById(id);
        }
        return yiYan;
    }

    //    @GetMapping
//    public List<Yiyan> getYiYanAll(){
//        return yiyanService.list();
//    }


    /**
     * 查询随机一条一言数据
     *
     * @return 一条一言
     */
    @GetMapping("random")
    @CrossOrigin
    public Object getRandomYiYan() {
        return yiyanService.findRandomYiYan();
    }
}
