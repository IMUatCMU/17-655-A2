package a2.common.ioc;

import java.util.Collection;

/**
 * Holder for the {@link BeanRegistry}, it implements singleton pattern.
 *
 * @since 1.0.0
 */
public class BeanHolder {

    private static final BeanHolder instance = new BeanHolder();
    private final BeanRegistry registry;

    public BeanHolder() {
        this.registry = new SimpleBeanRegistry();
    }

    public static AppBean getBean(String name) {
        return (AppBean) instance.registry.getBean(name);
    }

    public static BeanRegistry registry() {
        return instance.registry;
    }

    public static Collection<AppBean> getAllBeans() {
        return instance.registry.getAllBeans();
    }
}
