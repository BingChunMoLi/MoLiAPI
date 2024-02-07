package com.bingchunmoli.api.down.service;


import com.bingchunmoli.api.down.bean.MediaPO;

import java.io.IOException;

/**
 * @author BingChunMoLi
 */
public interface DownloadService {


    Boolean downloadMedia(MediaPO media) throws IOException, InterruptedException;
}
