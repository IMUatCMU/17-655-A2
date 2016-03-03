package a2.common.security;

import a2.common.exception.PasswordMatchException;
import a2.common.ioc.AppBean;
import org.mindrot.BCrypt;

/**
 * Implementation of {@link PasswordEncoder} that uses bcrypt algorithm
 *
 * @since 1.0.0
 */
public class BCryptPasswordEncoder implements PasswordEncoder, AppBean {

    @Override
    public String encoder(String rawPassword) {
        return BCrypt.hashpw(rawPassword, BCrypt.gensalt());
    }

    @Override
    public boolean matches(String rawPassword, String encoded) {
        try {
            return BCrypt.checkpw(rawPassword, encoded);
        } catch (Exception ex) {
            throw new PasswordMatchException(ex);
        }
    }
}
