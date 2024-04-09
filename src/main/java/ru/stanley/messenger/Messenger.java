package ru.stanley.messenger;

import javafx.application.Application;
import javafx.stage.Stage;
import ru.stanley.messenger.Database.DatabaseConnection;
import ru.stanley.messenger.Handler.ClientConnectionHandler;
import ru.stanley.messenger.Utils.FontLoader;
import ru.stanley.messenger.Utils.WindowsOpener;

import java.sql.SQLException;

public class Messenger extends Application {

    private static String accountName;
    private static Messenger instance;
    private static final ClientConnectionHandler clientConnectionHandler = new ClientConnectionHandler();
    private static DatabaseConnection databaseConnection;

    @Override
    public void start(Stage stage) {
        instance = this;

        FontLoader.loadFonts();
        clientConnectionHandler.connect("localhost", 12345);
        WindowsOpener.openWindow("auth.fxml");

    }

    @Override
    public void stop() throws Exception {
        clientConnectionHandler.disconnect();
        super.stop();
    }

    public void openDatabaseConnection() {

        try {
            databaseConnection = new DatabaseConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public static void main(String[] args) {
        launch();
    }
    public static Messenger getInstance() {
        return instance;
    }
    public static String getAccountName() { return accountName; }
    public static void setAccountName(String name) { accountName = name; }
    public static ClientConnectionHandler getClientConnectionHandler() {
        return clientConnectionHandler;
    }
    public static DatabaseConnection getDatabaseConnection() {
        return databaseConnection;
    }
}