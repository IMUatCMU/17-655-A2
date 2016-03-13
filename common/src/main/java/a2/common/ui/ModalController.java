package a2.common.ui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.util.function.Function;

/**
 * Utility controller to create a new modal UI. Since the presentation of UI breaks the synchronous
 * execution, the modal controller accepts a {@link Runnable} callback to be executed after user
 * clicked 'OK'.
 *
 * @since 1.0.0
 */
public class ModalController {

    @FXML private Label messageLabel;

    private Runnable callback;

    public static void createModal(String title, String message, Window owner, Runnable callback) {
        try {
            Stage stage = new Stage();
            FXMLLoader loader = new FXMLLoader(ModalController.class.getResource("/modal.fxml"));
            Parent root = loader.load();
            ModalController controller = loader.getController();
            controller.setRunnable(callback);

            stage.setScene(new Scene(root));
            stage.setTitle(title);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(owner);
            stage.show();

            controller.setMessage(message);
        } catch (Exception ex) {
            throw new RuntimeException("failed to modal file");
        }
    }

    public void setMessage(String message) {
        messageLabel.setText(message);
    }

    public void setRunnable(Runnable callback) {
        this.callback = callback;
    }

    public void okFired(ActionEvent actionEvent) {
        Node source = (Node) actionEvent.getSource();
        Stage stage  = (Stage) source.getScene().getWindow();
        stage.close();

        if (callback != null)
            callback.run();

        callback = null;
        messageLabel.setText(null);
    }
}
