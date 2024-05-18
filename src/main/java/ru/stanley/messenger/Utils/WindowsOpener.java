package ru.stanley.messenger.Utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import ru.stanley.messenger.Messenger;

import java.io.IOException;

public class WindowsOpener {

    private static final Messenger application = Messenger.getInstance();

    public static void openWindow(String fxmlFilePath) {
        Stage secondStage = new Stage();
        FXMLLoader loader = new FXMLLoader(application.getClass().getResource(fxmlFilePath));

        try {
            Parent root = loader.load();
            secondStage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }

        Object controller = loader.getController();
        registerController(controller);

        secondStage.setTitle("CryptoMessenger");
        secondStage.setResizable(false);
        secondStage.show();
    }

    public static void openAndCloseWindows(String fxmlFilePath, Stage stage) {
        Stage secondStage = new Stage();
        FXMLLoader loader = new FXMLLoader(application.getClass().getResource(fxmlFilePath));

        try {
            Parent root = loader.load();
            secondStage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }

        Object controller = loader.getController();
        registerController(controller);

        stage.close();

        secondStage.setTitle("CryptoMessenger");
        secondStage.setResizable(false);
        secondStage.show();
    }

    public static void closeWindow(Stage stage) {
        stage.close();
    }

    private static void registerController(Object controller) {
        if (controller != null) {
            ControllerRegistry.registerController(controller.getClass().getSimpleName(), controller);
        }
    }

}
