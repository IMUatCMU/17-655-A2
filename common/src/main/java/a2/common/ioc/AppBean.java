package a2.common.ioc;

/**
 * Common interfaces for objects to register itself as a bean
 *
 * @since 1.0.0
 */
public interface AppBean {

    default String beanName() {
        return getClass().getSimpleName();
    }

    default void afterInitialization() {
    }
}
