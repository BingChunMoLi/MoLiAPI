package com.bingchunmoli.api.daily.controller;

import cn.hutool.core.collection.CollectionUtil;
import com.bingchunmoli.api.app.DeviceService;
import com.bingchunmoli.api.bean.ResultVO;
import com.bingchunmoli.api.bean.enums.CodeEnum;
import com.bingchunmoli.api.daily.bean.DailyLog;
import com.bingchunmoli.api.daily.service.DailyLogService;
import com.bingchunmoli.api.even.MessageEven;
import com.bingchunmoli.api.push.bean.AppMessage;
import com.bingchunmoli.api.push.bean.enums.AppMessageEnum;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
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
@Tag(name = "每日签到", description = "也可以是文本存储器,便签")
@RequestMapping("daily")
@RequiredArgsConstructor
public class DailyController {

    private final DailyLogService dailyLogService;
    private final DeviceService deviceService;
    private final ApplicationEventPublisher applicationEventPublisher;
    Map<String, Collection<String>> map = new HashMap<>(Map.of("moli", List.of("https://keylol.com/t735968-1-1", "https://www.52pojie.cn/", "https://www.bilibili.com/")));

    /**
     * 获取每日签到网址列表
     * @param key 存储的key default moli
     * @return 签到网址列表
     */
    @GetMapping
    @Operation(summary = "获取每日签到的地址列表")
    public ResultVO<Collection<String>> getDailys(String key) {
        return ResultVO.ok(map.get(key) == null ? null : map.get(key));
    }

    /**
     * 添加签到网址列表
     * @param daily 一个网址
     * @return 存储的列表
     */
    @PutMapping
    @Operation(summary = "添加需要签到的网址")
    public ResultVO<Collection<String>> addDaily(@RequestBody Daily daily){
        if (map.containsKey(daily.getKey())) {
            map.get(daily.getKey()).add(daily.getValue());
        }else {
            map.put(daily.getKey(), Stream.of(daily.getValue()).collect(Collectors.toList()));
        }
        return ResultVO.ok(map.get(daily.getKey()));
    }

    @PutMapping("all")
    @Operation(summary = "添加多个每日签到网址")
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
    @Operation(summary = "签到后的回调，用于通知app")
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
        AppMessage appMessage = new AppMessage()
                .setTitle("签到成功")
                .setBody(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .setAppMessageEnum(AppMessageEnum.DEVICE_ID);
        deviceService.getDefaultToken().ifPresentOrElse(v -> appMessage.setDeviceToken(v), () -> appMessage.setDefaultTopic());
        applicationEventPublisher.publishEvent(new MessageEven(this, appMessage));
        return ResultVO.ok(dailyLogService.saveBatch(list));
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
