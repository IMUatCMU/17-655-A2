package a2.order;

import a2.common.ioc.AppBean;
import a2.common.ioc.BeanHolder;
import a2.common.model.OrderItem;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * Business controller for the order app.
 *
 * @since 1.0.0
 */
public class OrderController implements AppBean {

    private OrderDao orderDao;

    /**
     * Grab dependencies
     */
    @Override
    public void afterInitialization() {
        this.orderDao = (OrderDao) BeanHolder.getBean(OrderDao.class.getSimpleName());
    }

    /**
     * Validate order submission form
     *
     * @param form
     * @return
     */
    public OrderFormValidationResult validateOrder(OrderForm form) {
        OrderFormValidationResult result = new OrderFormValidationResult();
        result.setForm(form);

        if (StringUtils.isEmpty(form.getFirstName()))
            result.getMessages().put("1", "Please provide customer first name");
        if (StringUtils.isEmpty(form.getLastName()))
            result.getMessages().put("2", "Please provide customer last name");
        if (StringUtils.isEmpty(form.getPhone()))
            result.getMessages().put("3", "Please provide customer phone number");
        if (StringUtils.isEmpty(form.getAddress()))
            result.getMessages().put("4", "Please provide customer address");
        if (form.getOrderItems() == null || form.getOrderItems().size() == 0)
            result.getMessages().put("5", "Cart is empty");

        if (result.getMessages().size() == 0)
            result.setIsValid(true);

        return result;
    }

    /**
     * Submit the order. It assumes {@link #validateOrder(OrderForm)} is already passed. It will
     * call {@link OrderDao} to insert summary record first and then insert details (order items)
     * record.
     *
     * @param form
     */
    public void submitOrder(OrderForm form) {
        int orderId = orderDao.insertOrderInfo(
                new Date(),
                form.getFirstName(),
                form.getLastName(),
                form.getPhone(),
                form.getAddress(),
                form.getMessage(),
                calculateTotalPrice(form),
                false);
        form.getOrderItems().forEach(orderItem -> orderDao.insertOrderDetails(orderId, orderItem.getProductId()));
    }

    /**
     * Calculate the price sum of items in an order
     *
     * @param form
     * @return
     */
    public BigDecimal calculateTotalPrice(OrderForm form) {
        return calculateTotalPrice(form.getOrderItems());
    }

    /**
     * Calculate the price sum of items
     *
     * @param orderItems
     * @return
     */
    public BigDecimal calculateTotalPrice(Collection<OrderItem> orderItems) {
        return orderItems
                .stream()
                .map(OrderItem::getUnitPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
