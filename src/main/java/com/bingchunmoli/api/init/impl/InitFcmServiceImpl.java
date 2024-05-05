package com.bingchunmoli.api.init.impl;

import com.bingchunmoli.api.init.InitService;
import com.google.api.client.http.apache.v2.ApacheHttpTransport;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnResource;
import org.springframework.context.annotation.Profile;
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
@Profile("!test")
@RequiredArgsConstructor
@ConditionalOnResource(resources = "classpath:google-service.json")
public class InitFcmServiceImpl implements InitService {
    @Value("classpath:google-service.json")
    private Resource resource;

    @Override
    public void init() {
        FirebaseOptions options;
        try (InputStream inputStream = resource.getInputStream()){
            options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(inputStream))
                    .setHttpTransport(new ApacheHttpTransport(ApacheHttpTransport
                            .newDefaultHttpClientBuilder()
                            .setProxy(new HttpHost("127.0.0.1", 7890))
                            .build()))
                    .build();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        FirebaseApp.initializeApp(options);
    }

    @Override
    public Integer getOrder() {
        return 10;
    }
}
