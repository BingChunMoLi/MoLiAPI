package com.bingchunmoli.api.navigation.servcie;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bingchunmoli.api.navigation.bean.Navigation;
import com.bingchunmoli.api.navigation.bean.NavigationPO;

import java.util.List;

/**
 * @author MoLi
 */
public interface NavigationService extends IService<NavigationPO> {

    /**
     * 查询所有导航
     * @param tenant 租户
     * @return
     */
    List<Navigation> getNavigationsList(String tenant);

    boolean addNavigation(Navigation navigation);


    boolean updateNavigation(Navigation navigation);

    boolean deletedNavigation(Integer id);

    boolean importNavigation();
}
