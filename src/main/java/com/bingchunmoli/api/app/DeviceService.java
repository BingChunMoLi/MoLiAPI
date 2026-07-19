package com.bingchunmoli.api.app;

import com.baomidou.mybatisplus.spring.service.IService;
import com.bingchunmoli.api.app.bean.AppPushTarget;
import com.bingchunmoli.api.app.bean.DeviceVO;

import java.util.Optional;

/**
* @author MoLi
*/
public interface DeviceService extends IService<DeviceVO> {
    Optional<AppPushTarget> getDefaultPushTarget();
}