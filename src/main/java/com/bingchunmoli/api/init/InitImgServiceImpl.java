package com.bingchunmoli.api.init;

import com.bingchunmoli.api.img.task.ImgTask;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author MoLi
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class InitImgServiceImpl implements InitService{
    private final ImgTask imgTask;

    @Override
    public void init() {
        imgTask.saveImg();
    }
}
