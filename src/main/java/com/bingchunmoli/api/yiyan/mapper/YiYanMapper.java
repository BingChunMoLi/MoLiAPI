package com.bingchunmoli.api.yiyan.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bingchunmoli.api.yiyan.bean.YiYan;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * @author 冰纯茉莉
 * @since 2020-11-11
 */
@Mapper
public interface YiYanMapper extends BaseMapper<YiYan> {

    /**
     * description: 查询随机一条一言
     * author: BingChunMoLi
     * date: 2020/11/12 20:09
     * version: 0.0.1-SNAPSHOT
     *
     * @return com.bingchunmoli.api.entity.Yiyan
     */
    @Select("SELECT * FROM yi_yan as t1 WHERE t1.id>=(RAND()*(SELECT MAX(id) FROM yi_yan))LIMIT 1")
    YiYan findRandom();
}
