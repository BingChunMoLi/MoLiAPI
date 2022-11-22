package com.bingchunmoli.api.init;

import com.bingchunmoli.api.exception.ApiInitException;

import java.nio.file.Path;
import java.util.List;

/**
 * 初始化数据Service
 * @author MoLi
 */
public interface InitDataService<T> extends InitService {

    /**
     * 读取数据列表
     * @return 数据列表
     */
    List<T> readAll();

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

    /**
     * 读取数据列表从文件
     * @return 数据列表
     */
    List<T> readAllDataByFile();

    /**
     * 读取数据列表从文件
     * @return 数据列表
     */
    List<T> readAllDataByFile(Path path);


    @Override
    default void init(){
        if (check()) {
            return;
        }
        doInit();
    }

    void doInit();

    /**
     * 检查是否已经初始化过等异常情况
     *
     * @return 如果初始化过为true 否则为false
     */
    boolean check() throws ApiInitException;

    /**
     * 初始化结构
     */
    void initSchema();
}
