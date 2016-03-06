package a2.common.security;

import org.apache.commons.lang3.StringUtils;

/**
 * MySQL connection parameters for the current session.
 *
 * @since 1.0.0
 */
public class MySqlConnection {

    public static final String DEFAULT_HOST_ADDRESS = "localhost";
    public static final String DEFAULT_PORT = "3306";
    public static final String DEFAULT_USERNAME = "remote";
    public static final String DEFAULT_PASSWORD = "remote_pass";
    public static final String DEFAULT_PARAMETERS = "";

    private final String hostAddress;
    private final String port;
    private final String userName;
    private final String password;
    private final String parameters;

    public MySqlConnection(String hostAddress, String port, String userName, String password, String parameters) {
        this.hostAddress = hostAddress == null ? DEFAULT_HOST_ADDRESS : hostAddress;
        this.port = port == null ? DEFAULT_PORT : port;
        this.userName = userName == null ? DEFAULT_USERNAME : userName;
        this.password = password == null ? DEFAULT_PASSWORD : password;
        this.parameters = parameters == null ? DEFAULT_PARAMETERS : parameters;
    }

    public String getConnectionUrl() {
        return String.format("jdbc:mysql://%s:%s%s",
                hostAddress,
                port,
                parameters.length() == 0 ? "" : "?" + parameters);
    }

    public String getConnectionUrl(String database) {
        if (StringUtils.isEmpty(database))
            return getConnectionUrl();

        return String.format("jdbc:mysql://%s:%s/%s",
                hostAddress,
                port,
                database + (parameters.length() == 0 ? "" : "?" + parameters));
    }

    public String getHostAddress() {
        return hostAddress;
    }

    public String getPort() {
        return port;
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    public String getParameters() {
        return parameters;
    }
}
