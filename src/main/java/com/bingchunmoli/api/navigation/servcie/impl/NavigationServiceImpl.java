package com.bingchunmoli.api.navigation.servcie.impl;

import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
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
    public boolean addNavigation(final Navigation navigation) {
        final int navigationInsert = baseMapper.insert(navigation);
        if (navigationInsert < 1) {
            throw new RuntimeException("添加收藏失败");
        }
        final List<TagPO> tags = navigation.getTagList();
        if (tags == null || tags.isEmpty()) {
            return true;
        }
        final int tagsInsert = baseMapper.saveBatchTags(tags);
        if (tagsInsert < 1) {
            throw new RuntimeException("添加标签失败");
        }
        final List<Integer> tagIdList = tags.stream().map(TagPO::getId).toList();
        return baseMapper.saveNavigationTags(navigation.getId(), tagIdList) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateNavigation(final Navigation navigation) {
        if (!updateById(navigation)) {
            return false;
        }
        final List<TagPO> tags = navigation.getTagList();
        if (tags == null) {
            return true;
        }
        final List<TagPO> newTags = tags.stream().filter(tag -> tag.getId() == null).toList();
        final List<TagPO> existingTags = tags.stream().filter(tag -> tag.getId() != null).toList();
        if (!newTags.isEmpty()) {
            baseMapper.saveBatchTags(newTags);
        }
        if (!existingTags.isEmpty()) {
            baseMapper.updateBatchTags(existingTags);
        }
        baseMapper.removeNavigationTagsByNavigationId(navigation.getId());
        if (tags.isEmpty()) {
            return true;
        }
        final List<Integer> tagIdList = tags.stream().map(TagPO::getId).toList();
        return baseMapper.saveNavigationTags(navigation.getId(), tagIdList) > 0;
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
