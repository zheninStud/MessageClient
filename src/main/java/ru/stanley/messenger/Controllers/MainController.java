package ru.stanley.messenger.Controllers;


import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import ru.stanley.messenger.Handler.ClientConnectionHandler;
import ru.stanley.messenger.Messenger;


public class MainController {

    @FXML
    private ListView<String> chatList;

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

    private static final String currentUser = Messenger.getAccountName();
    private static final ClientConnectionHandler clientConnectionHandler = Messenger.getClientConnectionHandler();

    @FXML
    void initialize() {


        chatList.getItems().addAll("Chat 1", "Chat 2", "Chat 3");

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


        buttonSend.setOnAction(actionEvent -> {
            sendMessage(currentUser, messageField.getText());
        });
    }


    private void sendMessage(String sender, String text) {
        if (!text.isEmpty()) {
//            String sender = currentUser;
            addMessage(sender, text);
        }
    }

    public void addMessage(String sender, String text) {

        HBox messageBox = new HBox();
        HBox messageContent = gethBox(sender, text);

        messageBox.getChildren().add(messageContent);

        if (sender.equals(currentUser)) {
            messageBox.setAlignment(Pos.CENTER_RIGHT);
        } else {
            messageBox.setAlignment(Pos.CENTER_LEFT);
        }

        messageHistory.getChildren().add(messageBox);
        clientConnectionHandler.sendMessage(text);
    }

    private HBox gethBox(String sender, String text) {
        Text senderText = new Text(sender + ":");
        Text messageText = new Text(text);

        messageText.setWrappingWidth(180);

        HBox messageContent = new HBox(5, senderText, messageText);
        messageContent.setAlignment(Pos.TOP_LEFT);
        messageContent.setPadding(new Insets(5));
        messageContent.setMaxWidth(200);
        messageContent.setBackground(new Background(new BackgroundFill(sender.equals(currentUser) ? Color.LIGHTBLUE : Color.LIGHTGRAY, CornerRadii.EMPTY, Insets.EMPTY)));

        return messageContent;
    }

}
