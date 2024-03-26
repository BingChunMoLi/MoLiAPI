package com.bingchunmoli.api.daily.controller;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.bingchunmoli.api.app.DeviceService;
import com.bingchunmoli.api.bean.ResultVO;
import com.bingchunmoli.api.bean.enums.CodeEnum;
import com.bingchunmoli.api.daily.bean.DailyLog;
import com.bingchunmoli.api.daily.bean.DailyLogPO;
import com.bingchunmoli.api.daily.bean.DailyQuery;
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
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
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

    private static int getTenant(String tenant) {
        return tenant.equals("moli") ? 1 : 0;
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
     * 获取每日签到网址列表
     *
     * @param key 存储的key default moli
     * @return 签到网址列表
     */
    @GetMapping
    @Operation(summary = "获取每日签到的地址列表")
    public ResultVO<Collection<String>> getDailys(String key) {
        return ResultVO.ok(map.get(key));
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
        ArrayList<DailyLogPO> list = new ArrayList<>();
        //循环构建DailyLog
        urls.forEach(v -> {
            Collection<String> tenants = map.getOrDefault(v, Collections.singleton(""));
            String tenant = tenants.stream().filter(k -> k.equalsIgnoreCase("moli")).findFirst().orElse("0");
            list.add(new DailyLogPO()
                    .setType(1)
                    .setUrl(v)
                    .setTenant(getTenant(tenant))
                    .setCreateTime(LocalDateTime.now()));
        });
        AppMessage appMessage = new AppMessage()
                .setTitle("签到成功")
                .setBody(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .setAppMessageEnum(AppMessageEnum.DEVICE_ID);
        deviceService.getDefaultToken().ifPresentOrElse(appMessage::setDeviceToken, appMessage::setDefaultTopic);
        applicationEventPublisher.publishEvent(new MessageEven(this, appMessage));
        return ResultVO.ok(dailyLogService.saveBatch(list));
    }

    /**
     * 获取查询的请求参数, 比如开始时间与结束时间，以及可以查询的url
     *
     * @param url 可以根据url查询出最早的开始时间与最晚的结束时间
     * @return 查询参数
     */
    @GetMapping("param")
    @Operation(summary = "获取查询的请求参数, 比如开始时间与结束时间，以及可以查询的url")
    public ResultVO<DailyQuery> getQueryParam(String url, @RequestHeader(defaultValue = "moli") String tenant) {
        return ResultVO.ok(dailyLogService.getQueryParam(url, tenant));
    }

    /**
     * 获取当天签到状态
     *
     * @return 如果已签到，返回当日签到的url
     */
    @GetMapping("check")
    @Operation(summary = "获取当天签到状态")
    public ResultVO<List<String>> getNowSign(@RequestHeader(defaultValue = "moli") String tenant) {
        return ResultVO.ok(dailyLogService.list(new LambdaQueryWrapper<DailyLogPO>()
                        .eq(DailyLogPO::getCreateTime, LocalDate.now())
                        .eq(DailyLogPO::getTenant, getTenant(tenant)))
                .stream().map(DailyLogPO::getUrl)
                .toList());
    }

    /**
     * 查询签到日历表
     *
     * @param dailyQuery 查询参数
     * @return 日历表详情
     */
    @GetMapping("query")
    @Operation(summary = "查询签到日历表")
    public ResultVO<Map<LocalDate, List<DailyLog>>> querySign(DailyQuery dailyQuery, @RequestHeader(defaultValue = "moli") String tenant) {
        return ResultVO.ok(dailyLogService.querySign(dailyQuery, getTenant(tenant)));
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
