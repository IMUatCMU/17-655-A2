package a2.inventory;

import a2.common.exception.DatabaseConnectionException;
import a2.common.exception.DuplicateItemException;
import a2.common.ioc.BeanHolder;
import a2.common.model.Product;
import a2.common.ui.ModalController;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

/**
 *
 * @since 1.0.0
 */
public class InventoryFxViewController implements Initializable {

    @FXML private TextField productIdTextField;
    @FXML private TextField priceTextField;
    @FXML private TextField quantityTextField;
    @FXML private TextArea productDescriptionTextField;
    @FXML private RadioButton treesRadio;
    @FXML private RadioButton seedsRadio;
    @FXML private RadioButton shurbsRadio;
    @FXML private RadioButton cultureBoxesRadio;
    @FXML private RadioButton genomicsRadio;
    @FXML private RadioButton processingRadio;
    @FXML private RadioButton referenceMaterialRadio;
    @FXML private ListView<Inventory> inventoryListView;

    private InventoryController inventoryController;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ToggleGroup toggleGroup = new ToggleGroup();
        Arrays.asList(treesRadio,
                seedsRadio,
                shurbsRadio,
                cultureBoxesRadio,
                genomicsRadio,
                processingRadio,
                referenceMaterialRadio).forEach(radioButton -> radioButton.setToggleGroup(toggleGroup));
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

        resetControls();

        inventoryController = (InventoryController) BeanHolder.getBean(InventoryController.class.getSimpleName());
        assert inventoryController != null;
    }

    private void resetControls() {
        Arrays.asList(treesRadio,
                seedsRadio,
                shurbsRadio,
                cultureBoxesRadio,
                genomicsRadio,
                processingRadio,
                referenceMaterialRadio).forEach(radioButton -> radioButton.setSelected(false));
        Arrays.asList(productDescriptionTextField,
                productIdTextField,
                priceTextField,
                quantityTextField).forEach(textInputControl -> textInputControl.setText(""));
    }

    public void addItemButtonFired(ActionEvent actionEvent) {
        AddItemForm form = new AddItemForm();
        form.setCode(productIdTextField.getText());
        form.setDescription(productDescriptionTextField.getText());
        form.setPrice(priceTextField.getText());
        form.setQuantity(quantityTextField.getText());
        if (treesRadio.isSelected())
            form.setProduct(Product.TREE);
        else if (shurbsRadio.isSelected())
            form.setProduct(Product.SHRUB);
        else if (seedsRadio.isSelected())
            form.setProduct(Product.SEED);
        else if (referenceMaterialRadio.isSelected())
            form.setProduct(Product.REF_MATERIAL);
        else if (processingRadio.isSelected())
            form.setProduct(Product.PROCESSING);
        else if (genomicsRadio.isSelected())
            form.setProduct(Product.GENOMICS);
        else if (cultureBoxesRadio.isSelected())
            form.setProduct(Product.CULTUREBOXES);

        AddItemValidationResult validationResult = inventoryController.validateAddItem(form);
        if (!validationResult.isValid()) {
            String compiledMessages = validationResult.getMessages().values().stream().collect(Collectors.joining("\n"));
            ModalController.createModal("Error",
                    compiledMessages,
                    ((Node) actionEvent.getSource()).getScene().getWindow(),
                    () -> {});
        } else {
            try {
                inventoryController.addItem(form);
                ModalController.createModal("Success",
                        "Adding the item was successful!",
                        ((Node) actionEvent.getSource()).getScene().getWindow(),
                        () -> resetControls());
            } catch (DuplicateItemException ex) {
                ModalController.createModal("Error",
                        "Item with the same code already exists",
                        ((Node) actionEvent.getSource()).getScene().getWindow(),
                        () -> {});
            } catch (DatabaseConnectionException ex2) {
                ModalController.createModal("Error",
                        "Operation failed: " + ex2.getCause().getMessage(),
                        ((Node) actionEvent.getSource()).getScene().getWindow(),
                        () -> {});
            }
        }
    }

    public void listItemsButtonFired(ActionEvent actionEvent) {
        List<Inventory> inventories = new ArrayList<>();
        if (treesRadio.isSelected())
            inventories = inventoryController.getInventoryForTrees();
        else if (shurbsRadio.isSelected())
            inventories = inventoryController.getInventoryForShrubs();
        else if (seedsRadio.isSelected())
            inventories = inventoryController.getInventoryForSeeds();
        else if (referenceMaterialRadio.isSelected())
            inventories = inventoryController.getInventoryForReferenceMaterials();
        else if (processingRadio.isSelected())
            inventories = inventoryController.getInventoryForProcessing();
        else if (genomicsRadio.isSelected())
            inventories = inventoryController.getInventoryForGenomics();
        else if (cultureBoxesRadio.isSelected())
            inventories = inventoryController.getInventoryForCultureBoxes();
        else {
            ModalController.createModal("Info",
                    "Please select an inventory category.",
                    ((Node) actionEvent.getSource()).getScene().getWindow(),
                    () -> {});
        }

        inventoryListView.setItems(null);
        inventoryListView.setItems(FXCollections.observableList(inventories));
    }

    public void deleteItemButtonFired(ActionEvent actionEvent) {
        DeleteItemForm form = new DeleteItemForm();
        if (inventoryListView.getSelectionModel().getSelectedItem() == null) {
            ModalController.createModal("Info",
                    "Please select an item",
                    ((Node) actionEvent.getSource()).getScene().getWindow(),
                    () -> {});
            return;
        }

        form.setId(inventoryListView.getSelectionModel().getSelectedItem().getId());
        try {
            inventoryController.deleteItem(form);
            ModalController.createModal("Success",
                    "Deleting the item was successful!",
                    ((Node) actionEvent.getSource()).getScene().getWindow(),
                    () -> {
                        listItemsButtonFired(actionEvent);
                        resetControls();
                    });
        } catch (DatabaseConnectionException ex) {
            ModalController.createModal("Error",
                    "Operation failed: " + ex.getCause().getMessage(),
                    ((Node) actionEvent.getSource()).getScene().getWindow(),
                    () -> {});
        }
    }

    public void decrementButtonFired(ActionEvent actionEvent) {
        DecrementInventoryForm form = new DecrementInventoryForm();
        if (inventoryListView.getSelectionModel().getSelectedItem() == null) {
            ModalController.createModal("Info",
                    "Please select an item",
                    ((Node) actionEvent.getSource()).getScene().getWindow(),
                    () -> {});
            return;
        }

        form.setId(inventoryListView.getSelectionModel().getSelectedItem().getId());

        try {
            inventoryController.decrementInventory(form);
            listItemsButtonFired(actionEvent);
        } catch (DatabaseConnectionException ex) {
            ModalController.createModal("Error",
                    "Operation failed: " + ex.getCause().getMessage(),
                    ((Node) actionEvent.getSource()).getScene().getWindow(),
                    () -> {});
        }
    }
}
