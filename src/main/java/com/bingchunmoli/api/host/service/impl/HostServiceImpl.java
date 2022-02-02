package com.bingchunmoli.api.host.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bingchunmoli.api.host.bean.Host;
import com.bingchunmoli.api.host.bean.HostTypeEnum;
import com.bingchunmoli.api.host.mapper.HostMapper;
import com.bingchunmoli.api.host.service.IHostService;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;

/**
 * @author BingChunMoLi
 */
@Service
public class HostServiceImpl extends ServiceImpl<HostMapper, Host> implements IHostService {

    @Override
    public String getHosts(List<Integer> type) {
        for (Integer i:type) {
            if (i == 0) {
                return getHostString(list());
            }
        }
        LambdaQueryWrapper<Host> queryWrapper = new LambdaQueryWrapper<>();
        type.forEach(t ->{
            //是否需要elseError 抛出请求参数异常，不受支持的类型
            queryWrapper.eq(Host::getSource, Arrays.stream(HostTypeEnum.values()).filter(v -> Objects.equals(v.getType(), t)).findFirst().orElse(null));
        });
        return getHostString(list(queryWrapper));
    }

    private String getHostString(List<Host> list) {
        int t = 0;
        StringJoiner stringJoiner = new StringJoiner("", "#start\n#@author BingChunMoLi", "\n#end");
        String source = null;
        StringBuilder stringBuilder = new StringBuilder();
        for (Host host: list) {
            if (!Objects.equals(host.getSource(), source)) {
                source = host.getSource();
                stringBuilder.setLength(0);
                stringBuilder.append("\n#");
                stringBuilder.append(source);
                stringBuilder.append("--start\n");
                stringJoiner.add(stringBuilder);
            }
            stringBuilder.setLength(0);
            if (t % 2 == 0) {
                stringBuilder.append(host.getIp());
                stringBuilder.append("\t");
            }else {
                stringBuilder.append(host.getDomain());
                stringBuilder.append("\n");
            }
            stringJoiner.add(stringBuilder);
            t++;
        }
        return stringJoiner.toString();
    }
}
