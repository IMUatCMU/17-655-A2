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
 * @since 1.0.0
 */
public class A2Application extends Application implements AppBean {

    private static String[] applicationArgs;
    private LoginDao loginDao;

    @Override
    public void afterInitialization() {
        this.loginDao = (LoginDao) BeanHolder.getBean(LoginDao.class.getSimpleName());
    }

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

    @Override
    public void init() throws Exception {
        super.init();

        if (Arrays.asList(applicationArgs).size() < 1) {
            System.out.println("Please provide the first argument as the location for properties file.");
            System.exit(-1);
        }

        BeanHolder.registry().registerBean(this);
        BeanHolder.registry().registerBean(new ApplicationProperties(readProperties(Arrays.asList(applicationArgs).get(0))));
        BeanHolder.registry().registerBean(new LoginController());
        BeanHolder.registry().registerBean(new BCryptPasswordEncoder());
        BeanHolder.registry().registerBean(new LoginDao());
        BeanHolder.registry().registerBean(new ChoiceController());
        BeanHolder.registry().registerBean(new InventoryController());
        BeanHolder.registry().registerBean(new InventoryDao());

        BeanHolder.getAllBeans().forEach(AppBean::afterInitialization);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/login.fxml"));
        Parent root = loader.load();
        primaryStage.setTitle("Login");
        primaryStage.setScene(new Scene(root, 800, 500));
        primaryStage.show();
    }

    @Override
    public void stop() throws Exception {
        Authentication authentication = SessionContextHolder.getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            loginDao.insertLogoutSecurityAudit(authentication.getUserId(), new Date());
            SessionContextHolder.sessionManager().remove();
        }

        super.stop();
    }

    public static void main(String[] args) {
        applicationArgs = args;
        launch(args);
    }
}
