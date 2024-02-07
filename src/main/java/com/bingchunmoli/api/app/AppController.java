package com.bingchunmoli.api.app;


import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.bingchunmoli.api.app.bean.DeviceVO;
import com.bingchunmoli.api.bean.ResultVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;


@Slf4j
@RestController
@RequiredArgsConstructor
public class AppController {
    private final DeviceService deviceService;

    @PostMapping(value = "/app")
    @ResponseStatus(HttpStatus.CREATED)
    public ResultVO<Boolean> saveOrUpdateDevice(@RequestBody DeviceVO device){
        return ResultVO.ok(deviceService.saveOrUpdate(device));
    }

    @GetMapping("/app/{name}/{model}/{androidId}")
    public ResultVO<Integer> getDevice(@PathVariable String name, @PathVariable String model, @PathVariable String androidId){
        DeviceVO from = new DeviceVO()
                .setName(name)
                .setModel(model)
                .setAndroidId(androidId);
        DeviceVO device = Optional.ofNullable(deviceService.getOne(Wrappers.lambdaQuery(from))).orElse(from);
        if (device.getId() == null || device.getId() < 1) {
            deviceService.save(device);
        }
        return ResultVO.ok(device.getId());
    }
}
