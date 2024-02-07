package com.bingchunmoli.api.down.service;

import java.io.IOException;

/**
 * @author BingChunMoLi
 */

public interface UploadService {

    Boolean upload(String filePath) throws IOException, InterruptedException;
}
