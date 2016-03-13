package a2.migration;

import a2.common.dao.BasicDao;
import a2.common.exception.DatabaseConnectionException;
import a2.common.model.Product;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Utility class to perform some database migration jobs. Since this will only run once throughout
 * the entire life of the application, it isn't registered as a bean. The static method {@link #performMigration()}
 * takes care of initializing the instance and perform the migration.
 *
 * @since 1.0.0
 */
public class DBMigration {

    /**
     * Key names for the {@link Map} used to carry intermediate query results.
     */
    public static final String ID = "id";
    public static final String DESCRIPTION = "desc";
    public static final String QUANTITY = "quantity";
    public static final String PRICE = "price";
    public static final String TYPE = "type";
    public static final String DATE = "date";
    public static final String FIRST_NAME = "first_name";
    public static final String LAST_NAME = "last_name";
    public static final String ADDRESS = "address";
    public static final String PHONE = "phone";
    public static final String TOTAL_COST = "total_cost";
    public static final String SHIPPED = "shipped";
    public static final String CODE = "code";

    /**
     * Create instance and perform migration. The only entry point for this class.
     */
    public static void performMigration() {
        DBMigration migration = new DBMigration();
        migration.doMigrateInventory();
        migration.doMigrateOrders();
    }

    private DBMigration() {
    }

    /**
     * Migrate the inventory, it queries the EEP and LeafTech databases for all their inventories and format
     * them into the new schema before inserting them all.
     */
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

    /**
     * Migrate the orders, it queries the EEP database and migrate any old orders into the new format.
     */
    private void doMigrateOrders() {
        new SelectOrders().selectAll()
                .stream()
                .map(InsertOrder::new)
                .forEach(InsertOrder::insertOrders);
    }

    /**
     * Utility stateful class that handles inserting an inventory item.
     */
    private static class InsertInventory extends BasicDao {

        // properties of an inventory
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

    /**
     * Utility stateful class that handles inserting a new order
     */
    private static class InsertOrder extends BasicDao {

        private final Map<String, Object> data;

        public InsertOrder(Map<String, Object> data) {
            this.data = data;
        }

        @Override
        protected String databaseName() {
            return "eep_leaftech";
        }

        public void insertOrders() {
            try {
                String query = String.format("INSERT INTO `orders`(`date`, `first_name`, `last_name`, `phone`, `address`, `message`, `total_cost`, `shipped`) VALUES ('%s', '%s', '%s', '%s', '%s', '%s', %s, %s)",
                        new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(data.get(DATE)),
                        data.get(FIRST_NAME),
                        data.get(LAST_NAME),
                        data.get(PHONE),
                        data.get(ADDRESS),
                        "",
                        data.get(TOTAL_COST),
                        data.get(SHIPPED));
                PreparedStatement statement = getDatabaseConnection().prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
                statement.executeUpdate();

                ResultSet keys = statement.getGeneratedKeys();
                int orderId;
                if (keys.next())
                    orderId = keys.getInt(1);
                else
                    throw new Exception("Error retrieving keys after insert.");

                List<String> codes = (List) data.get(CODE);
                codes.stream()
                        .map(s -> new ProductCodeToId(s).find())
                        .forEach(integer -> {
                            String query2 = String.format("INSERT INTO `order_items`(`order_id`, `product_id`) VALUES (%s, %s)", orderId, integer);
                            try {
                                getDatabaseConnection().createStatement().executeUpdate(query2);
                            } catch (Exception ex) {
                                throw new DatabaseConnectionException(ex);
                            }
                        });

            } catch (Exception ex) {
                throw new DatabaseConnectionException(ex);
            }
        }
    }


    /**
     * Utility stateful class handling select of all inventories of a certain category
     */
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

    /**
     * Utility stateful class handling select of all orders of a certain category
     */
    private static class SelectOrders extends BasicDao {

        @Override
        protected String databaseName() {
            return "orderinfo";
        }

        public List<Map<String, Object>> selectAll() {
            List<Map<String, Object>> results = new ArrayList<>();

            try {
                ResultSet resultSet = getDatabaseConnection().createStatement().executeQuery("SELECT * FROM `orders`");

                while (resultSet.next()) {
                    Map<String, Object> row = new HashMap<>();
                    row.put(ID, resultSet.getInt("order_id"));
                    row.put(DATE, resultSet.getTimestamp("order_date"));
                    row.put(FIRST_NAME, resultSet.getString("first_name"));
                    row.put(LAST_NAME, resultSet.getString("last_name"));
                    row.put(ADDRESS, resultSet.getString("address"));
                    row.put(PHONE, resultSet.getString("phone"));
                    row.put(TOTAL_COST, resultSet.getBigDecimal("total_cost"));
                    row.put(SHIPPED, resultSet.getBoolean("shipped"));

                    String orderTable = resultSet.getString("ordertable");
                    String query = String.format("SELECT * FROM `%s`", orderTable);
                    ResultSet rs2 = getDatabaseConnection().createStatement().executeQuery(query);
                    List<String> codes = new ArrayList<>();
                    while (rs2.next()) {
                        codes.add(rs2.getString("product_id"));
                    }
                    row.put(CODE, codes);

                    results.add(row);
                }

                return results;
            } catch (Exception ex) {
                throw new RuntimeException("Database migration failed");
            }
        }
    }

    /**
     * Utility stateful class converting the code of a product to its id.
     */
    private static class ProductCodeToId extends BasicDao {

        private final String code;

        public ProductCodeToId(String code) {
            this.code = code;
        }

        @Override
        protected String databaseName() {
            return "eep_leaftech";
        }

        public int find() {
            try {
                String query = String.format("SELECT `id` FROM `products` WHERE `code` = '%s'", code);
                ResultSet rs = getDatabaseConnection().createStatement().executeQuery(query);

                if (rs.next())
                    return rs.getInt("id");

                throw new RuntimeException("Product code to id failed.");
            } catch (Exception ex) {
                throw new RuntimeException("Database migration failed");
            }
        }
    }
}
