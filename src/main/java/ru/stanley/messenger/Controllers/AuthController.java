package ru.stanley.messenger.Controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import ru.stanley.messenger.Database.DatabaseConnection;
import ru.stanley.messenger.Messenger;
import ru.stanley.messenger.Utils.WindowsOpener;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

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

    public static boolean verifyPassword(String enteredPassword, String storedHash) {
        try {
            // Разделение хранимой строки на соль и хеш
            String[] parts = storedHash.split(":");
            String salt = parts[0];
            String storedHexHash = parts[1];

            // Создание экземпляра MessageDigest с алгоритмом SHA-256
            MessageDigest md = MessageDigest.getInstance("SHA-256");

            // Обновление хеша с использованием соли и введенного пароля
            md.update(salt.getBytes(StandardCharsets.UTF_8));
            byte[] enteredHash = md.digest(enteredPassword.getBytes(StandardCharsets.UTF_8));

            // Конвертируем байтовый хеш в строку в формате HEX
            String enteredHexHash = new BigInteger(1, enteredHash).toString(16);

            // Сравнение хешей
            return storedHexHash.equals(enteredHexHash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error while hashing password", e);
        }
    }

}
