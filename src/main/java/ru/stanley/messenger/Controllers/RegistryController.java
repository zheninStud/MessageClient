package ru.stanley.messenger.Controllers;


import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

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

    @FXML
    void initialize() {

        if (fieldPassword == fieldPasswordRepeat) {

        }

    }

}
