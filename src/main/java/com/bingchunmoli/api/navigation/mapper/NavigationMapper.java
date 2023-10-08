package com.bingchunmoli.api.navigation.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bingchunmoli.api.navigation.bean.NavigationPO;
import com.bingchunmoli.api.navigation.bean.Navigation;
import com.bingchunmoli.api.navigation.bean.TagPO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author MoLi
 */
public interface NavigationMapper extends BaseMapper<NavigationPO> {

    /**
     * 查询所有导航列表
     * @param tenant 租户
     * @return 所有导航
     */
    List<Navigation> getNavigationsList(String tenant);

    int saveBatchTags(List<TagPO> tagList);

    int saveNavigationTags(@Param("id") Integer id,@Param("tagIdList") List<Integer> tagIdList);

    int updateBatchTags(List<TagPO> list);

    int removeNavigationTagsByNavigationId(Integer id);
}
