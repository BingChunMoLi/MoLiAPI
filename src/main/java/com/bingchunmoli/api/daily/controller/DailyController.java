package com.bingchunmoli.api.daily.controller;

import cn.hutool.core.collection.CollectionUtil;
import com.bingchunmoli.api.bean.ResultVO;
import com.bingchunmoli.api.bean.enums.CodeEnum;
import com.bingchunmoli.api.daily.bean.DailyLog;
import com.bingchunmoli.api.daily.service.DailyLogService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 每日签到网址(临时文本存储读取)
 * @author MoLi
 */
@RestController
@RequestMapping("daily")
@RequiredArgsConstructor
public class DailyController {

    private final DailyLogService dailyLogService;
    Map<String, Collection<String>> map = new HashMap<>(Map.of("moli", List.of("https://keylol.com/t735968-1-1", "https://www.52pojie.cn/", "https://www.bilibili.com/")));

    /**
     * 获取每日签到网址列表
     * @param key 存储的key default moli
     * @return 签到网址列表
     */
    @GetMapping
    public ResultVO<Collection<String>> getDailys(String key) {
        return ResultVO.ok(map.get(key) == null ? null : map.get(key));
    }

    /**
     * 添加签到网址列表
     * @param daily 一个网址
     * @return 存储的列表
     */
    @PutMapping
    public ResultVO<Collection<String>> addDaily(@RequestBody Daily daily){
        if (map.containsKey(daily.getKey())) {
            map.get(daily.getKey()).add(daily.getValue());
        }else {
            map.put(daily.getKey(), Stream.of(daily.getValue()).collect(Collectors.toList()));
        }
        return ResultVO.ok(map.get(daily.getKey()));
    }

    @PutMapping("all")
    public ResultVO<Collection<Daily>> addDailyAll(@RequestBody List<Daily> dailies) {
        dailies.forEach(daily -> {
            if (map.containsKey(daily.getKey())) {
                map.get(daily.getKey()).add(daily.getValue());
            }else {
                map.put(daily.getKey(), Stream.of(daily.getValue()).collect(Collectors.toList()));
            }
        });
        return ResultVO.ok(dailies);
    }

    /**
     * 签到回调
     * @return 是否成功
     */
    @PostMapping("signed")
    public ResultVO<Boolean> signed(@RequestBody List<String> urls){
        if (CollectionUtil.isEmpty(urls)) {
            return new ResultVO<>(CodeEnum.ERROR, null);
        }
        HashMap<String, List<String>> collectMap = new HashMap<>();
        //反转map结构
        map.forEach((key, value) -> value.forEach(f -> {
            List<String> mapOrDefault = collectMap.getOrDefault(f, new ArrayList<>());
            mapOrDefault.add(key);
            collectMap.put(f, mapOrDefault);
        }));
        ArrayList<DailyLog> list = new ArrayList<>();
        //循环构建DailyLog
        urls.forEach(v -> {
            Collection<String> tenants = map.getOrDefault(v, Collections.singleton(""));
            String tenant = tenants.stream().filter(k -> k.equalsIgnoreCase("moli")).findFirst().orElse("0");
            list.add(new DailyLog()
                    .type(1)
                    .url(v)
                    .tenant(tenant.equals("moli") ? 1 : 0)
                    .createTime(LocalDateTime.now()));
        });
        return ResultVO.ok( dailyLogService.saveBatch(list));
    }
}

/**
 * 网址实体k,v一对多
 */
@Data
@AllArgsConstructor
class Daily{
    private String key;
    private String value;
}
