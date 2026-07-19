package com.bingchunmoli.api.database;

import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.output.MigrateResult;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DatabaseMigrationTest {
    private static final String TABLE_COUNT_SQL = "SELECT COUNT(*) FROM %s";

    @TempDir
    private Path tempDirectory;

    @Test
    void migratesH2Database() {
        final String url = "jdbc:h2:mem:migration-test;MODE=MySQL;DATABASE_TO_LOWER=TRUE;DB_CLOSE_DELAY=-1";
        assertMigration(url, "org.h2.Driver", "classpath:db/migration", 4);
    }

    @Test
    void migratesSQLiteDatabase() {
        final String databasePath = tempDirectory.resolve("migration-test.db").toString();
        final String url = "jdbc:sqlite:" + databasePath;
        assertMigration(url, "org.sqlite.JDBC", "classpath:db/migration-sqlite", 1);
    }

    private void assertMigration(final String url, final String driverClassName, final String location,
                                 final int expectedMigrationCount) {
        final DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(driverClassName);
        dataSource.setUrl(url);

        final Flyway flyway = Flyway.configure()
                .dataSource(dataSource)
                .locations(location)
                .load();
        final MigrateResult migrateResult = flyway.migrate();
        final JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);

        assertEquals(expectedMigrationCount, migrateResult.migrationsExecuted);
        assertEquals(0, jdbcTemplate.queryForObject(TABLE_COUNT_SQL.formatted("device"), Integer.class));
        assertEquals(0, jdbcTemplate.queryForObject(TABLE_COUNT_SQL.formatted("account"), Integer.class));
        assertEquals(0, jdbcTemplate.queryForList("SELECT method, url FROM account").size());
        assertEquals(0, jdbcTemplate.queryForList("SELECT playlist_id FROM netease_music_song").size());
    }
}
