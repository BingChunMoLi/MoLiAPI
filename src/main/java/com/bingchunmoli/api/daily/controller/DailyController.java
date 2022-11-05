package com.bingchunmoli.api.daily.controller;

import com.bingchunmoli.api.bean.ResultVO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    Map<String, Collection<String>> map = new HashMap<>(Map.of("moli", List.of("https://cloud.tencent.com/act/integralmall?from=13925", "https://keylol.com/t735968-1-1", "https://www.52pojie.cn/", "https://www.bilibili.com/")));

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
