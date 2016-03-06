package a2.migration;

import a2.common.dao.BasicDao;
import a2.common.exception.DatabaseConnectionException;
import a2.common.ioc.AppBean;

import java.sql.ResultSet;
import java.sql.Statement;

/**
 * @author Weinan Qiu
 * @since 1.0.0
 */
public class MigrationDao extends BasicDao implements AppBean {

    @Override
    protected String databaseName() {
        return "";
    }

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

    public void createMigrationTable() {
        try {
            getDatabaseConnection().createStatement().executeUpdate("CREATE TABLE `eep_leaftech`.`MIGRATION` (`done` tinyint(1) DEFAULT 0) ENGINE=InnoDB DEFAULT CHARSET=utf8");
            getDatabaseConnection().createStatement().executeUpdate("INSERT INTO `eep_leaftech`.`MIGRATION` VALUES (false)");
        } catch (Exception ex) {
            throw new DatabaseConnectionException(ex);
        }
    }

    public void markMigrationAsDone() {
        try {
            getDatabaseConnection().createStatement().executeUpdate("UPDATE `eep_leaftech`.`MIGRATION` SET `done` = true");
        } catch (Exception ex) {
            throw new DatabaseConnectionException(ex);
        }
    }

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
