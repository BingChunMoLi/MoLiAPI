package com.bingchunmoli.api.down.factory;


import com.bingchunmoli.api.down.service.UploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class UploadFactory {
    @Autowired
    private Map<String, UploadService> map = new HashMap<>(2);

    public UploadService getUploadService(String upload) {
        return map.get(upload);
    }
}
