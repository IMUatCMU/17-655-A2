package a2.migration;

import a2.common.dao.BasicDao;
import a2.common.exception.DatabaseConnectionException;
import a2.common.model.Product;
import com.sun.org.apache.bcel.internal.generic.Select;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.*;

/**
 * @author Weinan Qiu
 * @since 1.0.0
 */
public class DBMigration {

    public static final String ID = "id";
    public static final String DESCRIPTION = "desc";
    public static final String QUANTITY = "quantity";
    public static final String PRICE = "price";
    public static final String TYPE = "type";

    public static void performMigration() {
        DBMigration migration = new DBMigration();
        migration.doMigrateInventory();
        migration.doMigrateOrders();
    }

    private DBMigration() {
    }

    private void doMigrateInventory() {
        Map<Product, SelectInventory> source = new HashMap<>();
        source.put(Product.TREE, new SelectInventory("inventory", "trees", "product_code", "description", "quantity", "price"));
        source.put(Product.SHRUB, new SelectInventory("inventory", "shrubs", "product_code", "description", "quantity", "price"));
        source.put(Product.SEED, new SelectInventory("inventory", "seeds", "product_code", "description", "quantity", "price"));
        source.put(Product.CULTUREBOXES, new SelectInventory("leaftech", "cultureboxes", "productid", "productdescription", "productquantity", "productprice"));
        source.put(Product.GENOMICS, new SelectInventory("leaftech", "genomics", "productid", "productdescription", "productquantity", "productprice"));
        source.put(Product.PROCESSING, new SelectInventory("leaftech", "processing", "productid", "productdescription", "productquantity", "productprice"));
        source.put(Product.REF_MATERIAL, new SelectInventory("leaftech", "referencematerials", "productid", "productdescription", "productquantity", "productprice"));

        source.entrySet()
                .stream()
                .forEach(productSelectInventoryEntry ->
                    productSelectInventoryEntry.getValue().selectAll()
                            .stream()
                            .map(stringObjectMap -> {
                                Map<String, Object> map = new HashMap<>();
                                map.putAll(stringObjectMap);
                                map.put(TYPE, productSelectInventoryEntry.getKey().getDatabaseValue());
                                return map;
                            })
                            .map(paramMap -> new InsertInventory(
                                    (String) paramMap.get(ID),
                                    (String) paramMap.get(DESCRIPTION),
                                    (BigDecimal) paramMap.get(PRICE),
                                    (Integer) paramMap.get(QUANTITY),
                                    (String) paramMap.get(TYPE)))
                            .forEach(InsertInventory::insertInventory)
                );
    }

    private void doMigrateOrders() {

    }

    private static class InsertInventory extends BasicDao {

        private final String id;
        private final String description;
        private final BigDecimal price;
        private final Integer quantity;
        private final String type;

        public InsertInventory(String id, String description, BigDecimal price, Integer quantity, String type) {
            this.id = id;
            this.description = description;
            this.price = price;
            this.quantity = quantity;
            this.type = type;
        }

        @Override
        protected String databaseName() {
            return "eep_leaftech";
        }

        public void insertInventory() {
            try {
                String query = String.format("INSERT INTO `products`(`code`, `description`, `quantity`, `price`, `type`) VALUES ('%s', '%s', %s, %s, '%s')",
                        id, description, quantity, price, type);
                System.out.println(query);
                getDatabaseConnection()
                        .createStatement()
                        .executeUpdate(query);
            } catch (Exception ex) {
                throw new DatabaseConnectionException(ex);
            }
        }
    }

    private static class SelectInventory extends BasicDao {



        private final String databaseName;
        private final String tableName;
        private final String idColumnName;
        private final String descriptionColumnName;
        private final String quantityColumnName;
        private final String priceColumnName;

        public SelectInventory(
                final String databaseName,
                final String tableName,
                final String idColumnName,
                final String descriptionColumnName,
                final String quantityColumnName,
                final String priceColumnName) {
            this.databaseName = databaseName;
            this.tableName = tableName;
            this.idColumnName = idColumnName;
            this.descriptionColumnName = descriptionColumnName;
            this.quantityColumnName = quantityColumnName;
            this.priceColumnName = priceColumnName;
        }

        @Override
        protected String databaseName() {
            return this.databaseName;
        }

        public List<Map<String, Object>> selectAll() {
            List<Map<String, Object>> results = new ArrayList<>();

            try {
                Statement statement = getDatabaseConnection().createStatement();
                String query = String.format("SELECT `%s`, `%s`, `%s`, `%s` FROM `%s`",
                        this.idColumnName,
                        this.descriptionColumnName,
                        this.quantityColumnName,
                        this.priceColumnName,
                        this.tableName);
                ResultSet resultSet = statement.executeQuery(query);

                while (resultSet.next()) {
                    Map<String, Object> row = new HashMap<>();
                    row.put(ID, resultSet.getString(idColumnName));
                    row.put(DESCRIPTION, resultSet.getString(descriptionColumnName));
                    row.put(PRICE, resultSet.getBigDecimal(priceColumnName));
                    row.put(QUANTITY, resultSet.getInt(quantityColumnName));
                    results.add(row);
                }

                return results;
            } catch (Exception ex) {
                throw new RuntimeException("Database migration failed");
            }
        }
    }
}
