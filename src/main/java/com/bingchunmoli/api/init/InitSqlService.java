package com.bingchunmoli.api.init;

import com.bingchunmoli.api.exception.ApiInitException;

import java.nio.file.Path;

/**
 * @author MoLi
 */
public interface InitSqlService extends InitService{


    /**
     * 检查是否已经初始化过等异常情况
     *
     * @return 如果初始化过为true 否则为false
     */
    boolean check() throws ApiInitException;


    @Override
    default void init(){
        if (check()) {
            return;
        }
        doInit();
    }

    /**
     * 真正的Init
     */
    void doInit();

    /**
     * 初始化结构
     */
    void initSchema();

    /**
     * 读取数据从sql
     * @return 数据列表
     */
    void initDataBySql();

    /**
     * 读取数据从sql
     * @return 数据列表
     */
    void initDataBySql(Path path);

}
