package ru.stanley.messenger.Database;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import ru.stanley.messenger.Controllers.MainController;
import ru.stanley.messenger.Messenger;
import ru.stanley.messenger.Models.UserMessage;
import ru.stanley.messenger.Utils.ControllerRegistry;
import ru.stanley.messenger.Utils.DHUtil;
import ru.stanley.messenger.Utils.SQLQuery;

import javax.crypto.SecretKey;
import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.RowSetFactory;
import javax.sql.rowset.RowSetProvider;
import ru.stanley.messenger.Models.User;

import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.sql.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DatabaseConnection {

    private static final Messenger application = Messenger.getInstance();
    private String pathDb;
    private RowSetFactory factory;
    private Connection connection;
    private MainController mainController;

    public DatabaseConnection() throws SQLException {

        checkPath();
        //connection = DriverManager.getConnection("jdbc:sqlite:" + pathDb);
        connection = DriverManager.getConnection("jdbc:sqlite:mydatabase.db");
        System.out.println("Соединение с локальной базой данных установлено.");

        executeStatement(SQLQuery.CREATE_TABLE_USER);
        executeStatement(SQLQuery.CREATE_TABLE_CHAT);
        executeStatement(SQLQuery.CREATE_TABLE_MESSAGE);
        executeStatement(SQLQuery.CREATE_TABLE_CHAT_USER);
        executeStatement(SQLQuery.CREATE_TABLE_USERKEY);


//        executeStatement(SQLQuery.INSERT_USER, "Stanley000", "test@mail.ru", "89178060015");
//        executeStatement(SQLQuery.INSERT_USER, "Pkro", "test2@mail.ru", "89178070042");
//        executeStatement(SQLQuery.INSERT_USER, "Staftoj", "test3@mail.ru", "89178010043");
//
//        executeStatement(SQLQuery.INSERT_CHAT, "Test chat", "2024-04-10");
//        executeStatement(SQLQuery.INSERT_USER_ADD_CHAT, 1, 1);
//        executeStatement(SQLQuery.INSERT_USER_ADD_CHAT, 1, 2);
//
//        executeStatement(SQLQuery.INSERT_MESSAGE, 1, 1, "Hello, everyone!", "2024-04-10");
//        executeStatement(SQLQuery.INSERT_MESSAGE, 1, 2, "Hello!", "2024-04-10");
//        executeStatement(SQLQuery.INSERT_MESSAGE, 1, 1, "How are you?", "2024-04-10");
//        executeStatement(SQLQuery.INSERT_MESSAGE, 1, 2, "I fine!", "2024-04-10");
//
//
//        ResultSet var1 = executeResultStatement(SQLQuery.SELECT_CHAT, 1);
//
//        if (var1.next()) {
//            System.out.println("Result SELECT_CHAT: \nChatId: " + var1.getInt("chatId") + "\nChatName: " + var1.getString("chatName")
//            + "\nDate: " + var1.getString("creationDate"));
//
//            ResultSet var2 = executeResultStatement(SQLQuery.SELECT_CHAT_USERS, var1.getInt("chatId"));
//
//            while (var2.next()) {
//                System.out.println("Result SELECT_CHAT_USERS: \nUserId: " + var2.getInt("userId") + "\nUserName: " + var2.getString("userName")
//                + "\nEmail: " + var2.getString("email") + "\nPhone: " + var2.getString("phone"));
//            }
//
//            ResultSet var3 = executeResultStatement(SQLQuery.SELECT_CHAT_MESSAGES, var1.getInt("chatId"));
//
//            while (var3.next()) {
//                System.out.println("Result SELECT_CHAT_MESSAGES: \nMessageId: " + var3.getInt("messageId") + "\nChatId: " + var3.getInt("chatId")
//                        + "\nSenderId: " + var3.getInt("senderId") + "\nContent: " + var3.getString("content") + "\nTimestamp: " + var3.getString("timestamp"));
//            }
//        }


//
//        ResultSet var1 = executeResultStatement(SQLQuery.SELECT_ALL_USER);
//
//        while (var1.next()) {
//            System.out.println("Result: \nUserId: " + var1.getInt("userId") + "\nUserName: " + var1.getString("userName")
//            + "\nEmail: " + var1.getString("email") + "\nPhone: " + var1.getString("phone"));
//        }
    }

    public void disconnect() throws SQLException {
        connection.close();
    }

    private void checkPath() {
        URL resourceUrl = application.getClass().getResource("databases/mydatabase.db");

        if (resourceUrl != null) {
            pathDb = resourceUrl.getPath();
            System.out.println("Resource: " + pathDb);
        } else {
            System.out.println("Resource not found");
        }
    }

    public void executeStatement(SQLQuery sql, Object... params) {
        executeStatement(sql.toString(), false, params);
    }

    public ResultSet executeResultStatement(SQLQuery sql, Object... params) {
        return executeStatement(sql.toString(), true, params);
    }

    public void executeBatchStatement(SQLQuery sql, Object[] array, Object... params) {
        executeBatchStatement(sql.toString(), array, params);
    }

    public int executeUpdateStatement(SQLQuery sql, Object... params) {
        return executeUpdateStatement(sql.toString(), params);
    }

    private ResultSet executeStatement(String sql, boolean result, Object... params) {

        try (PreparedStatement statement = connection.prepareStatement(sql)) {

            setParameters(statement, null, params);

            if (result) {
                CachedRowSet results = createCachedRowSet();
                results.populate(statement.executeQuery());
                return results;
            }

            statement.execute();

        } catch (SQLException ex) {
            System.out.println("Произошла ошибка SQL:");
            System.out.println("SQL State: " + ex.getSQLState());
            System.out.println("Error Code: " + ex.getErrorCode());
            System.out.println("Message: " + ex.getMessage());
            ex.printStackTrace();
        } catch (NullPointerException ex) {
            System.out.println("Произошла ошибка NullPointer:");
            System.out.println("Message: " + ex.getMessage());
            ex.printStackTrace();
        }

        return null;
    }

    private int executeUpdateStatement(String sql, Object... params) {

        try (PreparedStatement statement = connection.prepareStatement(sql)) {

            setParameters(statement, null, params);
            return statement.executeUpdate();

        } catch (SQLException ex) {
            System.out.println("Произошла ошибка SQL:");
            System.out.println("SQL State: " + ex.getSQLState());
            System.out.println("Error Code: " + ex.getErrorCode());
            System.out.println("Message: " + ex.getMessage());
            ex.printStackTrace();
        } catch (NullPointerException ex) {
            System.out.println("Произошла ошибка NullPointer:");
            System.out.println("Message: " + ex.getMessage());
            ex.printStackTrace();
        }

        return 0;
    }

    private void executeBatchStatement(String sql, Object[] array, Object... params) {

        try (PreparedStatement statement = connection.prepareStatement(sql)) {

            for (Object object : array) {
                setParameters(statement, object, params);
                statement.addBatch();
            }

            statement.executeBatch();

        } catch (SQLException ex) {
            System.out.println("Произошла ошибка SQL:");
            System.out.println("SQL State: " + ex.getSQLState());
            System.out.println("Error Code: " + ex.getErrorCode());
            System.out.println("Message: " + ex.getMessage());
            ex.printStackTrace();
        } catch (NullPointerException ex) {
            System.out.println("Произошла ошибка NullPointer:");
            System.out.println("Message: " + ex.getMessage());
            ex.printStackTrace();
        }

    }

    private void setParameters(PreparedStatement statement, Object element, Object... params) throws SQLException {
        if (params != null) {
            if (element == null) {
                for (int i = 0; i < params.length; i++) {
                    statement.setObject(i + 1, params[i]);
                }
            } else {
                statement.setObject(1, element);

                for (int i = 0; i < params.length; i++) {
                    statement.setObject(i + 2, params[i]);
                }

            }
        }
    }

    private CachedRowSet createCachedRowSet() throws SQLException {
        if (factory == null) {
            factory = RowSetProvider.newFactory();
        }
        return factory.createCachedRowSet();
    }

    public boolean insertUser(User user) {
        int result = executeUpdateStatement(SQLQuery.INSERT_USER, user.getUserId(), user.getUserName(), user.getEmail(), user.getPhone());

        return result > 0;
    }

    public boolean insertUserKey(String userId, String publicKey, String privateKey) {
        int result = executeUpdateStatement(SQLQuery.INSERT_USERKEY, userId, privateKey, publicKey);

        return result > 0;
    }

    public boolean insertUserRequest(User user) {
        int result = executeUpdateStatement(SQLQuery.INSERT_USER, user.getUserId(), user.getUserName(), user.getEmail(), user.getPhone());

        return result > 0;
    }

    public boolean insertUserKeyRequest(String userId, String publicKey) {
        int result = executeUpdateStatement(SQLQuery.INSERT_USERKEY_REQUEST, userId, publicKey, 1);

        return result > 0;

    }

    public boolean updateUserKey(String userId) {
        int result = executeUpdateStatement(SQLQuery.INSERT_USERKEY_IS_CLIENT_TAKEN, 1, userId);

        return result > 0;
    }

    public boolean updateUserKeySuccess(String userId) {
        int result = executeUpdateStatement(SQLQuery.INSERT_USERKEY_IS_SUCCESS_SERVER, 1, userId);

        return result > 0;
    }

    public boolean updateUserPrivateKey(String userId, String privateKey) {
        int result = executeUpdateStatement(SQLQuery.UPDATE_USER_PRIVATE_KEY, privateKey, userId);

        return result > 0;
    }

    public ObservableList<User> selectAllUser() throws SQLException {
        ObservableList<User> userList = FXCollections.observableArrayList();
        ResultSet resultSet = executeResultStatement(SQLQuery.SELECT_ALL_USER);

        if (resultSet.next()) {
            String userId = String.valueOf(resultSet.getString("userId"));
            String userName = resultSet.getString("userName");
            String email = resultSet.getString("email");
            String phone = resultSet.getString("phone");
            String privateKey = resultSet.getString("privateKey");

            User user = new User(userId, userName, email, phone);
            user.setPrivateKey(privateKey);
            userList.add(user);
        }

        return userList;
    }

    public boolean deleteUserKey(String userId) {
        int result = executeUpdateStatement(SQLQuery.DELETE_USER_KEY, userId);

        return result > 0;
    }

    public PublicKey selectUserKey(String userReguestTaken) throws SQLException, NoSuchAlgorithmException, InvalidKeySpecException, NoSuchProviderException {
        ResultSet resultSet = executeResultStatement(SQLQuery.SELECT_USERKEY, userReguestTaken);

        if (resultSet.next()) {
            String publicKeyBlob = resultSet.getString("publicKey");
            return DHUtil.convertBytesToPublicKey(publicKeyBlob);
        }

        return null;
    }

    public boolean selectUserKeyAll(String userId) throws SQLException {
        ResultSet resultSet = executeResultStatement(SQLQuery.SELECT_USERKEY, userId);

        if (resultSet.next()) {
            int isRequest = resultSet.getInt("is_request");
            int isClientTaken = resultSet.getInt("is_client_taken");
            int isSuccessServer = resultSet.getInt("is_success_server");

            mainController = (MainController) ControllerRegistry.getController("MainController");
            if (mainController != null) {
                if (isRequest == 1) {
                    Platform.runLater(() -> mainController.showSuccessNotification("The request has been sent to the user"));
                    return false;
                }

                if (isClientTaken == 1) {
                    Platform.runLater(() -> mainController.showSuccessNotification("The request was received by the user"));
                    return false;
                }

                if (isSuccessServer == 1) {
                    Platform.runLater(() -> mainController.showSuccessNotification("The request has been confirmed by the user"));
                    return false;
                }

                return true;
            }

            return false;
        }

        return false;
    }

    public boolean selectSecretKey(String userId, PublicKey publicKeyOther) throws SQLException, NoSuchAlgorithmException, InvalidKeySpecException, NoSuchProviderException, InvalidKeyException {
        ResultSet resultSet = executeResultStatement(SQLQuery.SELECT_USERKEY, userId);

        if (resultSet.next()) {
            String privateKeyBlob = resultSet.getString("privateKey");
            SecretKey secretKey = DHUtil.generateSharedSecret(DHUtil.convertBytesToPrivateKey(privateKeyBlob), publicKeyOther);

            updateUserPrivateKey(userId, DHUtil.keyToString(secretKey));
            deleteUserKey(userId);

            return true;
        }

        return false;
    }

    public List<UserMessage> selectMessageAll(String userIdSender, String userIdReceiver) throws SQLException {
        ResultSet resultSet = executeResultStatement(SQLQuery.SELECT_MESSAGE_ALL, userIdSender, userIdReceiver, userIdReceiver, userIdSender);

        if (resultSet.next()) {
            String senderId  = resultSet.getString("senderId");
            String receiverId = resultSet.getString("receiverId");
            String content = resultSet.getString("content");


        }

        return new ArrayList<>();
    }
}
