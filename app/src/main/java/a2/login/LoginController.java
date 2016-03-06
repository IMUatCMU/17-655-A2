package a2.login;

import a2.common.config.ApplicationProperties;
import a2.common.exception.AuthenticationFailedException;
import a2.common.exception.DatabaseConnectionException;
import a2.common.ioc.AppBean;
import a2.common.ioc.BeanHolder;
import a2.common.security.*;
import a2.migration.DBMigration;
import a2.migration.MigrationController;

import java.util.Date;
import java.util.regex.Pattern;

/**
 * Controller handling business related logic for login activities
 *
 * @since 1.0.0
 */
public class LoginController implements AppBean {

    private static final String IP_V4_PATTERN = "^(([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.){3}([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";

    private PasswordEncoder passwordEncoder;
    private ApplicationProperties applicationProperties;
    private LoginDao loginDao;
    private MigrationController migrationController;

    @Override
    public void afterInitialization() {
        passwordEncoder = (PasswordEncoder) BeanHolder.getBean(BCryptPasswordEncoder.class.getSimpleName());
        applicationProperties = (ApplicationProperties) BeanHolder.getBean(ApplicationProperties.class.getSimpleName());
        loginDao = (LoginDao) BeanHolder.getBean(LoginDao.class.getSimpleName());
        migrationController = (MigrationController) BeanHolder.getBean(MigrationController.class.getSimpleName());

        assert passwordEncoder != null;
        assert applicationProperties != null;
        assert loginDao != null;
        assert migrationController != null;
    }

    public LoginFormValidationResult validateForm(LoginForm loginForm) {
        assert loginForm != null;

        LoginFormValidationResult validationResult = new LoginFormValidationResult();
        validationResult.setForm(loginForm);

        if (loginForm.getUserName() == null || loginForm.getUserName().length() == 0)
            validationResult.getMessages().put(LoginForm.KEY_USERNAME, "Missing username");

        if (loginForm.getPassword() == null || loginForm.getPassword().length() == 0)
            validationResult.getMessages().put(LoginForm.KEY_PASSWORD, "Missing password");

        if (loginForm.getDatabaseAddress() == null || loginForm.getDatabaseAddress().length() == 0)
            validationResult.getMessages().put(LoginForm.KEY_DB_ADDR, "Missing database address");

        if (!"localhost".equals(loginForm.getDatabaseAddress()) &&
                !Pattern.compile(IP_V4_PATTERN).matcher(loginForm.getDatabaseAddress()).matches())
            validationResult.getMessages().put(LoginForm.KEY_DB_ADDR, "Invalid database address");

        if (validationResult.getMessages().size() == 0)
            validationResult.setIsValid(true);

        return validationResult;
    }

    public void authenticate(LoginForm loginForm) {
        Authentication authentication = new Authentication(loginForm.getUserName(), passwordEncoder.encoder(loginForm.getPassword()));
        MySqlConnection connection = new MySqlConnection(
                loginForm.getDatabaseAddress(),
                applicationProperties.getProperties().getProperty("mysql.port"),
                applicationProperties.getProperties().getProperty("mysql.user"),
                applicationProperties.getProperties().getProperty("mysql.pass"),
                applicationProperties.getProperties().getProperty("mysql.params"));

        SessionContext sessionContext = new SessionContext();
        sessionContext.setAuthentication(authentication);
        sessionContext.setMySqlConnection(connection);
        SessionContextHolder.sessionManager().put(sessionContext);

        boolean authenticationSuccess = false;
        Authentication record;
        try {
            record = loginDao.getAuthentication(loginForm.getUserName());
            if (record != null && passwordEncoder.matches(loginForm.getPassword(), record.getHashedPassword()))
                authenticationSuccess = true;
        } catch (Exception ex) {
            SessionContextHolder.sessionManager().remove();
            if (ex instanceof DatabaseConnectionException)
                throw ex;
            throw new AuthenticationFailedException(null);
        }

        if (!authenticationSuccess) {
            SessionContextHolder.sessionManager().remove();
            throw new AuthenticationFailedException(null);
        } else {
            SessionContext newSessionContext = new SessionContext();
            newSessionContext.setAuthentication(record);
            newSessionContext.setMySqlConnection(connection);
            SessionContextHolder.sessionManager().put(newSessionContext);

            migrationController.performMigrationIfNecessary();

            loginDao.insertLoginSecurityAudit(record.getUserId(), new Date());
        }
    }
}
