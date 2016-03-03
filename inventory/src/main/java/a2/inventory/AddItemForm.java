package a2.inventory;

/**
 * @author Weinan Qiu
 * @since 1.0.0
 */
public class AddItemForm {

    public static final String PRODUCT_ID = "PRODUCT_ID";
    public static final String PRICE = "PRICE";
    public static final String QUANTITY = "QUANTITY";
    public static final String DESCRIPTION = "DESCRIPTION";
    public static final String ITEM = "ITEM";

    private String code;
    private String price;
    private String quantity;
    private String description;
    private String itemName;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }
}
