package com.bingchunmoli.api.navigation.servcie.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bingchunmoli.api.navigation.bean.Navigation;
import com.bingchunmoli.api.navigation.bean.NavigationPO;
import com.bingchunmoli.api.navigation.mapper.NavigationMapper;
import com.bingchunmoli.api.navigation.servcie.NavigationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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
    public boolean addNavigation(Navigation navigation) {
        
        return false;
    }

    @Override
    public boolean updateNavigation(Navigation navigation) {
        return false;
    }

    @Override
    public boolean deletedNavigation(Integer id) {
        return false;
    }

    @Override
    public boolean importNavigation() {
        return false;
    }

}
