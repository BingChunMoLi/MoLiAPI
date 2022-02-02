package com.bingchunmoli.api.host.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bingchunmoli.api.host.bean.Host;

import java.util.List;

/**
 * @author BingChunMoLi
 */
public interface IHostService extends IService<Host> {

    /**
     * 根据请求类型 获得hosts的String
     * @param type 请求类型
     * @return hostsString
     */
    String getHosts(List<Integer> type);
}
