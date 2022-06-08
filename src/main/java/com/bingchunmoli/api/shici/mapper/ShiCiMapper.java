package com.bingchunmoli.api.shici.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bingchunmoli.api.shici.bean.ShiCi;
import org.apache.ibatis.annotations.Select;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author 冰纯茉莉
 * @since 2020-11-11
 */
public interface ShiCiMapper extends BaseMapper<ShiCi> {
    /**
     * description: 查询随机一条诗词
     * author: BingChunMoLi
     * date: 2020/11/12 20:17
     * version: 0.0.1-SNAPSHOT
     * * @param
     *
     * @return com.bingchunmoli.api.entity.Shici
     */
    @Select("SELECT * FROM shi_ci as t1 WHERE t1.id>=(RAND()*(SELECT MAX(id) FROM shi_ci))LIMIT 1")
    ShiCi findRandom();
}
