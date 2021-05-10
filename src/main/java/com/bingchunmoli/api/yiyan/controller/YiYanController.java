package com.bingchunmoli.api.yiyan.controller;

import com.bingchunmoli.api.yiyan.bean.YiYan;
import com.bingchunmoli.api.yiyan.service.IYiYanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author BingChunMoLi
 */
@RestController
@RequestMapping("yiyan")
public class YiYanController {

    @Autowired
    IYiYanService yiyanService;

    @GetMapping("{id}")
    public YiYan getYiYan(@PathVariable Integer id) {
        return yiyanService.getById(id);
    }

    //    @GetMapping
//    public List<Yiyan> getYiYanAll(){
//        return yiyanService.list();
//    }

    @GetMapping("random")
    @CrossOrigin
    public String getRandomYiYan() {
        return yiyanService.findRandomYiYan();
    }
}
