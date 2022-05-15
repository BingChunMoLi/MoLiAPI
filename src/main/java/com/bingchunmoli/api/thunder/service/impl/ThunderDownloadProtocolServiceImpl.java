package com.bingchunmoli.api.thunder.service.impl;

import com.bingchunmoli.api.thunder.bean.DownloadProtocolConstant;
import com.bingchunmoli.api.thunder.service.ThunderDownloadProtocolService;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * @author MoLi
 */
@Service
public class ThunderDownloadProtocolServiceImpl implements ThunderDownloadProtocolService {
    @Override
    public String toRaw(String thunderURL) {
        String downloadProtocol = thunderURL.substring(10);
        String rawTemp = new String(Base64.getDecoder().decode(downloadProtocol), StandardCharsets.UTF_8);
        return rawTemp.substring(2, rawTemp.length() - 2);
    }

    @Override
    public String toThunder(String rawURL) {
        return  DownloadProtocolConstant.THUNDER.getProtocol() + new String(Base64.getEncoder().encode(("AA" + rawURL + "ZZ").getBytes(StandardCharsets.UTF_8)));
    }
}
