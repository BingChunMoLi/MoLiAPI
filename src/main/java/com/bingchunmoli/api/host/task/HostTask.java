package com.bingchunmoli.api.host.task;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.bingchunmoli.api.host.bean.Host;
import com.bingchunmoli.api.host.service.IHostService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @author BingChunMoLi
 */
@Slf4j
@Component
public class HostTask {
    private static final String[] PREFIXS = {"https://github.com/", "https://hub.fastgit.org", "https://github.com.cnpmjs.org/", "https://ghproxy.com/"};
    private static final String NEO_HOST = " https://raw.githubusercontent.com/neoFelhz/neohosts/gh-pages/basic/hosts";
    private static final String NEO_HOST_2 = "https://cdn.jsdelivr.net/gh/neoFelhz/neohosts@gh-pages/basic/hosts";
    @Autowired
    private IHostService hostService;
    @Scheduled(cron = "0 0 0 * * ?")
    public void getHost(){
        String result = null;
        boolean resultStatus = true;
        while (resultStatus) {
            try {
                result = HttpUtil.downloadString(NEO_HOST, Charset.defaultCharset());
            } catch (Exception e) {
                log.info("githubusercontent e: {}", e.getLocalizedMessage());
            }
            try {
                if (result == null) {
                    result = HttpUtil.downloadString(NEO_HOST_2, Charset.defaultCharset());
                }
            } catch (Exception e) {
                log.info("jsdelivr e: {}", e.getLocalizedMessage());
            }
            if (result != null) {
                resultStatus = false;
            }
        }
        String[] strs = result.split("\n");
        List<String> host = new ArrayList<>(strs.length);
        for (String s : strs) {
            if (!s.startsWith("#") && StrUtil.isNotBlank(s)) {
                host.add(s);
            }
        }
        List<Host> list = new ArrayList<>(host.size());
        for (String h :host) {
            String[] split = h.split(" ");
            LambdaQueryWrapper<Host> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Host::getIp, split[0]);
            queryWrapper.eq(Host::getDomain, split[1]);
            Host one = hostService.getOne(queryWrapper);
            if (one == null) {
                Host hostPO = new Host(null, split[0], split[1], "neohostsBase");
                list.add(hostPO);
                log.debug("host: {}",hostPO);
            }
        }
        hostService.saveBatch(list);
        log.info("存储成功: {}", LocalDateTime.now());
    }

}
