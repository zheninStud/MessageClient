package ru.stanley.messenger.Controllers;


import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import ru.stanley.messenger.Database.DatabaseConnection;
import ru.stanley.messenger.Handler.ClientConnectionHandler;
import ru.stanley.messenger.Messenger;
import ru.stanley.messenger.Models.Message;
import ru.stanley.messenger.Models.User;
import ru.stanley.messenger.Utils.ControllerRegistry;
import ru.stanley.messenger.Utils.GOSTEncryptor;
import ru.stanley.messenger.Utils.WindowsOpener;

import java.sql.SQLException;


public class MainController {

    @FXML
    private ListView<User> chatList;

    @FXML
    private TextField messageField;

    @FXML
    private VBox messageHistory;

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private SplitPane splitPane;

    @FXML
    private Button buttonSend;

    @FXML
    public Button buttonSearchUser;

    @FXML
    public Button buttonUpdate;

    private static final User currentUser = Messenger.getAccountUser();
    private static final ClientConnectionHandler clientConnectionHandler = Messenger.getClientConnectionHandler();
    private static final DatabaseConnection database = Messenger.getDatabaseConnection();
    private static GOSTEncryptor gostEncryptor;

    @FXML
    void initialize() throws SQLException {

        loadUser();


        //chatList.getItems().addAll("Chat 1", "Chat 2", "Chat 3");

//        addMessage("Это тестовое сообщение 1", true);
//        addMessage("Это тестовое сообщение 2", false);
//        addMessage("Это тестовое сообщение 3", true);
//        addMessage("Это тестовое сообщение 4", true);
//        addMessage("Это тестовое сообщение 5", false);


//        sendMessage("You", "Это тестовое сообщение 1 Это тестовое сообщение 1Это тестовое сообщение 1 Это тестовое сообщение 1 Это тестовое сообщение 1Это тестовое сообщение 1 Это тестовое сообщение 1 Это тестовое сообщение 1");
//        sendMessage("Sol", "Это тестовое сообщение 2");
//        sendMessage("You", "Это тестовое сообщение 3");
//        sendMessage("You", "Это тестовое сообщение 4");
//        sendMessage("Sol", "Это тестовое сообщение 5 Это тестовое сообщение 5 Это тестовое сообщение 5 Это тестовое сообщение 5 Это тестовое сообщение 5 Это тестовое сообщение 5 Это тестовое сообщение 5");

        chatList.setOnMouseClicked(event -> {
            User selectedUser = chatList.getSelectionModel().getSelectedItem();
            if (selectedUser != null) {

                try {
                    openChat(selectedUser);
                } catch (SQLException e) {
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
            sendMessage(currentUser, messageField.getText());
        });

        buttonSearchUser.setOnAction(actionEvent -> {
            WindowsOpener.openWindow("userSearchForm.fxml");
        });
    }

    private void loadUser() throws SQLException {
        chatList.setItems(database.selectAllUser());
    }

    private void openChat(User selectedUser) throws SQLException {
        if (selectedUser.getPrivateKey() == null) {
            if (database.selectUserKeyAll(selectedUser.getUserId())) {
                WindowsOpener.openWindow("userFriendRequest.fxml");
                UserFriendRequestController userFriendRequestController = (UserFriendRequestController) ControllerRegistry.getController("UserFriendRequestController");
                if (userFriendRequestController != null) {
                    Platform.runLater(() -> userFriendRequestController.setUsername(selectedUser));
                }
            }
        } else {
            gostEncryptor = new GOSTEncryptor(selectedUser.getPrivateKey());
            database.selectMessageAll(currentUser.getUserId(), selectedUser.getUserId());
        }
    }

    public void reloadUser() throws SQLException {
        chatList.getItems().clear();
        chatList.setItems(database.selectAllUser());
    }

    private void sendMessage(User user, String text) {
        if (!text.isEmpty()) {
//            String sender = currentUser;
            addMessage(user.getUserName(), text);
        }
    }

    public void addMessage(String sender, String text) {

        HBox messageBox = new HBox();
        HBox messageContent = gethBox(sender, text);

        messageBox.getChildren().add(messageContent);

        if (sender.equals(currentUser.getUserName())) {
            messageBox.setAlignment(Pos.CENTER_RIGHT);
        } else {
            messageBox.setAlignment(Pos.CENTER_LEFT);
        }

        messageHistory.getChildren().add(messageBox);
        clientConnectionHandler.sendMessage(Message.fromJSON(text));
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
