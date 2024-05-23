package ru.stanley.messenger.Controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.json.JSONObject;
import ru.stanley.messenger.Database.DatabaseConnection;
import ru.stanley.messenger.Handler.ClientConnectionHandler;
import ru.stanley.messenger.Messenger;
import ru.stanley.messenger.Utils.ControllerRegistry;
import ru.stanley.messenger.Utils.DHUtil;
import ru.stanley.messenger.Utils.MessageType;
import ru.stanley.messenger.Utils.WindowsOpener;
import ru.stanley.messenger.Models.User;

import javax.crypto.SecretKey;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.sql.SQLException;
import java.util.Arrays;

public class UserFriendRequestController {

    @FXML
    private Label requestLabel;

    @FXML
    private Button acceptButton;

    @FXML
    private Button denyButton;

    private User user;
    private static final ClientConnectionHandler clientConnectionHandler = Messenger.getClientConnectionHandler();
    private static final DatabaseConnection database = Messenger.getDatabaseConnection();
    private MainController mainController;

    @FXML
    void initialize() {

        acceptButton.setOnAction(event -> {

            try {
                KeyPair dhUtil = DHUtil.initDH();
                PublicKey publicKey = dhUtil.getPublic();
                PrivateKey privateKey = dhUtil.getPrivate();

                PublicKey publicKeyOther = database.selectUserKey(user.getUserId());

                SecretKey secretKey = DHUtil.generateSharedSecret(privateKey, publicKeyOther);

                System.out.println("SecretKey2: " + DHUtil.keyToString(secretKey));

                if (database.updateUserPrivateKey(user.getUserId(), DHUtil.keyToString(secretKey))) {
                    MessageType messageType = MessageType.REGUEST_FRIEND_CLIENT_SUCCESS;
                    JSONObject jsonMessage = messageType.createJsonObject();

                    jsonMessage.getJSONObject("data").put("userId", user.getUserId());
                    jsonMessage.getJSONObject("data").put("publicKey", DHUtil.keyToString(publicKey));

                    clientConnectionHandler.sendMessage(messageType.createMessage(jsonMessage));
                }

                database.deleteUserKey(user.getUserId());

                mainController = (MainController) ControllerRegistry.getController("MainController");

                if (mainController != null) {
                    Platform.runLater(() -> {
                        try {
                            mainController.reloadUser();
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                        mainController.showSuccessNotification("User successfully added");
                    });
                }

                WindowsOpener.closeWindow((Stage) acceptButton.getScene().getWindow());

            } catch (NoSuchAlgorithmException | NoSuchProviderException | SQLException | InvalidKeySpecException |
                     RuntimeException | InvalidKeyException e) {
                throw new RuntimeException(e);
            }

        });

        denyButton.setOnAction(event -> {

            if (database.deleteUserKey(user.getUserId())) {

                MessageType messageType = MessageType.REGUEST_FRIEND_CLIENT_DENY;
                JSONObject jsonMessage = messageType.createJsonObject();

                jsonMessage.getJSONObject("data").put("userId", user.getUserId());

                clientConnectionHandler.sendMessage(messageType.createMessage(jsonMessage));

                mainController = (MainController) ControllerRegistry.getController("MainController");

                if (mainController != null) {
                    Platform.runLater(() -> mainController.showSuccessNotification("User deny added"));
                }
            }

            WindowsOpener.closeWindow((Stage) denyButton.getScene().getWindow());

        });

    }

    public void setUsername(User user) {
        this.user = user;
        requestLabel.setText("Запрос на добавление в друзья от: " + user.getUserName());
    }
}
