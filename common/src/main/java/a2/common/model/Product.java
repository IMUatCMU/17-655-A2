package a2.common.model;

import java.util.Arrays;
import java.util.Optional;

/**
 * Product type enumeration
 *
 * @since 1.0.0
 */
public enum Product {

    TREE("tree"),
    SHRUB("shrub"),
    SEED("seed"),
    REF_MATERIAL("ref_mat"),
    PROCESSING("process"),
    GENOMICS("genomics"),
    CULTUREBOXES("cul_box");

    private final String databaseValue;

    Product(String databaseValue) {
        this.databaseValue = databaseValue;
    }

    public String getDatabaseValue() {
        return databaseValue;
    }

    public static Product fromDatabaseValue(String databaseValue) {
        Optional<Product> p = Arrays.asList(Product.values())
                .stream()
                .filter(product -> product.databaseValue.equals(databaseValue))
                .findFirst();
        if (!p.isPresent())
            throw new IllegalArgumentException(databaseValue + " not found");
        return p.get();
    }
}
