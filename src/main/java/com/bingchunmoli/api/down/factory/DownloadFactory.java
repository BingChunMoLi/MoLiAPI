package com.bingchunmoli.api.down.factory;


import com.bingchunmoli.api.down.service.DownloadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * @author BingChunMoLi
 */
@Service
public class DownloadFactory {
    @Autowired
    private Map<String, DownloadService> map = new HashMap<>(2);

    public DownloadService getDownloadService(String download) {
        return map.get(download);
    }
}
