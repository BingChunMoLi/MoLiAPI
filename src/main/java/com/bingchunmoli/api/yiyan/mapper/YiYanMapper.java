package com.bingchunmoli.api.yiyan.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bingchunmoli.api.yiyan.bean.YiYan;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * @author 冰纯茉莉
 * @since 2020-11-11
 */
@Mapper
public interface YiYanMapper extends BaseMapper<YiYan> {

    /**
     * randomYiYan
     * @return yiyan
     */
    @Select("SELECT * FROM yi_yan ORDER BY id LIMIT 1 OFFSET #{offset}")
    YiYan findAtOffset(@Param("offset") final long offset);
}
