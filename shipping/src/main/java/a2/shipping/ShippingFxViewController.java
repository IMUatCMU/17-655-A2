package a2.shipping;

import a2.common.exception.DatabaseConnectionException;
import a2.common.ioc.BeanHolder;
import a2.common.model.OrderItem;
import a2.common.ui.ModalController;
import a2.inventory.Inventory;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import org.apache.commons.lang3.StringUtils;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

/**
 * @author Weinan Qiu
 * @since 1.0.0
 */
public class ShippingFxViewController implements Initializable {

    @FXML private ListView<Order> orderListView;
    @FXML private Label firstNameLabel;
    @FXML private Label lastNameLabel;
    @FXML private Label phoneLabel;
    @FXML private Label orderDateLabel;
    @FXML private Label addressLabel;
    @FXML private Label messageLabel;
    @FXML private ListView<OrderItem> orderItemsListView;

    private ShippingController shippingController;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.shippingController = (ShippingController) BeanHolder.getBean(ShippingController.class.getSimpleName());
        assert this.shippingController != null;

        orderListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        orderListView.setCellFactory(param ->
                        new ListCell<Order>() {
                            @Override
                            protected void updateItem(Order item, boolean empty) {
                                super.updateItem(item, empty);
                                if (item != null)
                                    setText(String.format("%s : %s : %s",
                                            item.getOrderId(),
                                            new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(item.getOrderDate()),
                                            String.format("%s %s", item.getFirstName(), item.getLastName())));
                                else
                                    setText(null);
                            }
                        }
        );

        orderItemsListView.setEditable(false);
        orderItemsListView.setCellFactory(param ->
                        new ListCell<OrderItem>() {
                            @Override
                            protected void updateItem(OrderItem item, boolean empty) {
                                super.updateItem(item, empty);
                                if (item != null)
                                    setText(String.format("%s : %s",
                                            item.getCode(),
                                            item.getDescription()));
                                else
                                    setText(null);
                            }
                        }
        );

        resetAllControls();
    }

    public void showPendingOrderButtonFired(ActionEvent actionEvent) {
        List<Order> pendingOrders = shippingController.getPendingOrders();
        if (pendingOrders == null || pendingOrders.size() == 0) {
            ModalController.createModal("Info",
                    "Everything is shipped!",
                    ((Node) actionEvent.getSource()).getScene().getWindow(),
                    this::resetAllControls);
            return;
        }

        this.orderListView.setItems(null);
        this.orderListView.setItems(FXCollections.observableList(pendingOrders));
    }

    public void showShippedOrdersButtonFired(ActionEvent actionEvent) {
        List<Order> shippedOrders = shippingController.getShippedOrders();
        if (shippedOrders == null || shippedOrders.size() == 0) {
            ModalController.createModal("Info",
                    "We haven't shipped anything yet.",
                    ((Node) actionEvent.getSource()).getScene().getWindow(),
                    () -> {
                    });
            return;
        }

        this.orderListView.setItems(null);
        this.orderListView.setItems(FXCollections.observableList(shippedOrders));
    }

    public void selectOrderButtonFired(ActionEvent actionEvent) {
        if (this.orderListView.getSelectionModel() == null || this.orderListView.getSelectionModel().getSelectedItem() == null) {
            ModalController.createModal("Info",
                    "Please select an order",
                    ((Node) actionEvent.getSource()).getScene().getWindow(),
                    () -> {});
            return;
        }

        Order order = this.orderListView.getSelectionModel().getSelectedItem();
        this.shippingController.loadOrderItems(order);

        firstNameLabel.setText(order.getFirstName());
        lastNameLabel.setText(order.getLastName());
        phoneLabel.setText(order.getPhone());
        addressLabel.setText(order.getAddress());
        orderDateLabel.setText(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(order.getOrderDate()));
        messageLabel.setText(order.getMessage());

        this.orderItemsListView.setItems(null);
        this.orderItemsListView.setItems(FXCollections.observableList(order.getOrderItems()));
    }

    public void markAsShippedButtonFired(ActionEvent actionEvent) {
        if (this.orderListView.getSelectionModel() == null) {
            ModalController.createModal("Info",
                    "Please select an order",
                    ((Node) actionEvent.getSource()).getScene().getWindow(),
                    () -> {});
            return;
        }

        Order order = this.orderListView.getSelectionModel().getSelectedItem();
        ModalController.createModal("Confirm",
                "Confirm to ship order #" + order.getOrderId() + "?\n\tOK to ship\n\tClose window to abort",
                ((Node) actionEvent.getSource()).getScene().getWindow(),
                () -> {
                    try {
                        shippingController.shipOrder(order);
                        ModalController.createModal("Info",
                                "Order shipped!",
                                ((Node) actionEvent.getSource()).getScene().getWindow(),
                                () -> {
                                    this.resetOrderDetailControls();
                                    this.showPendingOrderButtonFired(actionEvent);
                                });
                    } catch (DatabaseConnectionException ex) {
                        ModalController.createModal("Error",
                                "Operation Failed: " + ex.getCause().getMessage(),
                                ((Node) actionEvent.getSource()).getScene().getWindow(),
                                () -> {});
                    } catch (AlreadyShippedException ex2) {
                        ModalController.createModal("Error",
                                "This order was already shipped!",
                                ((Node) actionEvent.getSource()).getScene().getWindow(),
                                () -> {});
                    }
                });
    }

    private void resetAllControls() {
        orderListView.setItems(null);
        resetOrderDetailControls();
    }

    private void resetOrderDetailControls() {
        Arrays.asList(
                firstNameLabel,
                lastNameLabel,
                phoneLabel,
                addressLabel,
                messageLabel,
                addressLabel,
                orderDateLabel).forEach(label -> label.setText(""));
        orderItemsListView.setItems(null);
    }
}
