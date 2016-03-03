package a2.inventory;

import a2.common.dao.BasicDao;
import a2.common.exception.DatabaseConnectionException;
import a2.common.ioc.AppBean;

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

    public static final String TREE = "trees";
    public static final String SHRUB = "shrubs";
    public static final String SEED = "seeds";
    public static final String REF_MATERIAL = "referencematerials";
    public static final String PROCESSING = "processing";
    public static final String GENOMICS = "genomics";
    public static final String CULTUREBOXES = "cultureboxes";

    @Override
    protected String databaseName() {
        return "inventory";
    }

    public long countForInventory(String itemTableName, String code) {
        try {
            Statement statement = getDatabaseConnection().createStatement();
            String query = String.format("SELECT COUNT(*) FROM `%s` WHERE `product_code` = '%s'", itemTableName, code);
            ResultSet resultSet = statement.executeQuery(query);

            while (resultSet.next()) {
                return resultSet.getLong(1);
            }
        } catch (Exception ex) {
            throw new DatabaseConnectionException(ex);
        }

        return 0l;
    }

    public void insertInventory(String code, String description, String quantity, String price, String itemTableName) {
        try {
            Statement statement = getDatabaseConnection().createStatement();
            String query = String.format("INSERT INTO `%s`(`product_code`, `description`, `quantity`, `price`) VALUES ('%s', '%s', %s, %s)",
                    itemTableName, code, description, quantity, price);
            statement.executeUpdate(query);
        } catch (Exception ex) {
            throw new DatabaseConnectionException(ex);
        }
    }

    public void deleteInventory(String code, String itemTableName) {
        try {
            Statement statement = getDatabaseConnection().createStatement();
            String query = String.format("DELETE FROM `%s` WHERE `product_code` = '%s'", itemTableName, code);
            statement.executeUpdate(query);
        } catch (Exception ex) {
            throw new DatabaseConnectionException(ex);
        }
    }

    public void decrementInventory(String code, String itemTableName) {
        try {
            Statement statement = getDatabaseConnection().createStatement();
            String query = String.format("UPDATE `%s` SET `quantity` = `quantity` - 1 WHERE `product_code` = '%s'", itemTableName, code);
            statement.executeUpdate(query);
        } catch (Exception ex) {
            throw new DatabaseConnectionException(ex);
        }
    }

    public List<Inventory> getInventoryFor(String itemTableName) {
        List<Inventory> results = new ArrayList<>();

        try {
            Statement statement = getDatabaseConnection().createStatement();
            String query = String.format("SELECT * FROM `%s`", itemTableName);
            ResultSet resultSet = statement.executeQuery(query);

            while (resultSet.next()) {
                Inventory inventory = new Inventory();
                inventory.setCode(resultSet.getString("product_code"));
                inventory.setDescription(resultSet.getString("description"));
                inventory.setQuantity(resultSet.getLong("quantity"));
                inventory.setPrice(resultSet.getBigDecimal("price"));
                results.add(inventory);
            }

            return results;
        } catch (Exception ex) {
            throw new DatabaseConnectionException(ex);
        }
    }
}
