package com.bingchunmoli.api.init.impl;

import com.bingchunmoli.api.init.InitService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

@Slf4j
@Service
@Deprecated
@RequiredArgsConstructor
public class InitSqlServiceImpl implements InitService {
    private static final String TABLE_TYPE = "TABLE";
    private static final String FLYWAY_SCHEMA_HISTORY = "flyway_schema_history";

    private final DataSource dataSource;

    @Value("${moli.init.sqlPath:classpath:/init/db/init.sql}")
    private Resource sqlResource;

    @Override
    public void init() {
        if (sqlResource == null || !sqlResource.exists() || hasApplicationTables()) {
            return;
        }

        new ResourceDatabasePopulator(sqlResource).execute(dataSource);
    }

    @Override
    public Integer getOrder() {
        return 5;
    }

    private boolean hasApplicationTables() {
        try (Connection connection = dataSource.getConnection();
             ResultSet tables = connection.getMetaData().getTables(
                     connection.getCatalog(), null, "%", new String[]{TABLE_TYPE})) {
            while (tables.next()) {
                final String tableName = tables.getString("TABLE_NAME");
                if (!FLYWAY_SCHEMA_HISTORY.equalsIgnoreCase(tableName)) {
                    return true;
                }
            }
            return false;
        } catch (SQLException exception) {
            throw new DataAccessResourceFailureException("Failed to inspect database tables", exception);
        }
    }
}
