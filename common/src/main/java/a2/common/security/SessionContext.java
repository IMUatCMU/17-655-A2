package a2.common.security;

/**
 * The object that holds authentication details and database connection details for the current session.
 *
 * @since 1.0.0
 */
public class SessionContext {

    private Authentication authentication;

    private MySqlConnection mySqlConnection;

    public Authentication getAuthentication() {
        return authentication;
    }

    public void setAuthentication(Authentication authentication) {
        this.authentication = authentication;
    }

    public MySqlConnection getMySqlConnection() {
        return mySqlConnection;
    }

    public void setMySqlConnection(MySqlConnection mySqlConnection) {
        this.mySqlConnection = mySqlConnection;
    }
}
