package a2.order;

import a2.common.dao.BasicDao;
import a2.common.exception.DatabaseConnectionException;
import a2.common.ioc.AppBean;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Weinan Qiu
 * @since 1.0.0
 */
public class OrderDao extends BasicDao implements AppBean {

    @Override
    protected String databaseName() {
        return "eep_leaftech";
    }

    public void insertOrderDetails(int orderId, int productId) {
        try {
            Statement statement = getDatabaseConnection().createStatement();
            String query = String.format("INSERT INTO `order_items`(`order_id`, `product_id`) VALUES (%s, %s)",
                    orderId,
                    productId);
            statement.executeUpdate(query);
        } catch (Exception ex) {
            throw new DatabaseConnectionException(ex);
        }
    }

    public int insertOrderInfo(Date orderDate,
                                String firstName,
                                String lastName,
                                String phone,
                                String address,
                                String message,
                                BigDecimal totalCost,
                                Boolean shipped) {
        try {

            String query = String.format("INSERT INTO `orders`(`date`, `first_name`, `last_name`, `phone`, `address`, `message`, `total_cost`, `shipped`) VALUES ('%s', '%s', '%s', '%s', '%s', '%s', %s, %s)",
                    new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(orderDate),
                    firstName,
                    lastName,
                    phone,
                    address,
                    message,
                    totalCost,
                    shipped);
            PreparedStatement statement = getDatabaseConnection().prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            statement.executeUpdate();

            ResultSet keys = statement.getGeneratedKeys();
            if (keys.next())
                return keys.getInt(1);
            else
                throw new Exception("Error retrieving keys after insert.");
        } catch (Exception ex) {
            throw new DatabaseConnectionException(ex);
        }
    }
}
