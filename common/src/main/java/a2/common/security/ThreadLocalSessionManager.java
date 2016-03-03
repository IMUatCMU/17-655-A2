package a2.common.security;

/**
 * Implementation of {@link SessionManager} that stores {@link SessionContext} on the java thread local. Caution that
 * the {@link SessionContext} must be cleared by calling {@link #remove()} before losing access to the current thread;
 * otherwise, it's highly likely to cause a memory leak.
 *
 * @since 1.0.0
 */
public class ThreadLocalSessionManager implements SessionManager {

    private static final ThreadLocal<SessionContext> threadLocal = new InheritableThreadLocal<SessionContext>();

    public void put(SessionContext sessionContext) {
        threadLocal.set(sessionContext);
    }

    public SessionContext get() {
        return threadLocal.get();
    }

    public void remove() {
        threadLocal.remove();
    }
}
