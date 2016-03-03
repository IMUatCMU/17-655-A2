package a2.common.security;

/**
 * Common interface for session management. Implementations can introduce storage for the {@link SessionContext} object.
 *
 * @since 1.0.0
 */
public interface SessionManager {

    void put(SessionContext sessionContext);

    SessionContext get();

    void remove();
}
