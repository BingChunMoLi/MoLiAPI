package com.bingchunmoli.api.init.impl;

import com.bingchunmoli.api.exception.ApiInitException;
import com.bingchunmoli.api.init.InitService;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author moli
 */
@Order(1)
@Service
@Profile("!test")
@ConditionalOnClass(name = "com.google.firebase.FirebaseApp")
@ConditionalOnProperty(prefix = "moli.features", name = "firebase", havingValue = "true")
public class InitFcmServiceImpl implements InitService {
    @Value("${moli.firebase.credentials-location:classpath:google-service.json}")
    private Resource resource;

    @Override
    public void init() {
        FirebaseOptions options;
        try (InputStream inputStream = resource.getInputStream()) {
            options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(inputStream))
                    .build();
            FirebaseApp.initializeApp(options);
        } catch (IOException e) {
            throw new ApiInitException(e);
        }
    }

    @Override
    public Integer getOrder() {
        return 10;
    }
}
