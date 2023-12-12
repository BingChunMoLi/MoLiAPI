package com.bingchunmoli.api.push;

import com.bingchunmoli.api.push.bean.Message;
import com.bingchunmoli.api.push.bean.PushLog;
import com.bingchunmoli.api.push.bean.enums.PushLogStatusEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author moli
 */
@Slf4j
@RequiredArgsConstructor
public class PushLoggingWrapper implements Push {
    private final Push push;
    private final PushLogService pushLogService;

    @Override
    public String send(Message message) {
        PushLog pushLog = new PushLog()
                .setType(message.getType().getType())
                .setTitle(message.getTitle())
                .setBody(message.getBody())
                .setReceive(message.getReceive())
                .setStatus(PushLogStatusEnum.CREATED.getStatus());
        pushLogService.save(pushLog);
        String res;
        try {
            res = push.send(message);
        } catch (Exception e) {
            pushLogService.updateById(pushLog.setStatus(PushLogStatusEnum.FAIL.getStatus()));
            throw e;
        }
        pushLogService.updateById(pushLog.setStatus(PushLogStatusEnum.SUCCESS.getStatus()));
        return res;
    }

    @Override
    public boolean support(Message message) {
        return push.support(message);
    }
}
