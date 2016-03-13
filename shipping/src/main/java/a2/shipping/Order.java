package a2.shipping;

import a2.common.model.OrderItem;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Model for a customer order
 *
 * @since 1.0.0
 */
public class Order {

    private int orderId;
    private Date orderDate;
    private String firstName;
    private String lastName;
    private String phone;
    private String address;
    private String message;
    private boolean shipped;
    private List<OrderItem> orderItems = new ArrayList<>();

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public Date getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isShipped() {
        return shipped;
    }

    public void setShipped(boolean shipped) {
        this.shipped = shipped;
    }

    public List<OrderItem> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<OrderItem> orderItems) {
        this.orderItems = orderItems;
    }

}
