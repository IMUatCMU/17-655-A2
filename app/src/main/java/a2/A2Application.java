package a2;

import a2.choice.ChoiceController;
import a2.common.config.ApplicationProperties;
import a2.common.ioc.AppBean;
import a2.common.ioc.BeanHolder;
import a2.common.security.Authentication;
import a2.common.security.BCryptPasswordEncoder;
import a2.common.security.SessionContextHolder;
import a2.inventory.InventoryController;
import a2.inventory.InventoryDao;
import a2.login.LoginController;
import a2.login.LoginDao;
import a2.migration.MigrationController;
import a2.migration.MigrationDao;
import a2.order.OrderController;
import a2.order.OrderDao;
import a2.shipping.ShippingController;
import a2.shipping.ShippingDao;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Date;
import java.util.Properties;

/**
 * This is the entry point for the A2 assignment system. It kick-starts the JavaFX application through
 * the main method.
 *
 * It is created as a {@link Application} subclass, hence it will receive JavaFX
 * lifecycle callbacks.
 *
 * It is also registered as an {@link AppBean}, which will receive container lifecycle callbacks in
 * order to grab its dependencies.
 *
 * @since 1.0.0
 */
public class A2Application extends Application implements AppBean {

    /**
     * Cache application arguments used to extract the path of the configuration file.
     */
    private static String[] applicationArgs;

    /**
     * Login data access
     */
    private LoginDao loginDao;

    /**
     * Acquire dependencies
     */
    @Override
    public void afterInitialization() {
        this.loginDao = (LoginDao) BeanHolder.getBean(LoginDao.class.getSimpleName());
    }

    /**
     * Read 'application.properties' file from the provided location and parse it into
     * a {@link Properties} object.
     *
     * @param location
     * @return
     */
    private Properties readProperties(String location) {
        Properties prop = new Properties();
        InputStream input = null;
        try {
            input = new FileInputStream(location + "/application.properties");
            prop.load(input);
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return prop;
    }

    /**
     * JavaFX lifecycle callback for initialization. Here we register all beans (dependencies) in
     * the system and give them a callback so that they know dependencies are all prepared and ready
     * to be grabbed.
     *
     * @throws Exception
     */
    @Override
    public void init() throws Exception {
        super.init();

        // require 'application.properties' location argument
        if (Arrays.asList(applicationArgs).size() < 1) {
            System.out.println("Please provide the first argument as the location for properties file.");
            System.exit(-1);
        }

        // register beans that implemented AppBean interface
        BeanHolder.registry().registerBean(this);
        BeanHolder.registry().registerBean(new ApplicationProperties(readProperties(Arrays.asList(applicationArgs).get(0))));
        BeanHolder.registry().registerBean(new LoginController());
        BeanHolder.registry().registerBean(new BCryptPasswordEncoder());
        BeanHolder.registry().registerBean(new LoginDao());
        BeanHolder.registry().registerBean(new ChoiceController());
        BeanHolder.registry().registerBean(new InventoryController());
        BeanHolder.registry().registerBean(new InventoryDao());
        BeanHolder.registry().registerBean(new OrderController());
        BeanHolder.registry().registerBean(new OrderDao());
        BeanHolder.registry().registerBean(new ShippingController());
        BeanHolder.registry().registerBean(new ShippingDao());
        BeanHolder.registry().registerBean(new MigrationDao());
        BeanHolder.registry().registerBean(new MigrationController());

        // notify the callback so all beans can grab reference to their dependencies
        BeanHolder.getAllBeans().forEach(AppBean::afterInitialization);
    }

    /**
     * JavaFX lifecycle callback for application start event. Here we construct the UI and render
     * the login screen
     *
     * @param primaryStage
     * @throws Exception
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/login.fxml"));
        Parent root = loader.load();
        primaryStage.setTitle("Login");
        primaryStage.setScene(new Scene(root, 800, 500));
        primaryStage.show();
    }

    /**
     * JavaFX lifecycle callback for application stop event. Here we clear the session context
     * and audit the logout event for the authenticated user.
     *
     * @throws Exception
     */
    @Override
    public void stop() throws Exception {
        Authentication authentication = SessionContextHolder.getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            loginDao.insertLogoutSecurityAudit(authentication.getUserId(), new Date());
            SessionContextHolder.sessionManager().remove();
        }

        super.stop();
    }

    /**
     * Cache the arguments and launch the JavaFX application
     * @param args
     */
    public static void main(String[] args) {
        applicationArgs = args;
        launch(args);
    }
}
