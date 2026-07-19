package db.migration;

import org.flywaydb.core.api.FlywayException;
import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public final class V3__synchronize_application_schema extends BaseJavaMigration {
    private static final String ACCOUNT_TABLE = "account";
    private static final String MUSIC_SONG_TABLE = "netease_music_song";

    @Override
    public void migrate(final Context context) {
        final Connection connection = context.getConnection();
        addColumnIfMissing(connection, ACCOUNT_TABLE, "method", "varchar(10) NULL");
        addColumnIfMissing(connection, ACCOUNT_TABLE, "url", "varchar(1000) NULL");
        addColumnIfMissing(connection, MUSIC_SONG_TABLE, "playlist_id", "int NULL");
    }

    private void addColumnIfMissing(final Connection connection, final String tableName, final String columnName,
                                    final String columnDefinition) {
        if (hasColumn(connection, tableName, columnName)) {
            return;
        }

        final String sql = "ALTER TABLE " + tableName + " ADD COLUMN " + columnName + " " + columnDefinition;
        try (Statement statement = connection.createStatement()) {
            statement.execute(sql);
        } catch (SQLException exception) {
            throw new FlywayException("Failed to add column " + tableName + "." + columnName, exception);
        }
    }

    private boolean hasColumn(final Connection connection, final String tableName, final String columnName) {
        try (ResultSet columns = connection.getMetaData().getColumns(connection.getCatalog(), null, "%", "%")) {
            while (columns.next()) {
                final String currentTableName = columns.getString("TABLE_NAME");
                final String currentColumnName = columns.getString("COLUMN_NAME");
                final boolean matchingTable = tableName.equalsIgnoreCase(currentTableName);
                final boolean matchingColumn = columnName.equalsIgnoreCase(currentColumnName);
                if (matchingTable && matchingColumn) {
                    return true;
                }
            }
            return false;
        } catch (SQLException exception) {
            throw new FlywayException("Failed to inspect column " + tableName + "." + columnName, exception);
        }
    }
}
