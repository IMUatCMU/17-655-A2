package a2.common.security;

/**
 * Holder object for the {@link SessionManager} that implements singleton pattern. Since the
 * {@link Authentication} object has a 1-to-1 relation with the current thread, having it static scoped
 * will give us easy access.
 *
 * @since 1.0.0
 */
public class SessionContextHolder {

    private static final SessionContextHolder instance = new SessionContextHolder();
    private final SessionManager sessionManager;

    private SessionContextHolder() {
        sessionManager = new ThreadLocalSessionManager();
    }

    /**
     * Convenience method to access session manager
     *
     * @return
     */
    public static SessionManager sessionManager() {
        return instance.sessionManager;
    }

    /**
     * Convenience method to access authentication object.
     *
     * @return
     */
    public static Authentication getAuthentication() {
        if (sessionManager().get() == null)
            return null;
        return sessionManager().get().getAuthentication();
    }

    /**
     * Convenience method to access database (mysql) connection details
     *
     * @return
     */
    public static MySqlConnection getDatabaseConnectionDetails() {
        return sessionManager().get().getMySqlConnection();
    }
}
