package a2.common.security;

/**
 * Interface for password encoders that are able to encode a password and compare encoded and no-encoded passwords.
 *
 * @since 1.0.0
 */
public interface PasswordEncoder {

    String encoder(String rawPassword);

    boolean matches(String rawPassword, String encoded);
}
