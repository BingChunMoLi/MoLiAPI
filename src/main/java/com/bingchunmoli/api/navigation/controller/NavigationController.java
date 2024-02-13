package com.bingchunmoli.api.navigation.controller;

import com.bingchunmoli.api.bean.ResultVO;
import com.bingchunmoli.api.navigation.bean.Navigation;
import com.bingchunmoli.api.navigation.servcie.NavigationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author MoLi
 */
@Slf4j
@RestController
@Tag(name = "网址导航")
@RequiredArgsConstructor
@RequestMapping("navigation")
public class NavigationController {
    private final NavigationService navigationService;

    /**
     * 查询所有导航站
     * @return 所有导航
     */
    @GetMapping
    @Operation(summary = "根据请求头中的tenant查询所有导航")
    public ResultVO<List<Navigation>> getNavigationsList(@RequestHeader(required = false, defaultValue = "public") String tenant){
        return ResultVO.ok(navigationService.getNavigationsList(tenant));
    }

    /**
     * 添加导航
     * @return 添加导航
     */
    @PutMapping
    @Operation(summary = "添加导航")
    public ResultVO<Boolean> addNavigation(@RequestBody @Valid Navigation navigation){
        return ResultVO.ok(navigationService.addNavigation(navigation));
    }

    /**
     * 修改导航
     *
     * @return 修改导航
     */
    @PostMapping("{id}")
    @Operation(summary = "修改导航")
    public ResultVO<Boolean> updateNavigation(@PathVariable Integer id, @RequestBody Navigation navigation) {
        navigation.setId(id);
        return ResultVO.ok(navigationService.updateNavigation(navigation));
    }

    @DeleteMapping("{id}")
    @Operation(summary = "删除导航")
    public ResultVO<Boolean> deleteNavigation(@PathVariable Integer id){
        return ResultVO.ok(navigationService.deletedNavigation(id));
    }

    @PostMapping("import")
    @Operation(summary = "导入导航")
    public ResultVO<Boolean> importNavigation(){
        return ResultVO.ok(navigationService.importNavigation());
    }

    @GetMapping("export")
    @Operation(summary = "导出导航")
    public ResultVO<List<Navigation>> exportNavigation(){
        return ResultVO.ok(navigationService.getNavigationsList("public"));
    }

}
