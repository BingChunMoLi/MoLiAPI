package com.bingchunmoli.api.config;

import com.bingchunmoli.api.weather.bean.WeatherVO;
import com.jthinking.common.util.ip.IPv4Info;
import com.jthinking.common.util.ip.IPv6Info;
import db.migration.V3__synchronize_application_schema;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.predicate.RuntimeHintsPredicates;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class NativeRuntimeHintsTest {
    private final RuntimeHints hints = new RuntimeHints();

    @BeforeEach
    void setUp() {
        new NativeRuntimeHints().registerHints(hints, getClass().getClassLoader());
    }

    @ParameterizedTest
    @ValueSource(strings = {"mapper/NavigationMapper.xml", "db/migration/V0___init.sql",
            "db/migration-postgresql/V0__init.sql", "db/migration-sqlite/V0__init.sql",
            "db/migration/V3__synchronize_application_schema.class", "init/yiyan/a.json", "img/404.jpg",
            "config/logback-spring.xml", "emojis.json", "ip.zip", "ipv6.zip"})
    void includesDynamicallyLoadedResources(final String resource) {
        assertThat(RuntimeHintsPredicates.resource().forResource(resource)).accepts(hints);
    }

    @Test
    void excludesPrivateCredentialsFromCustomResourceHints() {
        assertThat(RuntimeHintsPredicates.resource().forResource("google-service.json")).rejects(hints);
    }

    @Test
    void supportsReflectiveConfigurationAccess() {
        assertThat(RuntimeHintsPredicates.reflection().onMethodInvocation(ApiConfig.class, "getWeatherKey"))
                .accepts(hints);
        assertThat(RuntimeHintsPredicates.reflection().onMethodInvocation(ApiConfig.class, "setWeatherKey"))
                .accepts(hints);
    }

    @Test
    void supportsJavaMigrationDiscovery() {
        assertThat(RuntimeHintsPredicates.reflection()
                .onConstructorInvocation(assertDoesNotThrow(
                        () -> V3__synchronize_application_schema.class.getDeclaredConstructor()))).accepts(hints);
    }

    @Test
    void registersNestedIntegrationModelsAndConcreteIpResponses() {
        assertThat(hints.reflection().getTypeHint(WeatherVO.LocationDTO.class)).isNotNull();
        assertThat(hints.reflection().getTypeHint(IPv4Info.class)).isNotNull();
        assertThat(hints.reflection().getTypeHint(IPv6Info.class)).isNotNull();
    }
}
