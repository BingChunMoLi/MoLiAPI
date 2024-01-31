package com.bingchunmoli.api.app;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bingchunmoli.api.app.bean.DeviceVO;
import com.bingchunmoli.api.app.mapper.DeviceMapper;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
* @author MoLi
*/
@Service
public class DeviceServiceImpl extends ServiceImpl<DeviceMapper, DeviceVO> implements DeviceService{

    @Override
    public Optional<String> getDefaultToken() {
        DeviceVO defaultDevice = getById(1);
        return Optional.ofNullable(defaultDevice)
                .map(DeviceVO::getToken);
    }
}




