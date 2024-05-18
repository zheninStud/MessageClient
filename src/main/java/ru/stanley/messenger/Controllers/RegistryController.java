package ru.stanley.messenger.Controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.json.JSONObject;
import ru.stanley.messenger.Handler.ClientConnectionHandler;
import ru.stanley.messenger.Messenger;
import ru.stanley.messenger.Utils.GOSTHashing;
import ru.stanley.messenger.Utils.MessageType;
import ru.stanley.messenger.Utils.WindowsOpener;

import java.util.Base64;

public class RegistryController {

    @FXML
    private Button buttonRegistry;

    @FXML
    private TextField fieldLogin;

    @FXML
    private TextField fieldMail;

    @FXML
    private PasswordField fieldPassword;

    @FXML
    private PasswordField fieldPasswordRepeat;

    @FXML
    private TextField fieldPhone;

    private static final ClientConnectionHandler clientConnectionHandler = Messenger.getClientConnectionHandler();

    @FXML
    void initialize() {

        buttonRegistry.setOnAction(actionEvent -> {

            String login = fieldLogin.getText();
            String email = fieldMail.getText();
            String password = fieldPassword.getText();
            String repeatPassword = fieldPasswordRepeat.getText();
            String phone = fieldPhone.getText();

            if (login.isEmpty() || email.isEmpty() || password.isEmpty() || repeatPassword.isEmpty() || phone.isEmpty()) {
                showAlert("All fields are required!");
                return;
            }

            if (!password.equals(repeatPassword)) {
                showAlert("Passwords do not match!");
                return;
            }

            if (!isValidEmail(email)) {
                showAlert("Invalid email address!");
                return;
            }

            if (!isValidPhoneNumber(phone)) {
                showAlert("Invalid phone number!");
                return;
            }

            registerUser(login, password, email, phone);

        });
    }

    private void registerUser(String username, String password, String email, String phone) {
        byte[] salt = GOSTHashing.generateSalt(16);
        String passwordHash = hashPassword(password, salt);
        String base64Salt = Base64.getEncoder().encodeToString(salt);

        MessageType messageType = MessageType.REGISTER;
        JSONObject jsonMessage = messageType.createJsonObject();

        jsonMessage.getJSONObject("data").put("username", username);
        jsonMessage.getJSONObject("data").put("passwordHash", passwordHash);
        jsonMessage.getJSONObject("data").put("salt", base64Salt);
        jsonMessage.getJSONObject("data").put("email", email);
        jsonMessage.getJSONObject("data").put("phone", phone);

        clientConnectionHandler.sendMessage(messageType.createMessage(jsonMessage));
    }

    public String hashPassword(String password, byte[] salt) {
        byte[] hashedPassword = GOSTHashing.computeHashWithSalt(password, salt);
        return GOSTHashing.encodeSaltAndHash(salt, hashedPassword);
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private boolean isValidEmail(String email) {
        return email.matches("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}");
    }

    private boolean isValidPhoneNumber(String phone) {
        return phone.matches("\\d{10,12}");
    }

    public void checkAlert(String message) {
        switch (message) {
            case "Username":
                showAlert("Username is already taken!");
                clearText();
            case "Email":
                showAlert("Email is already taken!");
                clearText();
            case "Password":
                showAlert("Password is already taken!");
                clearText();
            case "MySQL":
                showAlert("MySQL error!");
                clearText();
        }
    }

    public void clearText() {
        fieldLogin.clear();
        fieldMail.clear();
        fieldPassword.clear();
        fieldPasswordRepeat.clear();
        fieldPhone.clear();
    }

    public void openAuthForm() {
        WindowsOpener.openAndCloseWindows("auth.fxml", (Stage) buttonRegistry.getScene().getWindow());
    }

}
