package com.bingchunmoli.api.init;

import java.nio.file.Path;
import java.util.List;

/**
 * 初始化数据Service
 * @author MoLi
 */
public interface InitDataService<T> extends InitSqlService {

    /**
     * 读取数据列表
     * @return 数据列表
     */
    List<T> readAll();

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

}
