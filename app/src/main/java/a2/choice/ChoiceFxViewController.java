package a2.choice;

import a2.common.ioc.AppBean;
import a2.common.ioc.BeanHolder;
import a2.common.security.Permission;
import a2.common.ui.ModalController;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * @author Weinan Qiu
 * @since 1.0.0
 */
public class ChoiceFxViewController implements Initializable {

    private ChoiceController choiceController;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.choiceController = (ChoiceController) BeanHolder.getBean(ChoiceController.class.getSimpleName());

        assert this.choiceController != null;
    }

    public void inventoryButtonFired(ActionEvent actionEvent) {
        if (!this.choiceController.hasSufficientPermission(Permission.INVENTORY))
            displayInsufficientPermissionModal("Inventory", ((Node) actionEvent.getSource()).getScene().getWindow());
        else {
            launchNextScreen(
                    (Stage) ((Node) actionEvent.getSource()).getScene().getWindow(),
                    "Inventory",
                    "/inventory.fxml");
        }
    }

    public void orderButtonFired(ActionEvent actionEvent) {
        if (!this.choiceController.hasSufficientPermission(Permission.ORDER))
            displayInsufficientPermissionModal("Order", ((Node) actionEvent.getSource()).getScene().getWindow());
        else {
            launchNextScreen(
                    (Stage) ((Node) actionEvent.getSource()).getScene().getWindow(),
                    "Order",
                    "/order.fxml");
        }
    }

    public void shippingButtonFired(ActionEvent actionEvent) {
        if (!this.choiceController.hasSufficientPermission(Permission.SHIPPING))
            displayInsufficientPermissionModal("Shipping", ((Node) actionEvent.getSource()).getScene().getWindow());
        else {
            launchNextScreen(
                    (Stage) ((Node) actionEvent.getSource()).getScene().getWindow(),
                    "Shipping",
                    "/shipping.fxml");
        }
    }

    private void displayInsufficientPermissionModal(String appToAccess, Window owner) {
        ModalController.createModal(
                "Error",
                String.format("You do not have permission to access %s App.", appToAccess),
                owner,
                () -> {});
    }

    private void launchNextScreen(Stage primaryStage, String title, String fxmlLocation) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlLocation));
            Parent root = loader.load();
            primaryStage.setTitle(title);
            primaryStage.setScene(new Scene(root, 1000, 800));
            primaryStage.show();
        } catch (Exception ex) {
            throw new RuntimeException("Failed to load view file.");
        }
    }
}
