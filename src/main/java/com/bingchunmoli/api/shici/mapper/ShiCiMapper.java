package com.bingchunmoli.api.shici.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bingchunmoli.api.shici.bean.ShiCi;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface ShiCiMapper extends BaseMapper<ShiCi> {

    @Select("SELECT * FROM shi_ci WHERE deleted = 0 ORDER BY id LIMIT 1 OFFSET #{offset}")
    ShiCi findAtOffset(@Param("offset") final long offset);
}
