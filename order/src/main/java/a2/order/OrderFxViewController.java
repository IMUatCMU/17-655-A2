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
 * @author Weinan Qiu
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

    public void treesButtonFired(ActionEvent actionEvent) {
        populateInventoryListView(this.inventoryController.getInventoryForTrees());
    }

    public void shrubsButtonFired(ActionEvent actionEvent) {
        populateInventoryListView(this.inventoryController.getInventoryForShrubs());
    }

    public void seedsButtonFired(ActionEvent actionEvent) {
        populateInventoryListView(this.inventoryController.getInventoryForSeeds());
    }

    public void processingButtonFired(ActionEvent actionEvent) {
        populateInventoryListView(this.inventoryController.getInventoryForProcessing());
    }

    public void referenceMaterialButtonFired(ActionEvent actionEvent) {
        populateInventoryListView(this.inventoryController.getInventoryForReferenceMaterials());
    }

    public void genomicsButtonFired(ActionEvent actionEvent) {
        populateInventoryListView(this.inventoryController.getInventoryForGenomics());
    }

    public void cultureBoxesButtonFired(ActionEvent actionEvent) {
        populateInventoryListView(this.inventoryController.getInventoryForCultureBoxes());
    }

    private void populateInventoryListView(List<Inventory> inventories) {
        this.inventoryListView.setItems(null);
        inventoryListView.setItems(FXCollections.observableList(inventories));
    }

    public void addToOrderButtonFired(ActionEvent actionEvent) {
        if (this.inventoryListView.getSelectionModel() == null) {
            ModalController.createModal("Error",
                    "Please select an inventory item to add to order.",
                    ((Node) actionEvent.getSource()).getScene().getWindow(),
                    () -> {
                    });
            return;
        }

        Inventory inventory = this.inventoryListView.getSelectionModel().getSelectedItem();
        OrderItem orderItem = new OrderItem();
        orderItem.setProductId(inventory.getCode());
        orderItem.setDescription(inventory.getDescription());
        orderItem.setUnitPrice(inventory.getPrice());

        ObservableList<OrderItem> selectedModel = this.itemsSelectedListView.getItems();
        if (selectedModel == null || selectedModel.size() == 0) {
            selectedModel = FXCollections.observableArrayList();
        }
        selectedModel.add(orderItem);
        this.itemsSelectedListView.setItems(selectedModel);

        BigDecimal totalPrice = orderController.calculateTotalPrice(selectedModel);
        this.totalCostLabel.setText("$" + new DecimalFormat("#,###.00").format(totalPrice));
    }

    public void submitOrderButtonFired(ActionEvent actionEvent) {
        OrderForm form = new OrderForm();
        form.setFirstName(firstNameTextField.getText());
        form.setLastName(lastNameTextFiedl.getText());
        form.setPhone(phoneTextField.getText());
        form.setAddress(addressTextField.getText());
        form.setMessage(orderMessageTextArea.getText());
        form.setOrderItems(this.itemsSelectedListView.getItems());

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
