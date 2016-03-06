package a2.inventory;

import a2.common.exception.DuplicateItemException;
import a2.common.exception.NegativeStockException;
import a2.common.ioc.AppBean;
import a2.common.ioc.BeanHolder;
import a2.common.model.Product;
import org.apache.commons.lang3.math.NumberUtils;
import org.w3c.dom.stylesheets.LinkStyle;

import java.util.List;

/**
 * @author Weinan Qiu
 * @since 1.0.0
 */
public class InventoryController implements AppBean {

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
        if (addItemForm.getProduct() == null)
            result.getMessages().put(AddItemForm.ITEM, "please select an inventory category");

        if (result.getMessages().size() == 0)
            result.setIsValid(true);

        return result;
    }

    public void addItem(AddItemForm addItemForm) {
        long count = inventoryDao.countForInventory(addItemForm.getCode(), addItemForm.getProduct().getDatabaseValue());

        if (count > 0)
            throw new DuplicateItemException();

        inventoryDao.insertInventory(
                addItemForm.getCode(),
                addItemForm.getDescription(),
                addItemForm.getQuantity(),
                addItemForm.getPrice(),
                addItemForm.getProduct());
    }

    public void deleteItem(DeleteItemForm form) {
        inventoryDao.deleteInventory(form.getId());
    }

    public void decrementInventory(DecrementInventoryForm form) {
        long currentStock = inventoryDao.countForInventory(form.getId());
        if (currentStock <= 0)
            throw new NegativeStockException();

        inventoryDao.decrementInventory(form.getId());
    }

    public List<Inventory> getInventoryForTrees() {
        return inventoryDao.getInventoryFor(Product.TREE);
    }

    public List<Inventory> getInventoryForShrubs() {
        return inventoryDao.getInventoryFor(Product.SHRUB);
    }

    public List<Inventory> getInventoryForSeeds() {
        return inventoryDao.getInventoryFor(Product.SEED);
    }

    public List<Inventory> getInventoryForReferenceMaterials() {
        return inventoryDao.getInventoryFor(Product.REF_MATERIAL);
    }

    public List<Inventory> getInventoryForProcessing() {
        return inventoryDao.getInventoryFor(Product.PROCESSING);
    }

    public List<Inventory> getInventoryForGenomics() {
        return inventoryDao.getInventoryFor(Product.GENOMICS);
    }

    public List<Inventory> getInventoryForCultureBoxes() {
        return inventoryDao.getInventoryFor(Product.CULTUREBOXES);
    }
}
