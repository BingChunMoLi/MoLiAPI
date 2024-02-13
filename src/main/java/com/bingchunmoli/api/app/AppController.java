package com.bingchunmoli.api.app;


import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.bingchunmoli.api.app.bean.DeviceVO;
import com.bingchunmoli.api.bean.ResultVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "app")
@RequiredArgsConstructor
public class AppController {
    private final DeviceService deviceService;

    @PostMapping(value = "/app")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "保存并更新device信息")
    public ResultVO<Boolean> saveOrUpdateDevice(@RequestBody DeviceVO device){
        return ResultVO.ok(deviceService.saveOrUpdate(device));
    }

    @GetMapping("/app/{name}/{model}/{androidId}")
    @Operation(summary = "根据name, model, androidIds, 查询设备")
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
