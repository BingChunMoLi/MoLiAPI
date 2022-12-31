package com.bingchunmoli.api.navigation.controller;

import com.bingchunmoli.api.bean.ResultVO;
import com.bingchunmoli.api.navigation.bean.Navigation;
import com.bingchunmoli.api.navigation.servcie.NavigationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author MoLi
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("navigation")
public class NavigationController {

    private final NavigationService navigationService;

    /**
     * 查询所有导航站
     * @return 所有导航
     */
    @GetMapping
    public ResultVO<List<Navigation>> getNavigationsList(@RequestHeader(required = false, defaultValue = "public") String tenant){
        navigationService.list();
            return ResultVO.ok(navigationService.getNavigationsList(tenant));
    }

    /**
     * 添加导航
     * @return 添加导航
     */
    @PutMapping
    public ResultVO<Boolean> addNavigation(@RequestBody @Valid Navigation navigation){
        return ResultVO.ok(navigationService.addNavigation(navigation));
    }

    /**
     * 修改导航
     *
     * @return 修改导航
     */
    @PostMapping("{id}")
    public ResultVO<Boolean> updateNavigation(@PathVariable Integer id, @RequestBody Navigation navigation) {
        navigation.setId(id);
        return ResultVO.ok(navigationService.updateNavigation(navigation));
    }

    @DeleteMapping("{id}")
    public ResultVO<Boolean> deleteNavigation(@PathVariable Integer id){
        return ResultVO.ok(navigationService.deletedNavigation(id));
    }

    @PostMapping("import")
    public ResultVO<Boolean> importNavigation(){
        return ResultVO.ok(navigationService.importNavigation());
    }

    @GetMapping("export")
    public ResultVO<List<Navigation>> exportNavigation(){
        return ResultVO.ok(navigationService.getNavigationsList("public"));
    }

}
