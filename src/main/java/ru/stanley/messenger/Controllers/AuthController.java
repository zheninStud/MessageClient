package ru.stanley.messenger.Controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import ru.stanley.messenger.Database.DatabaseConnection;
import ru.stanley.messenger.Messenger;
import ru.stanley.messenger.Utils.WindowsOpener;

public class AuthController {

    private static final Messenger application = Messenger.getInstance();

    @FXML
    private Button buttonAuth;

    @FXML
    private Button buttonRegister;

    @FXML
    private TextField fieldLogin;

    @FXML
    private PasswordField fieldPassword;

    @FXML
    void initialize() {
        buttonAuth.setOnAction(actionEvent ->
        {
            if (fieldLogin.getText().equals("test")) {
                Messenger.setAccountName("test");
                application.openDatabaseConnection();
                WindowsOpener.openAndCloseWindows("main.fxml", (Stage) buttonAuth.getScene().getWindow());
            } else if (fieldLogin.getText().equals("test2")) {
                Messenger.setAccountName("test2");
                WindowsOpener.openAndCloseWindows("main.fxml", (Stage) buttonAuth.getScene().getWindow());
            }
        });
        buttonRegister.setOnAction(actionEvent -> WindowsOpener.openAndCloseWindows("registry.fxml", (Stage) buttonRegister.getScene().getWindow()));
    }

}
