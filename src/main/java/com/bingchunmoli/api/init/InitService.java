package com.bingchunmoli.api.init;

/**
 * 初始化Service,初始化顺序依赖于getOrder的值，值越小优先级越高
 * @author MoLi
 */
public interface InitService {

    /**
     * 初始化方法
     */
    void init();

    /**
     * 获取优先级order 值越小优先级越高
     * @return 优先级
     */
    Integer getOrder();
}
