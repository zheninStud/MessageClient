package ru.stanley.messenger;

import javafx.application.Application;
import javafx.stage.Stage;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import ru.stanley.messenger.Database.DatabaseConnection;
import ru.stanley.messenger.Handler.ClientConnectionHandler;
import ru.stanley.messenger.Models.User;
import ru.stanley.messenger.Utils.FontLoader;
import ru.stanley.messenger.Utils.WindowsOpener;

import java.security.Security;
import java.sql.SQLException;

public class Messenger extends Application {

    private static User accountUser;
    private static Messenger instance;
    private static final ClientConnectionHandler clientConnectionHandler = new ClientConnectionHandler();
    private static DatabaseConnection databaseConnection;

    @Override
    public void start(Stage stage) {
        Security.addProvider(new BouncyCastleProvider());
        instance = this;

        FontLoader.loadFonts();
        clientConnectionHandler.connect("localhost", 12345);
        WindowsOpener.openWindow("auth.fxml");

    }

    @Override
    public void stop() throws Exception {
        clientConnectionHandler.disconnect();
        databaseConnection.disconnect();
        super.stop();
    }

    public static void openDatabaseConnection() {

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
    public static User getAccountUser() { return accountUser; }
    public static void setAccountUser(User user) { accountUser = user; }
    public static ClientConnectionHandler getClientConnectionHandler() {
        return clientConnectionHandler;
    }
    public static DatabaseConnection getDatabaseConnection() {
        return databaseConnection;
    }
}