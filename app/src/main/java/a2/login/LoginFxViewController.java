package a2.login;

import a2.common.exception.AuthenticationFailedException;
import a2.common.exception.DatabaseConnectionException;
import a2.common.ioc.AppBean;
import a2.common.ioc.BeanHolder;
import a2.common.security.MySqlConnection;
import a2.common.ui.ModalController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller handling view related logic for login activities.
 *
 * @since 1.0.0
 */
public class LoginFxViewController implements Initializable, AppBean {

    @FXML private TextField userNameTextField;
    @FXML private PasswordField passwordTextField;
    @FXML private Label userNameErrorLabel;
    @FXML private Label passwordErrorLabel;
    @FXML private TextField databaseAddressTextField;
    @FXML private Label databaseErrorLabel;

    private LoginController loginController;

    /**
     * Lifecycle callback method provided by JavaFX. Here we can safely set the default status of
     * the controls and grab dependency beans.
     *
     * @param location
     * @param resources
     */
    public void initialize(URL location, ResourceBundle resources) {
        resetAllControls();
        loginController = (LoginController) BeanHolder.getBean(LoginController.class.getSimpleName());
    }

    /**
     * User clicked Login
     *
     * @param actionEvent
     */
    public void loginButtonFired(ActionEvent actionEvent) {
        // load control data into form
        LoginForm loginForm = new LoginForm();
        loginForm.setUserName(userNameTextField.getText());
        loginForm.setPassword(passwordTextField.getText());
        loginForm.setDatabaseAddress(databaseAddressTextField.getText());

        // validate user data, display error if not valid
        LoginFormValidationResult validationResult = loginController.validateForm(loginForm);
        if (!validationResult.isValid()) {
            if (validationResult.getMessages().containsKey(LoginForm.KEY_USERNAME)) {
                userNameErrorLabel.setVisible(true);
                userNameErrorLabel.setText(validationResult.getMessages().get(LoginForm.KEY_USERNAME));
            }

            if (validationResult.getMessages().containsKey(LoginForm.KEY_PASSWORD)) {
                passwordErrorLabel.setVisible(true);
                passwordErrorLabel.setText(validationResult.getMessages().get(LoginForm.KEY_PASSWORD));
            }

            if (validationResult.getMessages().containsKey(LoginForm.KEY_DB_ADDR)) {
                databaseErrorLabel.setVisible(true);
                databaseErrorLabel.setText(validationResult.getMessages().get(LoginForm.KEY_DB_ADDR));
            }

            return;
        }

        // call business logic to log user in
        try {
            // do authentication
            loginController.authenticate(loginForm);

            // display modal if success and launch the choice UI when user acknowledges
            ModalController.createModal(
                    "Success",
                    "You have been logged in",
                    ((Node) actionEvent.getSource()).getScene().getWindow(),
                    () -> launchNextScreen((Stage) ((Node) actionEvent.getSource()).getScene().getWindow()));

        } catch (AuthenticationFailedException ex) {
            // display modal if authentication failed.
            ModalController.createModal(
                    "Error",
                    "Your credentials did not match the records",
                    ((Node) actionEvent.getSource()).getScene().getWindow(),
                    () -> {});
        } catch (DatabaseConnectionException ex2) {
            // display modal if connection failed.
            ModalController.createModal(
                    "Error",
                    "Could not contact database",
                    ((Node) actionEvent.getSource()).getScene().getWindow(),
                    () -> {});
        }
    }

    /**
     * User clicked 'Reset' button
     *
     * @param actionEvent
     */
    public void resetButtonFired(ActionEvent actionEvent) {
        resetAllControls();
    }

    /**
     * Prepare the choiec UI and launch it.
     *
     * @param primaryStage
     */
    public void launchNextScreen(Stage primaryStage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/choice.fxml"));
            Parent root = loader.load();
            primaryStage.setTitle("Choose app");
            primaryStage.setScene(new Scene(root, 800, 130));
            primaryStage.show();
        } catch (Exception ex) {
            throw new RuntimeException("Failed to load view file.");
        }

    }

    /**
     * Utility method to reset all UI controls.
     */
    private void resetAllControls() {
        userNameTextField.setText("");
        passwordTextField.setText("");
        userNameErrorLabel.setText("");
        userNameErrorLabel.setVisible(false);
        passwordErrorLabel.setText("");
        passwordErrorLabel.setVisible(false);

        databaseAddressTextField.setText(MySqlConnection.DEFAULT_HOST_ADDRESS);
        databaseErrorLabel.setText("");
        databaseErrorLabel.setVisible(false);
    }

    public LoginController getLoginController() {
        return loginController;
    }

    public void setLoginController(LoginController loginController) {
        this.loginController = loginController;
    }
}
