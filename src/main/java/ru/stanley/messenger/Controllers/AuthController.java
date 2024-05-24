package ru.stanley.messenger.Controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.json.JSONObject;
import ru.stanley.messenger.Database.DatabaseConnection;
import ru.stanley.messenger.Handler.ClientConnectionHandler;
import ru.stanley.messenger.Messenger;
import ru.stanley.messenger.Models.User;
import ru.stanley.messenger.Utils.GOSTHashing;
import ru.stanley.messenger.Utils.MessageType;
import ru.stanley.messenger.Utils.WindowsOpener;

import java.sql.SQLException;
import java.util.Base64;


public class AuthController {

    private static final ClientConnectionHandler clientConnectionHandler = Messenger.getClientConnectionHandler();

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
            GOSTHashing.requestGenerateSalt(fieldLogin.getText());
        });

        buttonRegister.setOnAction(actionEvent -> WindowsOpener.openAndCloseWindows("registry.fxml", (Stage) buttonRegister.getScene().getWindow()));
    }

    public String hashPassword(String password, String saltString) {
        byte[] convertedByteSalt = Base64.getDecoder().decode(saltString);;
        byte[] hashedPassword = GOSTHashing.computeHashWithSalt(password, convertedByteSalt);
        return GOSTHashing.encodeSaltAndHash(convertedByteSalt, hashedPassword);
    }

    public void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void authMessage(String salt) {
        String passwordHash = hashPassword(fieldPassword.getText(), salt);

        MessageType messageType = MessageType.AUTH;
        JSONObject jsonMessage = messageType.createJsonObject();

        jsonMessage.getJSONObject("data").put("username", fieldLogin.getText());
        jsonMessage.getJSONObject("data").put("passwordHash", passwordHash);

        clientConnectionHandler.sendMessage(messageType.createMessage(jsonMessage));
    }

    public void openMainForm(User user) throws SQLException {
        Messenger.openDatabaseConnection();
        Messenger.setAccountUser(user);
        DatabaseConnection database = Messenger.getDatabaseConnection();
        database.insertUser(user);
        WindowsOpener.openAndCloseWindows("main.fxml", (Stage) buttonAuth.getScene().getWindow());
    }

    public void clearText() {
        showAlert("Incorrect login or password");
        fieldPassword.clear();
    }
}
