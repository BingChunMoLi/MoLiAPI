package com.bingchunmoli.api.app;

import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.bingchunmoli.api.app.bean.AppPushTarget;
import com.bingchunmoli.api.app.bean.DeviceVO;
import com.bingchunmoli.api.app.bean.PushTypeEnum;
import com.bingchunmoli.api.app.mapper.DeviceMapper;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
* @author MoLi
*/
@Service
public class DeviceServiceImpl extends ServiceImpl<DeviceMapper, DeviceVO> implements DeviceService {

    @Override
    public Optional<AppPushTarget> getDefaultPushTarget() {
        final DeviceVO defaultDevice = getById(1);
        return Optional.ofNullable(defaultDevice)
                .flatMap(this::getPushTarget);
    }

    private Optional<AppPushTarget> getPushTarget(final DeviceVO device) {
        final PushTypeEnum pushType = device.getPushTypeEnum();
        if (PushTypeEnum.HMS.equals(pushType) && device.getHmsToken() != null && !device.getHmsToken().isBlank()) {
            return Optional.of(new AppPushTarget(device.getHmsToken(), PushTypeEnum.HMS));
        }
        if (device.getToken() != null && !device.getToken().isBlank()) {
            return Optional.of(new AppPushTarget(device.getToken(), PushTypeEnum.FCM));
        }
        if (device.getHmsToken() != null && !device.getHmsToken().isBlank()) {
            return Optional.of(new AppPushTarget(device.getHmsToken(), PushTypeEnum.HMS));
        }
        return Optional.empty();
    }
}




