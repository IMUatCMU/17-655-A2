package a2.migration;

import a2.common.dao.BasicDao;
import a2.common.exception.DatabaseConnectionException;
import a2.common.ioc.AppBean;

import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Data access object for database migration. It is mainly responsible for handling CRUD related to
 * the 'MIGRATION' table.
 *
 * @since 1.0.0
 */
public class MigrationDao extends BasicDao implements AppBean {

    @Override
    protected String databaseName() {
        return "";
    }

    /**
     * Check if a certain table exists
     *
     * @param dbName
     * @param tableName
     * @return
     */
    public boolean tableExists(String dbName, String tableName) {
        try {
            Statement statement = getDatabaseConnection().createStatement();
            String query = String.format("SELECT COUNT(*) FROM `information_schema`.`TABLES` WHERE `TABLE_SCHEMA` = '%s' AND `TABLE_NAME` = '%s'", dbName, tableName);
            ResultSet rs = statement.executeQuery(query);
            return rs.next() && rs.getInt(1) >= 1;
        } catch (Exception ex) {
            return false;
        }
    }

    /**
     * Create MIGRATION table
     */
    public void createMigrationTable() {
        try {
            getDatabaseConnection().createStatement().executeUpdate("CREATE TABLE `eep_leaftech`.`MIGRATION` (`done` tinyint(1) DEFAULT 0) ENGINE=InnoDB DEFAULT CHARSET=utf8");
            getDatabaseConnection().createStatement().executeUpdate("INSERT INTO `eep_leaftech`.`MIGRATION` VALUES (false)");
        } catch (Exception ex) {
            throw new DatabaseConnectionException(ex);
        }
    }

    /**
     * Mark the migration as done
     */
    public void markMigrationAsDone() {
        try {
            getDatabaseConnection().createStatement().executeUpdate("UPDATE `eep_leaftech`.`MIGRATION` SET `done` = true");
        } catch (Exception ex) {
            throw new DatabaseConnectionException(ex);
        }
    }

    /**
     * Check if migration has every been performed
     */
    public boolean isMigrationDone() {
        try {
            Statement statement = getDatabaseConnection().createStatement();
            ResultSet rs = statement.executeQuery("SELECT `done` FROM `eep_leaftech`.`MIGRATION`");
            return rs.next() && rs.getBoolean("done");
        } catch (Exception ex) {
            return true;
        }
    }
}
