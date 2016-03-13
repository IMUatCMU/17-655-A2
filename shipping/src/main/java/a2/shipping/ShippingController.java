package a2.shipping;

import a2.common.ioc.AppBean;
import a2.common.ioc.BeanHolder;
import a2.inventory.DecrementInventoryForm;
import a2.inventory.InventoryController;

import java.util.List;

/**
 * Business controller for the shipping app. Note we can largely reuse {@link InventoryController}
 * business logic here for listing inventories and decrement inventory count after shipped.
 *
 * @since 1.0.0
 */
public class ShippingController implements AppBean {

    private ShippingDao shippingDao;
    private InventoryController inventoryController;

    /**
     * Grab dependencies.
     */
    @Override
    public void afterInitialization() {
        shippingDao = (ShippingDao) BeanHolder.getBean(ShippingDao.class.getSimpleName());
        inventoryController = (InventoryController) BeanHolder.getBean(InventoryController.class.getSimpleName());

        assert this.shippingDao != null;
        assert this.inventoryController != null;
    }

    /**
     * List pending orders
     *
     * @return
     */
    public List<Order> getPendingOrders() {
        return shippingDao.queryOrders(false);
    }

    /**
     * List shipped orders
     *
     * @return
     */
    public List<Order> getShippedOrders() {
        return shippingDao.queryOrders(true);
    }

    /**
     * Load order items for a selected order (summary)
     *
     * @param order
     */
    public void loadOrderItems(Order order) {
        order.setOrderItems(shippingDao.queryOrderItems(order.getOrderId()));
    }

    /**
     * Mark the order as shipped.
     *
     * @param order
     */
    public void shipOrder(Order order) {
        // check if it's alredy shipped.
        if (order.isShipped())
            throw new AlreadyShippedException();

        // mark order as shipped
        shippingDao.updateOrderShippingStatus(order.getOrderId(), true);

        // decrement inventory count for each order item
        order.getOrderItems().forEach(orderItem -> {
            DecrementInventoryForm form = new DecrementInventoryForm();
            form.setId(orderItem.getProductId());
            inventoryController.decrementInventory(form);
        });
    }
}
