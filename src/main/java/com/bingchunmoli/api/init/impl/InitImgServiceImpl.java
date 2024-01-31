package com.bingchunmoli.api.init.impl;

import com.bingchunmoli.api.img.task.ImgTask;
import com.bingchunmoli.api.init.InitService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

/**
 * @author MoLi
 */
@Slf4j
@Service
@Profile({"!test", "!dev"})
@RequiredArgsConstructor
public class InitImgServiceImpl implements InitService {
    private final ImgTask imgTask;

    @Override
    public void init() {
        imgTask.saveImg();
    }

    @Override
    public Integer getOrder() {
        return 100;
    }
}
