package a2.common.exception;

import a2.common.security.Authentication;

/**
 * @author Weinan Qiu
 * @since 1.0.0
 */
public class AuthenticationFailedException extends RuntimeException {

    private final Authentication authentication;

    public AuthenticationFailedException(Authentication authentication) {
        this.authentication = authentication;
    }

    public Authentication getAuthentication() {
        return authentication;
    }
}
