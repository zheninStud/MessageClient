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
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;

public class DatabaseConnection {

    private static final Messenger application = Messenger.getInstance();
    private String pathDb;
    private RowSetFactory factory;
    private Connection connection;
    private MainController mainController;

    public DatabaseConnection() throws SQLException {

        connection = DriverManager.getConnection("jdbc:sqlite:mydatabase.db");
        System.out.println("Соединение с локальной базой данных установлено.");

        executeStatement(SQLQuery.CREATE_TABLE_USER);
        executeStatement(SQLQuery.CREATE_TABLE_MESSAGE);
        executeStatement(SQLQuery.CREATE_TABLE_USERKEY);

    }

    public void disconnect() throws SQLException {
        connection.close();
    }

    public void executeStatement(SQLQuery sql, Object... params) {
        executeStatement(sql.toString(), false, params);
    }

    public ResultSet executeResultStatement(SQLQuery sql, Object... params) {
        return executeStatement(sql.toString(), true, params);
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

    public boolean insertUser(User user, Integer is_main) throws SQLException {
        if (selectUserUserId(user.getUserId()) == null) {
            int result = executeUpdateStatement(SQLQuery.INSERT_USER, user.getUserId(), user.getUserName(), user.getEmail(), user.getPhone(), is_main);

            return result > 0;
        } else {
            return true;
        }
    }

    public boolean insertUserKey(String userId, String publicKey, String privateKey) {
        int result = executeUpdateStatement(SQLQuery.INSERT_USERKEY, userId, privateKey, publicKey);

        return result > 0;
    }

    public boolean insertUserRequest(User user) {
        int result = executeUpdateStatement(SQLQuery.INSERT_USER, user.getUserId(), user.getUserName(), user.getEmail(), user.getPhone(), 0);

        return result > 0;
    }

    public boolean insertUserKeyRequest(String userId, String publicKey) {
        int result = executeUpdateStatement(SQLQuery.INSERT_USERKEY_REQUEST, userId, publicKey, 0);

        return result > 0;

    }

    public boolean updateUserKeyRequest(String userId) {
        int result = executeUpdateStatement(SQLQuery.UPDATE_USERKEY_REQUEST, 1, userId);

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

        while (resultSet.next()) {
            String userId = resultSet.getString("userId");
            String userName = resultSet.getString("userName");
            String email = resultSet.getString("email");
            String phone = resultSet.getString("phone");
            String privateKey = resultSet.getString("privateKey");
            int is_main = resultSet.getInt("is_main");

            if (is_main == 0) {
                User user = new User(userId, userName, email, phone);
                user.setPrivateKey(privateKey);
                userList.add(user);
            }
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

            mainController = (MainController) ControllerRegistry.getController("MainController");
            if (mainController != null) {
                Platform.runLater(() -> mainController.setPrivateKey(DHUtil.keyToString(secretKey)) );
            }

            return true;
        }

        return false;
    }

    public User selectUserUserId(String userId) throws SQLException {
        ResultSet resultSet = executeResultStatement(SQLQuery.SELECT_USER, userId);

        if (resultSet.next()) {
            String userIdResult = resultSet.getString("userId");
            String userName = resultSet.getString("userName");
            String email = resultSet.getString("email");
            String phone = resultSet.getString("phone");
            String privateKey = resultSet.getString("privateKey");

            User user = new User(userIdResult, userName, email, phone);
            user.setPrivateKey(privateKey);
            return user;
        }

        return null;

    }

    public List<UserMessage> selectMessageAll(String userIdSender, String userIdReceiver) throws SQLException {
        List<UserMessage> userMessageList = new ArrayList<>();

        ResultSet resultSet = executeResultStatement(SQLQuery.SELECT_MESSAGE_ALL, userIdSender, userIdReceiver, userIdReceiver, userIdSender);

        while (resultSet.next()) {
            String senderId  = resultSet.getString("senderId");
            String receiverId = resultSet.getString("receiverId");
            String content = resultSet.getString("content");

            User userSender = selectUserUserId(senderId);
            User userReceiver = selectUserUserId(receiverId);

            userMessageList.add(new UserMessage(userSender, userReceiver, content));
        }

        return userMessageList;
    }

    public boolean insertMessage(String userSender, String userReceiver, String content) {
        Date currentDate = new Date();
        Timestamp timestamp = new Timestamp(currentDate.getTime());

        int result = executeUpdateStatement(SQLQuery.INSERT_MESSAGE, userSender, userReceiver, content, timestamp);

        return result > 0;
    }

    public boolean deleteUser(String userId) {
        int result = executeUpdateStatement(SQLQuery.DELETE_USER, userId);

        return result > 0;
    }
}
