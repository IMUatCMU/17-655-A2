package a2.login;

/**
 * DTO for login data
 *
 * @since 1.0.0
 */
public class LoginForm {

    public static final String KEY_USERNAME = "userName";
    public static final String KEY_PASSWORD = "password";
    public static final String KEY_DB_ADDR = "databaseAddress";

    private String userName;
    private String password;
    private String databaseAddress;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDatabaseAddress() {
        return databaseAddress;
    }

    public void setDatabaseAddress(String databaseAddress) {
        this.databaseAddress = databaseAddress;
    }
}
