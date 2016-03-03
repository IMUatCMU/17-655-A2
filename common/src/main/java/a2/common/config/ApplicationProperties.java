package a2.common.config;

import a2.common.ioc.AppBean;

import java.util.Properties;

/**
 * Wrapper for {@link Properties}
 *
 * @since 1.0.0
 */
public class ApplicationProperties implements AppBean {

    private final Properties properties;

    public ApplicationProperties(Properties properties) {
        this.properties = properties;
    }

    public Properties getProperties() {
        return properties;
    }
}
