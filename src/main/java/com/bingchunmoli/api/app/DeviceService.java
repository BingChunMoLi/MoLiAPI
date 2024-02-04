package com.bingchunmoli.api.app;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bingchunmoli.api.app.bean.DeviceVO;

import java.util.Optional;

/**
* @author MoLi
*/
public interface DeviceService extends IService<DeviceVO> {
    Optional<String> getDefaultToken();
}
