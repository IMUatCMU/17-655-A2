package a2.common.security;

import java.util.ArrayList;
import java.util.List;

/**
 * This represents an authenticated user for the current session. It stores security
 * related information for the user.
 *
 * @since 1.0.0
 */
public class Authentication {

    /**
     * id of the user
     */
    private String userId;

    /**
     * User name for the user.
     */
    private final String userName;

    /**
     * Hashed password (Bcrypt) for the user. Don't ever store plain text passwords.
     */
    private final String hashedPassword;

    /**
     * Whether the principal has passed authentication
     */
    private boolean authenticated = false;

    /**
     * User permissions.
     */
    private List<Permission> permissions = new ArrayList<Permission>();

    public Authentication(String userName, String hashedPassword) {
        this.userName = userName;
        this.hashedPassword = hashedPassword;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public String getHashedPassword() {
        return hashedPassword;
    }

    public List<Permission> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<Permission> permissions) {
        this.permissions = permissions;
    }

    public boolean isAuthenticated() {
        return authenticated;
    }

    public void setAuthenticated(boolean authenticated) {
        this.authenticated = authenticated;
    }
}
