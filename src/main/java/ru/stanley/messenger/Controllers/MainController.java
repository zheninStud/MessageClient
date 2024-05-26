package ru.stanley.messenger.Controllers;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import org.json.JSONObject;
import ru.stanley.messenger.Database.DatabaseConnection;
import ru.stanley.messenger.Handler.ClientConnectionHandler;
import ru.stanley.messenger.Messenger;
import ru.stanley.messenger.Models.User;
import ru.stanley.messenger.Models.UserMessage;
import ru.stanley.messenger.Utils.*;

import java.sql.SQLException;
import java.util.List;


public class MainController {

    @FXML
    private ListView<User> chatList;

    @FXML
    private TextField messageField;

    @FXML
    private VBox messageHistory;

    @FXML
    private Button buttonSend;

    @FXML
    public Button buttonSearchUser;

    @FXML
    public Button buttonUpdate;

    private static final User currentUser = Messenger.getAccountUser();
    private static User selectUser;
    private static final ClientConnectionHandler clientConnectionHandler = Messenger.getClientConnectionHandler();
    private static final DatabaseConnection database = Messenger.getDatabaseConnection();
    private static GOSTEncryptor gostEncryptor;

    @FXML
    void initialize() throws SQLException {

        loadUser();
        checkMessage();

        chatList.setOnMouseClicked(event -> {
            User selectedUser = chatList.getSelectionModel().getSelectedItem();
            if (selectedUser != null) {

                try {
                    messageHistory.getChildren().clear();
                    openChat(selectedUser);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });

        buttonUpdate.setOnAction(actionEvent -> {
            try {
                reloadUser();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });

        buttonSend.setOnAction(actionEvent -> {
            try {
                sendMessage(currentUser, selectUser, messageField.getText());
                messageField.setText("");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        buttonSearchUser.setOnAction(actionEvent -> {
            WindowsOpener.openWindow("userSearchForm.fxml");
        });
    }

    private void checkMessage() {
        MessageType messageTypeSend = MessageType.CHECK_MESSAGE;
        JSONObject jsonMessage = messageTypeSend.createJsonObject();

        jsonMessage.getJSONObject("data").put("userId", currentUser.getUserId());

        clientConnectionHandler.sendMessage(messageTypeSend.createMessage(jsonMessage));
    }

    private void loadUser() throws SQLException {
        ObservableList<User> userList = database.selectAllUser();
        if (userList != null) {
            chatList.setItems(userList);
        }
    }

    private void openChat(User selectedUser) throws Exception {
        selectUser = selectedUser;
        if (selectedUser.checkPrivateKey()) {
            if (database.selectUserKeyAll(selectedUser.getUserId())) {
                WindowsOpener.openWindow("userFriendRequest.fxml");
                UserFriendRequestController userFriendRequestController = (UserFriendRequestController) ControllerRegistry.getController("UserFriendRequestController");
                if (userFriendRequestController != null) {
                    Platform.runLater(() -> userFriendRequestController.setUsername(selectedUser));
                }
            }
        } else {
            gostEncryptor = new GOSTEncryptor(selectedUser.getPrivateKey());
            List<UserMessage> userMessageList = database.selectMessageAll(currentUser.getUserId(), selectedUser.getUserId());
            if (userMessageList != null) {
                for (UserMessage userMessage : userMessageList) {
                    String decodeText = gostEncryptor.decrypt(userMessage.getContent());
                    addMessage(userMessage.getSender(), decodeText);
                }
            }
        }
    }

    public void reloadUser() throws SQLException {
        chatList.getItems().clear();
        ObservableList<User> userList = database.selectAllUser();
        if (userList != null) {
            chatList.setItems(userList);
        }
    }

    private void sendMessage(User user, User receiverUser, String text) throws Exception {
        if (!text.isEmpty()) {
            HBox messageBox = new HBox();
            HBox messageContent = gethBox(user.getUserName(), text);

            messageBox.getChildren().add(messageContent);

            if (user.getUserName().equals(currentUser.getUserName())) {
                messageBox.setAlignment(Pos.CENTER_RIGHT);
            } else {
                messageBox.setAlignment(Pos.CENTER_LEFT);
            }

            messageHistory.getChildren().add(messageBox);

            gostEncryptor = new GOSTEncryptor(receiverUser.getPrivateKey());
            String encodeText = gostEncryptor.encrypt(text);

            database.insertMessage(user.getUserId(), receiverUser.getUserId(), encodeText);

            MessageType messageTypeSend = MessageType.MESSAGE_SENT;
            JSONObject jsonMessage = messageTypeSend.createJsonObject();

            jsonMessage.getJSONObject("data").put("sender", user.getUserId());
            jsonMessage.getJSONObject("data").put("recipient", receiverUser.getUserId());
            jsonMessage.getJSONObject("data").put("message", encodeText);

            clientConnectionHandler.sendMessage(messageTypeSend.createMessage(jsonMessage));
        }
    }

    private void addMessage(User sender, String text) {
        HBox messageBox = new HBox();
        HBox messageContent = gethBox(sender.getUserName(), text);

        messageBox.getChildren().add(messageContent);

        if (sender.getUserName().equals(currentUser.getUserName())) {
            messageBox.setAlignment(Pos.CENTER_RIGHT);
        } else {
            messageBox.setAlignment(Pos.CENTER_LEFT);
        }

        messageHistory.getChildren().add(messageBox);
    }

    public void newMessage(User sender, User receiver, String text) throws Exception {
        if (selectUser != null) {
            if (sender.getUserName().equals(selectUser.getUserName())) {
                database.insertMessage(sender.getUserId(), receiver.getUserId(), text);

                gostEncryptor = new GOSTEncryptor(selectUser.getPrivateKey());
                String decodeText = gostEncryptor.decrypt(text);

                HBox messageBox = new HBox();
                HBox messageContent = gethBox(sender.getUserName(), decodeText);

                messageBox.getChildren().add(messageContent);

                if (sender.getUserName().equals(currentUser.getUserName())) {
                    messageBox.setAlignment(Pos.CENTER_RIGHT);
                } else {
                    messageBox.setAlignment(Pos.CENTER_LEFT);
                }

                messageHistory.getChildren().add(messageBox);
            } else {
                database.insertMessage(sender.getUserId(), receiver.getUserId(), text);
            }
        } else {
            database.insertMessage(sender.getUserId(), receiver.getUserId(), text);
        }
    }

    public void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void showSuccessNotification(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void setPrivateKey(String privateKey) {
        if (selectUser != null) {
            if (selectUser.checkPrivateKey()) {
                selectUser.setPrivateKey(privateKey);
            }
        }
    }

    private HBox gethBox(String sender, String text) {
        Text senderText = new Text(sender + ":");
        Text messageText = new Text(text);

        messageText.setWrappingWidth(180);

        HBox messageContent = new HBox(5, senderText, messageText);
        messageContent.setAlignment(Pos.TOP_LEFT);
        messageContent.setPadding(new Insets(5));
        messageContent.setMaxWidth(200);
        messageContent.setBackground(new Background(new BackgroundFill(sender.equals(currentUser.getUserName()) ? Color.LIGHTBLUE : Color.LIGHTGRAY, CornerRadii.EMPTY, Insets.EMPTY)));

        return messageContent;
    }

}
