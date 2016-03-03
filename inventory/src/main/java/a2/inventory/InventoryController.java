package a2.inventory;

import a2.common.exception.DuplicateItemException;
import a2.common.exception.NegativeStockException;
import a2.common.ioc.AppBean;
import a2.common.ioc.BeanHolder;
import org.apache.commons.lang3.math.NumberUtils;
import org.w3c.dom.stylesheets.LinkStyle;

import java.util.List;

/**
 * @author Weinan Qiu
 * @since 1.0.0
 */
public class InventoryController implements AppBean {

    public static final String TREE = "TREE";
    public static final String SHRUB = "SHRUB";
    public static final String SEED = "SEED";
    public static final String REF_MATERIAL = "REF_MATERIAL";
    public static final String PROCESSING = "PROCESSING";
    public static final String GENOMICS = "GENOMICS";
    public static final String CULTUREBOXES = "CULTUREBOXES";

    private InventoryDao inventoryDao;

    @Override
    public void afterInitialization() {
        this.inventoryDao = (InventoryDao) BeanHolder.getBean(InventoryDao.class.getSimpleName());

        assert inventoryDao != null;
    }

    public AddItemValidationResult validateAddItem(AddItemForm addItemForm) {
        AddItemValidationResult result = new AddItemValidationResult();
        result.setForm(addItemForm);

        if (addItemForm.getCode() == null || addItemForm.getCode().length() == 0)
            result.getMessages().put(AddItemForm.PRODUCT_ID, "product id missing");
        if (addItemForm.getPrice() == null || addItemForm.getPrice().length() == 0)
            result.getMessages().put(AddItemForm.PRICE, "product price missing");
        if (addItemForm.getQuantity() == null || addItemForm.getQuantity().length() == 0)
            result.getMessages().put(AddItemForm.QUANTITY, "product quantity missing");
        if (addItemForm.getDescription() == null || addItemForm.getDescription().length() == 0)
            result.getMessages().put(AddItemForm.DESCRIPTION, "product description missing");
        if (!NumberUtils.isNumber(addItemForm.getPrice()))
            result.getMessages().put(AddItemForm.PRICE, "invalid product price");
        if (!NumberUtils.isNumber(addItemForm.getQuantity()))
            result.getMessages().put(AddItemForm.QUANTITY, "invalid product quantity");
        if (addItemForm.getItemName() == null)
            result.getMessages().put(AddItemForm.ITEM, "please select an inventory category");

        if (result.getMessages().size() == 0)
            result.setIsValid(true);

        return result;
    }

    public void addItem(AddItemForm addItemForm) {
        long count = 0;
        String tableName = identifyTableName(addItemForm.getItemName());
        count = inventoryDao.countForInventory(tableName, addItemForm.getCode());

        if (count > 0)
            throw new DuplicateItemException();

        inventoryDao.insertInventory(
                addItemForm.getCode(),
                addItemForm.getDescription(),
                addItemForm.getQuantity(),
                addItemForm.getPrice(),
                tableName);
    }

    public void deleteItem(DeleteItemForm form) {
        String tableName = identifyTableName(form.getItem());
        inventoryDao.deleteInventory(form.getCode(), tableName);
    }

    public void decrementInventory(DecrementInventoryForm form) {
        String tableName = identifyTableName(form.getItem());
        long currentStock = inventoryDao.countForInventory(tableName, form.getCode());
        if (currentStock <= 0)
            throw new NegativeStockException();

        inventoryDao.decrementInventory(form.getCode(), tableName);
    }

    public List<Inventory> getInventoryForTrees() {
        return inventoryDao.getInventoryFor(InventoryDao.TREE);
    }

    public List<Inventory> getInventoryForShrubs() {
        return inventoryDao.getInventoryFor(InventoryDao.SHRUB);
    }

    public List<Inventory> getInventoryForSeeds() {
        return inventoryDao.getInventoryFor(InventoryDao.SEED);
    }

    public List<Inventory> getInventoryForReferenceMaterials() {
        return inventoryDao.getInventoryFor(InventoryDao.REF_MATERIAL);
    }

    public List<Inventory> getInventoryForProcessing() {
        return inventoryDao.getInventoryFor(InventoryDao.PROCESSING);
    }

    public List<Inventory> getInventoryForGenomics() {
        return inventoryDao.getInventoryFor(InventoryDao.GENOMICS);
    }

    public List<Inventory> getInventoryForCultureBoxes() {
        return inventoryDao.getInventoryFor(InventoryDao.CULTUREBOXES);
    }

    private String identifyTableName(String item) {
        if (TREE.equals(item))
            return InventoryDao.TREE;
        else if (SHRUB.equals(item))
            return  InventoryDao.SHRUB;
        else if (SEED.equals(item))
            return  InventoryDao.SEED;
        else if (REF_MATERIAL.equals(item))
            return  InventoryDao.REF_MATERIAL;
        else if (PROCESSING.equals(item))
            return  InventoryDao.PROCESSING;
        else if (GENOMICS.equals(item))
            return  InventoryDao.GENOMICS;
        else if (CULTUREBOXES.equals(item))
            return InventoryDao.CULTUREBOXES;
        else
            return null;
    }
}
