package a2.common.ioc;

import java.util.Collection;

/**
 * Interface for bean registration.
 *
 * @since 1.0.0
 */
public interface BeanRegistry {

    AppBean getBean(String beanName);

    Collection<AppBean> getAllBeans();

    default void registerBean(AppBean bean) {
        registerBean(bean.beanName(), bean);
    }

    void registerBean(String name, AppBean bean);
}
