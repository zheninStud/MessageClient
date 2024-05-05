package ru.stanley.messenger.Controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.json.JSONObject;
import ru.stanley.messenger.Handler.ClientConnectionHandler;
import ru.stanley.messenger.Messenger;
import ru.stanley.messenger.Utils.MessageType;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

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

            // Проверка наличия текста в каждом поле
            if (login.isEmpty() || email.isEmpty() || password.isEmpty() || repeatPassword.isEmpty() || phone.isEmpty()) {
                showAlert("All fields are required!");
                return;
            }

            // Проверка соответствия паролей
            if (!password.equals(repeatPassword)) {
                showAlert("Passwords do not match!");
                return;
            }

            // Проверка корректности введенной почты
            if (!isValidEmail(email)) {
                showAlert("Invalid email address!");
                return;
            }

            // Проверка корректности введенного номера телефона
            if (!isValidPhoneNumber(phone)) {
                showAlert("Invalid phone number!");
                return;
            }

            registerUser(login, password, email, phone);

        });
    }

    private void registerUser(String username, String password, String email, String phone) {
        String passwordHash = hashPassword(password);

        MessageType messageType = MessageType.REGISTER;
        JSONObject jsonMessage = messageType.createJsonObject();

        jsonMessage.getJSONObject("data").put("username", username);
        jsonMessage.getJSONObject("data").put("passwordHash", passwordHash);
        jsonMessage.getJSONObject("data").put("email", email);
        jsonMessage.getJSONObject("data").put("phone", phone);

        System.out.println(jsonMessage);
        clientConnectionHandler.sendMessage(messageType.createMessage(jsonMessage));
    }

    public String hashPassword(String password) {
        try {
            // Генерируем случайную соль
            byte[] salt = new byte[16];
            SecureRandom secureRandom = new SecureRandom();
            secureRandom.nextBytes(salt);
            String generatedSalt = new BigInteger(1, salt).toString(16);

            // Создаем экземпляр MessageDigest с алгоритмом SHA-256
            MessageDigest md = MessageDigest.getInstance("SHA-256");

            // Обновляем хеш с использованием соли и байтов представления пароля
            md.update(generatedSalt.getBytes(StandardCharsets.UTF_8));
            byte[] hashedPassword = md.digest(password.getBytes(StandardCharsets.UTF_8));

            // Конвертируем байтовый хеш в строку в формате HEX
            String hexHash = new BigInteger(1, hashedPassword).toString(16);

            // Форматируем результат в виде "соль:хеш"
            return generatedSalt + ":" + hexHash;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error while hashing password", e);
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Метод для проверки корректности введенной почты
    private boolean isValidEmail(String email) {
        return email.matches("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}");
    }

    // Метод для проверки корректности введенного номера телефона
    private boolean isValidPhoneNumber(String phone) {
        return phone.matches("\\d{10,12}");
    }

}
