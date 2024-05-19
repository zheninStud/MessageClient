package ru.stanley.messenger.Controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.json.JSONObject;
import ru.stanley.messenger.Handler.ClientConnectionHandler;
import ru.stanley.messenger.Messenger;
import ru.stanley.messenger.Utils.MessageType;
import ru.stanley.messenger.Utils.WindowsOpener;

public class UserSearchController {

    private static final ClientConnectionHandler clientConnectionHandler = Messenger.getClientConnectionHandler();

    @FXML
    private TextField searchField;

    @FXML
    private Button searchButton;

    @FXML
    void initialize() {

        searchButton.setOnAction(event -> {

            MessageType messageType = MessageType.GET_USER;
            JSONObject jsonMessage = messageType.createJsonObject();

            jsonMessage.getJSONObject("data").put("username", searchField.getText());

            clientConnectionHandler.sendMessage(messageType.createMessage(jsonMessage));

            WindowsOpener.closeWindow((Stage) searchButton.getScene().getWindow());

        });

    }

}
