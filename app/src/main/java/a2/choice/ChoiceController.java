package a2.choice;

import a2.common.ioc.AppBean;
import a2.common.security.Authentication;
import a2.common.security.Permission;
import a2.common.security.SessionContextHolder;

/**
 * Business controller for choice UI.
 *
 * @since 1.0.0
 */
public class ChoiceController implements AppBean {

    /**
     * Provides authorization service for the choice UI. Checks the current authenticated user has
     * enough permission to access the app with provided permission.
     *
     * @param required
     * @return
     */
    public boolean hasSufficientPermission(Permission required) {
        Authentication authentication = SessionContextHolder.getAuthentication();
        if (authentication == null || !authentication.isAuthenticated())
            return false;

        return authentication.getPermissions().contains(required);
    }
}
