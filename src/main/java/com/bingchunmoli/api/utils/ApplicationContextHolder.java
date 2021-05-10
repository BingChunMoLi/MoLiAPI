package com.bingchunmoli.api.utils;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * @copyright(c) 2017-2020 冰纯茉莉
 * @Description: TODO
 * @Author 冰彦糖
 * @Data 2020/11/12 13:50
 * @ClassName ApplicationContextHolder
 * @PackageName: com.bingchunmoli.api.com.bingchunmoli.api.utils
 * @Version 0.0.1-SNAPSHOT
 **/
@Component
public class ApplicationContextHolder implements ApplicationContextAware {
    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext ctx) throws BeansException {
        applicationContext = ctx;
    }

    /**
     * Get application context from everywhere
     *
     * @return
     */
    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    /**
     * Get bean by class
     *
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> T getBean(Class<T> clazz) {
        return applicationContext.getBean(clazz);
    }

    /**
     * Get bean by class name
     *
     * @param name
     * @param <T>
     * @return
     */

    public static <T> T getBean(String name) {
        return (T) applicationContext.getBean(name);
    }
}