package a2.order;

import a2.common.exception.DatabaseConnectionException;
import a2.common.ioc.BeanHolder;
import a2.common.model.OrderItem;
import a2.common.ui.ModalController;
import a2.inventory.Inventory;
import a2.inventory.InventoryController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;

import java.math.BigDecimal;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

/**
 * View controller for the order app.
 *
 * @since 1.0.0
 */
public class OrderFxViewController implements Initializable {

    @FXML private TextField firstNameTextField;
    @FXML private TextField lastNameTextFiedl;
    @FXML private TextField phoneTextField;
    @FXML private TextArea addressTextField;
    @FXML private ListView<Inventory> inventoryListView;
    @FXML private Label totalCostLabel;
    @FXML private ListView<OrderItem> itemsSelectedListView;
    @FXML private TextArea orderMessageTextArea;

    private OrderController orderController;
    private InventoryController inventoryController;

    /**
     * Initialize control state and grab dependencies
     *
     * @param location
     * @param resources
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        inventoryListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        inventoryListView.setCellFactory(param ->
                        new ListCell<Inventory>() {
                            @Override
                            protected void updateItem(Inventory item, boolean empty) {
                                super.updateItem(item, empty);
                                if (item != null)
                                    setText(String.format("%s x %s @ %s (each)",
                                            item.getQuantity().toString(),
                                            item.getDescription(),
                                            item.getPrice().toString()));
                                else
                                    setText(null);
                            }
                        }
        );
        itemsSelectedListView.setEditable(false);
        itemsSelectedListView.setCellFactory(param ->
                        new ListCell<OrderItem>() {
                            @Override
                            protected void updateItem(OrderItem item, boolean empty) {
                                super.updateItem(item, empty);
                                if (item != null)
                                    setText(String.format("%s @ %s",
                                            item.getDescription(),
                                            item.getUnitPrice().toString()));
                                else
                                    setText(null);
                            }
                        }
        );

        this.orderController = (OrderController) BeanHolder.getBean(OrderController.class.getSimpleName());
        this.inventoryController = (InventoryController) BeanHolder.getBean(InventoryController.class.getSimpleName());

        assert this.orderController != null;
        assert this.inventoryController != null;

        resetControls();
    }

    /**
     * User clicked 'trees' button
     * @param actionEvent
     */
    public void treesButtonFired(ActionEvent actionEvent) {
        populateInventoryListView(this.inventoryController.getInventoryForTrees());
    }

    /**
     * User clicked 'shrubs' button
     * @param actionEvent
     */
    public void shrubsButtonFired(ActionEvent actionEvent) {
        populateInventoryListView(this.inventoryController.getInventoryForShrubs());
    }

    /**
     * User clicked 'seeds' button
     * @param actionEvent
     */
    public void seedsButtonFired(ActionEvent actionEvent) {
        populateInventoryListView(this.inventoryController.getInventoryForSeeds());
    }

    /**
     * User clicked 'processing' button
     * @param actionEvent
     */
    public void processingButtonFired(ActionEvent actionEvent) {
        populateInventoryListView(this.inventoryController.getInventoryForProcessing());
    }

    /**
     * User clicked 'reference material' button
     * @param actionEvent
     */
    public void referenceMaterialButtonFired(ActionEvent actionEvent) {
        populateInventoryListView(this.inventoryController.getInventoryForReferenceMaterials());
    }

    /**
     * User clicked 'genomics' button
     * @param actionEvent
     */
    public void genomicsButtonFired(ActionEvent actionEvent) {
        populateInventoryListView(this.inventoryController.getInventoryForGenomics());
    }

    /**
     * User clicked 'culture boxes' button
     * @param actionEvent
     */
    public void cultureBoxesButtonFired(ActionEvent actionEvent) {
        populateInventoryListView(this.inventoryController.getInventoryForCultureBoxes());
    }

    /**
     * load data into the inventory list UI
     *
     * @param inventories
     */
    private void populateInventoryListView(List<Inventory> inventories) {
        this.inventoryListView.setItems(null);
        inventoryListView.setItems(FXCollections.observableList(inventories));
    }

    /**
     * User clicked 'add to order' button
     * @param actionEvent
     */
    public void addToOrderButtonFired(ActionEvent actionEvent) {
        // check selection
        if (this.inventoryListView.getSelectionModel() == null || this.inventoryListView.getSelectionModel().getSelectedItem() == null) {
            ModalController.createModal("Error",
                    "Please select an inventory item to add to order.",
                    ((Node) actionEvent.getSource()).getScene().getWindow(),
                    () -> {
                    });
            return;
        }

        // parse the selected inventory as an order item
        Inventory inventory = this.inventoryListView.getSelectionModel().getSelectedItem();

        OrderItem orderItem = new OrderItem();
        orderItem.setProductId(inventory.getId());
        orderItem.setCode(inventory.getCode());
        orderItem.setDescription(inventory.getDescription());
        orderItem.setUnitPrice(inventory.getPrice());

        // add order item to list
        ObservableList<OrderItem> selectedModel = this.itemsSelectedListView.getItems();
        if (selectedModel == null || selectedModel.size() == 0) {
            selectedModel = FXCollections.observableArrayList();
        }
        selectedModel.add(orderItem);
        this.itemsSelectedListView.setItems(selectedModel);

        // update total price
        BigDecimal totalPrice = orderController.calculateTotalPrice(selectedModel);
        this.totalCostLabel.setText("$" + new DecimalFormat("#,###.00").format(totalPrice));
    }

    /**
     * User clicked 'submit order' button
     * @param actionEvent
     */
    public void submitOrderButtonFired(ActionEvent actionEvent) {

        // create order submission form
        OrderForm form = new OrderForm();
        form.setFirstName(firstNameTextField.getText());
        form.setLastName(lastNameTextFiedl.getText());
        form.setPhone(phoneTextField.getText());
        form.setAddress(addressTextField.getText());
        form.setMessage(orderMessageTextArea.getText());
        form.setOrderItems(this.itemsSelectedListView.getItems());

        // validate form
        OrderFormValidationResult validationResult = orderController.validateOrder(form);
        if (!validationResult.isValid()) {
            String combinedError = validationResult.getMessages().values().stream().collect(Collectors.joining("\n"));
            ModalController.createModal("Error",
                    combinedError,
                    ((Node) actionEvent.getSource()).getScene().getWindow(),
                    () -> {});
            return;
        } else {
            try {
                // submit order
                orderController.submitOrder(form);
                ModalController.createModal("Success",
                        "Order submitted!",
                        ((Node) actionEvent.getSource()).getScene().getWindow(),
                        this::resetControls);
            } catch (DatabaseConnectionException ex) {
                ModalController.createModal("Error",
                        "Operation failed: " + ex.getCause().getMessage(),
                        ((Node) actionEvent.getSource()).getScene().getWindow(),
                        () -> {});
            }
        }
    }

    private void resetControls() {
        Arrays.asList(firstNameTextField,
                lastNameTextFiedl,
                phoneTextField,
                addressTextField,
                orderMessageTextArea).forEach(textInputControl1 -> textInputControl1.setText(""));
        totalCostLabel.setText("$0.00");
        Arrays.asList(inventoryListView, itemsSelectedListView).forEach(listView -> listView.setItems(null));
    }
}
