package com.bingchunmoli.api.config;

import org.junit.jupiter.api.Test;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Configuration;

import static org.assertj.core.api.Assertions.assertThat;

class FeaturePropertiesTest {
    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withUserConfiguration(TestConfiguration.class);

    @Test
    void disablesOptionalFeaturesByDefault() {
        contextRunner.run(context -> {
            final FeatureProperties properties = context.getBean(FeatureProperties.class);

            assertThat(properties.getStartupInitialization()).isFalse();
            assertThat(properties.getYiYanPreload()).isFalse();
            assertThat(properties.getFirebase()).isFalse();
            assertThat(properties.getScheduling()).isFalse();
            assertThat(properties.getTasks().getTencentCdnCertificate()).isFalse();
        });
    }

    @Test
    void bindsExplicitFeatureSwitches() {
        contextRunner
                .withPropertyValues(
                        "moli.features.startup-initialization=true",
                        "moli.features.yi-yan-preload=true",
                        "moli.features.scheduling=true",
                        "moli.features.tasks.bing=true",
                        "moli.features.tasks.netease-music=true")
                .run(context -> {
                    final FeatureProperties properties = context.getBean(FeatureProperties.class);

                    assertThat(properties.getStartupInitialization()).isTrue();
                    assertThat(properties.getYiYanPreload()).isTrue();
                    assertThat(properties.getScheduling()).isTrue();
                    assertThat(properties.getTasks().getBing()).isTrue();
                    assertThat(properties.getTasks().getNeteaseMusic()).isTrue();
                });
    }

    @Configuration(proxyBeanMethods = false)
    @EnableConfigurationProperties(FeatureProperties.class)
    static class TestConfiguration {
    }
}
