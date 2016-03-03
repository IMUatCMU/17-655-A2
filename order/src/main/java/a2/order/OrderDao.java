package a2.order;

import a2.common.dao.BasicDao;
import a2.common.exception.DatabaseConnectionException;
import a2.common.ioc.AppBean;

import java.math.BigDecimal;
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
        return "orderinfo";
    }

    public void createOrderDetailsTable(String orderDetailsTableName) {
        try {
            Statement statement = getDatabaseConnection().createStatement();
            String query = String.format("CREATE TABLE `%s`(" +
                    "item_id int unsigned not null auto_increment primary key, " +
                    "product_id varchar(20), " +
                    "description varchar(80), " +
                    "item_price float(7,2)" +
                    ");", orderDetailsTableName);
            statement.executeUpdate(query);
        } catch (Exception ex) {
            throw new DatabaseConnectionException(ex);
        }
    }

    public void insertOrderDetails(String tableName, String productCode, String description, BigDecimal itemPrice) {
        try {
            Statement statement = getDatabaseConnection().createStatement();
            String query = String.format("INSERT INTO `%s`(`product_id`, `description`, `item_price`) VALUES ('%s', '%s', %s)",
                    tableName,
                    productCode,
                    description,
                    itemPrice);
            statement.executeUpdate(query);
        } catch (Exception ex) {
            throw new DatabaseConnectionException(ex);
        }
    }

    public void insertOrderInfo(Date orderDate,
                                 String firstName,
                                 String lastName,
                                 String phone,
                                 String address,
                                String message,
                                 BigDecimal totalCost,
                                 Boolean shipped,
                                 String orderTable) {
        try {
            Statement statement = getDatabaseConnection().createStatement();
            String query = String.format("INSERT INTO `orders`(`order_date`, `first_name`, `last_name`, `phone`, `address`, `message`, `total_cost`, `shipped`, `ordertable`) VALUES ('%s', '%s', '%s', '%s', '%s', '%s', %s, %s, '%s')",
                    new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(orderDate),
                    firstName,
                    lastName,
                    phone,
                    address,
                    message,
                    totalCost.toString(),
                    shipped.toString(),
                    orderTable);
            statement.executeUpdate(query);
        } catch (Exception ex) {
            throw new DatabaseConnectionException(ex);
        }
    }
}
