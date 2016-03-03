package a2.common.ioc;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Simple implementation of {@link BeanRegistry} that stores beans in a {@link ConcurrentHashMap}.
 *
 * @since 1.0.0
 */
public class SimpleBeanRegistry implements BeanRegistry {

    private static final Map<String, AppBean> registry = new ConcurrentHashMap<>();

    @Override
    public AppBean getBean(String beanName) {
        return registry.get(beanName);
    }

    @Override
    public Collection<AppBean> getAllBeans() {
        return registry.values();
    }

    @Override
    public void registerBean(String name, AppBean bean) {
        registry.put(name, bean);
    }
}
