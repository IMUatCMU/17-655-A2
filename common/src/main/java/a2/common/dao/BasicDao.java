package a2.common.dao;

import a2.common.exception.DatabaseConnectionException;
import a2.common.security.MySqlConnection;
import a2.common.security.SessionContextHolder;

import java.sql.Connection;
import java.sql.DriverManager;

/**
 * Data access object that offers connection to database.
 *
 * @since 1.0.0
 */
public abstract class BasicDao {

    private Connection databaseConnection = null;

    private void connectToDatabase() {
        MySqlConnection connectionDetails = SessionContextHolder.getDatabaseConnectionDetails();
        if (connectionDetails == null)
            throw new RuntimeException("Unable to confirm connection details to database");

        try {
            Class.forName("com.mysql.jdbc.Driver");
            System.out.println(connectionDetails.getConnectionUrl(databaseName()));
            databaseConnection = DriverManager.getConnection(
                    connectionDetails.getConnectionUrl(databaseName()),
                    connectionDetails.getUserName(),
                    connectionDetails.getPassword());
        } catch (Exception ex) {
            throw new DatabaseConnectionException(ex);
        }
    }

    public Connection getDatabaseConnection() {
        if (databaseConnection == null)
            connectToDatabase();
        return databaseConnection;
    }

    protected abstract String databaseName();
}
