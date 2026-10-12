package com.bingchunmoli.api.database;

import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.flyway.autoconfigure.FlywayAutoConfiguration;
import org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

import static org.assertj.core.api.Assertions.assertThat;

class FlywayAutoConfigurationTest {

    @Test
    void automaticallyMigratesAnEmptyDatabaseBeforeServingQueries() {
        new ApplicationContextRunner()
                .withConfiguration(AutoConfigurations.of(
                        DataSourceAutoConfiguration.class, FlywayAutoConfiguration.class))
                .withPropertyValues(
                        "spring.datasource.url=jdbc:h2:mem:flyway-auto-test;MODE=MySQL;DATABASE_TO_LOWER=TRUE",
                        "spring.datasource.driver-class-name=org.h2.Driver",
                        "spring.flyway.locations=classpath:db/migration")
                .run(context -> {
                    assertThat(context).hasSingleBean(Flyway.class);
                    assertThat(context.getBean(Flyway.class).info().applied()).hasSize(4);
                    final JdbcTemplate jdbcTemplate = new JdbcTemplate(context.getBean(DataSource.class));
                    assertThat(jdbcTemplate.queryForObject("SELECT COUNT(*) FROM shi_ci", Integer.class))
                            .isPositive();
                    assertThat(jdbcTemplate.queryForList("SELECT method, url FROM account")).isEmpty();
                });
    }
}
