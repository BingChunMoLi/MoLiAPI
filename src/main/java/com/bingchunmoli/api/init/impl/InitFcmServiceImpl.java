package com.bingchunmoli.api.init.impl;

import com.bingchunmoli.api.init.InitService;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author moli
 */
@Slf4j
@Order(1)
@Service
@RequiredArgsConstructor
public class InitFcmServiceImpl implements InitService {
    @Value("classpath:google-service.json")
    private Resource resource;

    @Override
    public void init() {
        System.setProperty("http.proxyHost", "127.0.0.1");
        System.setProperty("http.proxyPort", "7890");
        System.setProperty("https.proxyHost", "127.0.0.1");
        System.setProperty("https.proxyPort", "7890");
        FirebaseOptions options;
        try (InputStream inputStream = resource.getInputStream()){
            options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(inputStream))
                    .build();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        FirebaseApp.initializeApp(options);
    }
}
