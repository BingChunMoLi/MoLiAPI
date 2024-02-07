package com.bingchunmoli.api.down.service;

import java.util.concurrent.CountDownLatch;

/**
 * @author MoLi
 */
public interface InitUtilService {
    /**
     * 初始化百度网盘
     *
     * @param countDownLatch 多线程记录器
     */
    void initBaiduPCS(CountDownLatch countDownLatch);

    /**
     * 初始化B站
     *
     * @param countDownLatch 多线程记录器
     */
    void initBBDown(CountDownLatch countDownLatch);

    /**
     * 初始化网盘挂载器(onedrive)
     *
     * @param countDownLatch 多线程记录器
     */
    void initRclone(CountDownLatch countDownLatch);

    /**
     * 初始化you-get
     *
     * @param countDownLatch 多线程记录器
     */
    void initYouGet(CountDownLatch countDownLatch);

}
