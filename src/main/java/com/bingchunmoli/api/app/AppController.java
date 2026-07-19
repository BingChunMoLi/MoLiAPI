package com.bingchunmoli.api.app;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.bingchunmoli.api.app.bean.DeviceVO;
import com.bingchunmoli.api.app.bean.PushTypeEnum;
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
    @Operation(summary = "保存并更新 device 信息")
    public ResultVO<Boolean> saveOrUpdateDevice(@RequestBody final DeviceVO device) {
        fillDefaultPushType(device);
        mergeExistingDevice(device);
        return ResultVO.ok(deviceService.saveOrUpdate(device));
    }

    @GetMapping("/app/{name}/{model}/{androidId}")
    @Operation(summary = "根据 name, model, androidId 查询设备")
    public ResultVO<Integer> getDevice(@PathVariable final String name, @PathVariable final String model,
                                       @PathVariable final String androidId) {
        final DeviceVO from = new DeviceVO()
                .setName(name)
                .setModel(model)
                .setAndroidId(androidId);
        final DeviceVO device = Optional.ofNullable(deviceService.getOne(Wrappers.lambdaQuery(from))).orElse(from);
        if (device.getId() == null || device.getId() < 1) {
            fillDefaultPushType(device);
            deviceService.save(device);
        }
        return ResultVO.ok(device.getId());
    }

    private void fillDefaultPushType(final DeviceVO device) {
        if (device.getPushType() != null && !device.getPushType().isBlank()) {
            device.setPushType(device.getPushTypeEnum().getType());
            return;
        }
        if (device.getHmsToken() != null && !device.getHmsToken().isBlank()) {
            device.setPushType(PushTypeEnum.HMS.getType());
            return;
        }
        device.setPushType(PushTypeEnum.FCM.getType());
    }

    private void mergeExistingDevice(final DeviceVO device) {
        if (device.getId() == null) {
            return;
        }
        final DeviceVO existingDevice = deviceService.getById(device.getId());
        if (existingDevice == null) {
            return;
        }
        if (device.getToken() == null || device.getToken().isBlank()) {
            device.setToken(existingDevice.getToken());
        }
        if (device.getHmsToken() == null || device.getHmsToken().isBlank()) {
            device.setHmsToken(existingDevice.getHmsToken());
        }
        if (device.getName() == null || device.getName().isBlank()) {
            device.setName(existingDevice.getName());
        }
        if (device.getModel() == null || device.getModel().isBlank()) {
            device.setModel(existingDevice.getModel());
        }
        if (device.getAndroidId() == null || device.getAndroidId().isBlank()) {
            device.setAndroidId(existingDevice.getAndroidId());
        }
    }
}
