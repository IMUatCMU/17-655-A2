package a2.inventory;

import a2.common.dao.BasicDao;
import a2.common.exception.DatabaseConnectionException;
import a2.common.ioc.AppBean;
import a2.common.model.Product;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Weinan Qiu
 * @since 1.0.0
 */
public class InventoryDao extends BasicDao implements AppBean {

    @Override
    protected String databaseName() {
        return "eep_leaftech";
    }

    public long countForInventory(Integer id) {
        try {
            Statement statement = getDatabaseConnection().createStatement();
            String query = String.format("SELECT SUM(`quantity`) FROM `products` WHERE `id` = %s", id);
            ResultSet resultSet = statement.executeQuery(query);

            while (resultSet.next()) {
                return resultSet.getLong(1);
            }
        } catch (Exception ex) {
            throw new DatabaseConnectionException(ex);
        }

        return 0l;
    }

    public long countForInventory(String code, String type) {
        try {
            Statement statement = getDatabaseConnection().createStatement();
            String query = String.format("SELECT SUM(`quantity`) FROM `products` WHERE `code` = '%s' AND `type` = '%s'", code, type);
            ResultSet resultSet = statement.executeQuery(query);

            while (resultSet.next()) {
                return resultSet.getLong(1);
            }
        } catch (Exception ex) {
            throw new DatabaseConnectionException(ex);
        }

        return 0l;
    }

    public void insertInventory(String code, String description, String quantity, String price, Product product) {
        try {
            Statement statement = getDatabaseConnection().createStatement();
            String query = String.format("INSERT INTO `products`(`code`, `description`, `quantity`, `price`, `type`) VALUES ('%s', '%s', %s, %s, '%s')",
                    code, description, quantity, price, product.getDatabaseValue());
            statement.executeUpdate(query);
        } catch (Exception ex) {
            throw new DatabaseConnectionException(ex);
        }
    }

    public void deleteInventory(int id) {
        try {
            Statement statement = getDatabaseConnection().createStatement();
            String query = String.format("DELETE FROM `products` WHERE `id` = %s", id);
            statement.executeUpdate(query);
        } catch (Exception ex) {
            throw new DatabaseConnectionException(ex);
        }
    }

    public void decrementInventory(int id) {
        try {
            Statement statement = getDatabaseConnection().createStatement();
            String query = String.format("UPDATE `products` SET `quantity` = `quantity` - 1 WHERE `id` = %s", id);
            statement.executeUpdate(query);
        } catch (Exception ex) {
            throw new DatabaseConnectionException(ex);
        }
    }

    public List<Inventory> getInventoryFor(Product product) {
        List<Inventory> results = new ArrayList<>();

        try {
            Statement statement = getDatabaseConnection().createStatement();
            String query = String.format("SELECT * FROM `products` WHERE `type` = '%s'", product.getDatabaseValue());
            ResultSet resultSet = statement.executeQuery(query);

            while (resultSet.next()) {
                Inventory inventory = new Inventory();
                inventory.setId(resultSet.getInt("id"));
                inventory.setCode(resultSet.getString("code"));
                inventory.setDescription(resultSet.getString("description"));
                inventory.setQuantity(resultSet.getLong("quantity"));
                inventory.setPrice(resultSet.getBigDecimal("price"));
                inventory.setProduct(Product.fromDatabaseValue(resultSet.getString("type")));
                results.add(inventory);
            }

            return results;
        } catch (Exception ex) {
            throw new DatabaseConnectionException(ex);
        }
    }
}
