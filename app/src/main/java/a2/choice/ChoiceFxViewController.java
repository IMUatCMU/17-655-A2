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
 * View Controller for the choice UI.
 *
 * @since 1.0.0
 */
public class ChoiceFxViewController implements Initializable {

    private ChoiceController choiceController;

    /**
     * Grab dependency.
     *
     * @param location
     * @param resources
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.choiceController = (ChoiceController) BeanHolder.getBean(ChoiceController.class.getSimpleName());

        assert this.choiceController != null;
    }

    /**
     * User selected 'Inventory' app
     *
     * @param actionEvent
     */
    public void inventoryButtonFired(ActionEvent actionEvent) {
        // check permission
        if (!this.choiceController.hasSufficientPermission(Permission.INVENTORY))
            displayInsufficientPermissionModal("Inventory", ((Node) actionEvent.getSource()).getScene().getWindow());
        else {
            launchNextScreen(
                    (Stage) ((Node) actionEvent.getSource()).getScene().getWindow(),
                    "Inventory",
                    "/inventory.fxml");
        }
    }

    /**
     * User selected 'Order' app
     *
     * @param actionEvent
     */
    public void orderButtonFired(ActionEvent actionEvent) {
        // check permission
        if (!this.choiceController.hasSufficientPermission(Permission.ORDER))
            displayInsufficientPermissionModal("Order", ((Node) actionEvent.getSource()).getScene().getWindow());
        else {
            launchNextScreen(
                    (Stage) ((Node) actionEvent.getSource()).getScene().getWindow(),
                    "Order",
                    "/order.fxml");
        }
    }

    /**
     * User selected 'Shipping' app
     *
     * @param actionEvent
     */
    public void shippingButtonFired(ActionEvent actionEvent) {
        // check permission
        if (!this.choiceController.hasSufficientPermission(Permission.SHIPPING))
            displayInsufficientPermissionModal("Shipping", ((Node) actionEvent.getSource()).getScene().getWindow());
        else {
            launchNextScreen(
                    (Stage) ((Node) actionEvent.getSource()).getScene().getWindow(),
                    "Shipping",
                    "/shipping.fxml");
        }
    }

    /**
     * Utility method to show 'permission insufficient' modal UI.
     *
     * @param appToAccess
     * @param owner
     */
    private void displayInsufficientPermissionModal(String appToAccess, Window owner) {
        ModalController.createModal(
                "Error",
                String.format("You do not have permission to access %s App.", appToAccess),
                owner,
                () -> {});
    }

    /**
     * Prepare one of the 'Inventory', 'Order', 'Shipping' app UI and launches it.
     *
     * @param primaryStage
     * @param title
     * @param fxmlLocation
     */
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
