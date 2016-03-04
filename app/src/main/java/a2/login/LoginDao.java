package a2.login;

import a2.common.dao.BasicDao;
import a2.common.exception.DatabaseConnectionException;
import a2.common.ioc.AppBean;
import a2.common.security.Authentication;
import a2.common.security.Permission;

import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Weinan Qiu
 * @since 1.0.0
 */
public class LoginDao extends BasicDao implements AppBean {

    @Override
    protected String databaseName() {
        return "eep_leaftech";
    }

    public Authentication getAuthentication(String userName) {
        try {
            Statement statement = getDatabaseConnection().createStatement();
            String query = String.format("SELECT * FROM `users` WHERE `username` = '%s'", userName);
            ResultSet resultSet = statement.executeQuery(query);

            Authentication authentication = null;
            while (resultSet.next()) {
                authentication = new Authentication(
                        resultSet.getString("username"),
                        resultSet.getString("password"));
                authentication.setUserId(resultSet.getString("userid"));

                if (resultSet.getBoolean("inventoryflag"))
                    authentication.getPermissions().add(Permission.INVENTORY);
                if (resultSet.getBoolean("orderflag"))
                    authentication.getPermissions().add(Permission.ORDER);
                if (resultSet.getBoolean("shippingflag"))
                    authentication.getPermissions().add(Permission.SHIPPING);

                authentication.setAuthenticated(true);
                break;
            }

            return authentication;
        } catch (Exception ex) {
            throw new DatabaseConnectionException(ex);
        }
    }

    public void insertLoginSecurityAudit(String userId, Date auditTime) {
        try {
            Statement statement = getDatabaseConnection().createStatement();
            String query = String.format("INSERT INTO `security_audit`(`userid`, `time`, `action`) VALUES ('%s', CURRENT_TIMESTAMP, 'LOGIN')",
                    userId);
            statement.executeUpdate(query);
        } catch (Exception ex) {
            throw new DatabaseConnectionException(ex);
        }
    }

    public void insertLogoutSecurityAudit(String userId, Date auditTime) {
        try {
            Statement statement = getDatabaseConnection().createStatement();
            String query = String.format("INSERT INTO `security_audit`(`userid`, `time`, `action`) VALUES ('%s', CURRENT_TIMESTAMP, 'LOGOUT')",
                    userId);
            statement.executeUpdate(query);
        } catch (Exception ex) {
            throw new DatabaseConnectionException(ex);
        }
    }
}
