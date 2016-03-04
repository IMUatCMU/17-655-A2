package a2.shipping;

import a2.common.ioc.AppBean;
import a2.common.ioc.BeanHolder;
import a2.inventory.DecrementInventoryForm;
import a2.inventory.InventoryController;

import java.util.List;

/**
 * @author Weinan Qiu
 * @since 1.0.0
 */
public class ShippingController implements AppBean {

    private ShippingDao shippingDao;
    private InventoryController inventoryController;

    @Override
    public void afterInitialization() {
        shippingDao = (ShippingDao) BeanHolder.getBean(ShippingDao.class.getSimpleName());
        inventoryController = (InventoryController) BeanHolder.getBean(InventoryController.class.getSimpleName());

        assert this.shippingDao != null;
        assert this.inventoryController != null;
    }

    public List<Order> getPendingOrders() {
        return shippingDao.queryOrders(false);
    }

    public List<Order> getShippedOrders() {
        return shippingDao.queryOrders(true);
    }

    public void loadOrderItems(Order order) {
        order.setOrderItems(shippingDao.queryOrderItems(order.getOrderTable()));
    }

    public void shipOrder(Order order) {
        if (order.isShipped())
            throw new AlreadyShippedException();
        shippingDao.updateOrderShippingStatus(order.getOrderId(), true);

        // TODO. perform decrement
    }
}
