package com.bingchunmoli.api.navigation.servcie.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bingchunmoli.api.navigation.bean.Navigation;
import com.bingchunmoli.api.navigation.bean.NavigationPO;
import com.bingchunmoli.api.navigation.bean.TagPO;
import com.bingchunmoli.api.navigation.mapper.NavigationMapper;
import com.bingchunmoli.api.navigation.servcie.NavigationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

/**
 * @author MoLi
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NavigationServiceImpl extends ServiceImpl<NavigationMapper, NavigationPO> implements NavigationService {

    @Override
    public List<Navigation> getNavigationsList(String tenant) {
        return this.baseMapper.getNavigationsList(tenant);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addNavigation(Navigation navigation) {
        int navigationInsert = baseMapper.insert(navigation);
        if (navigationInsert < 1) {
            throw new RuntimeException("添加收藏失败");
        }
        int tagsInsert = baseMapper.saveBatchTags(navigation.getTagList());
        if (tagsInsert < 1) {
            throw new RuntimeException("添加标签失败");
        }
        List<Integer> tagIdList = navigation.getTagList().stream().map(TagPO::getId).toList();
        return baseMapper.saveNavigationTags(navigation.getId(), tagIdList) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateNavigation(Navigation navigation) {
        updateById(navigation);
        baseMapper.saveBatchTags(navigation.getTagList().stream()
                .filter(v -> Objects.isNull(v.getId()))
                .toList());
        baseMapper.updateBatchTags(navigation.getTagList().stream()
                .filter(v -> !Objects.isNull(v.getId()))
                .toList());
        baseMapper.removeNavigationTagsByNavigationId(navigation.getId());
        baseMapper.saveBatchTags(navigation.getTagList());
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deletedNavigation(Integer id) {
        removeById(id);
        baseMapper.removeNavigationTagsByNavigationId(id);
        return true;
    }

    @Override
    public boolean importNavigation() {
        return false;
    }

}
