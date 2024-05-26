package ru.stanley.messenger.Utils;

public enum SQLQuery {

    CREATE_TABLE_USER(
            "CREATE TABLE IF NOT EXISTS User (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "userId TEXT," +
                    "userName TEXT," +
                    "email TEXT," +
                    "phone TEXT," +
                    "privateKey TEXT," +
                    "is_main INT" +
                    ")"
    ),

    CREATE_TABLE_MESSAGE(
            "CREATE TABLE IF NOT EXISTS Message (" +
                    "messageId INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "senderId TEXT," +
                    "receiverId TEXT," +
                    "content TEXT," +
                    "timestamp DATE," +
                    "FOREIGN KEY (senderId) REFERENCES User(id)" +
                    ")"
    ),

    CREATE_TABLE_USERKEY(
            "CREATE TABLE IF NOT EXISTS UserKeys (" +
                    "id INTEGER PRIMARY KEY," +
                    "userId TEXT," +
                    "privateKey TEXT," +
                    "publicKey TEXT," +
                    "is_request INT," +
                    "is_client_taken INT," +
                    "is_success_server INT," +
                    "FOREIGN KEY (UserId) REFERENCES User(id)" +
                    ")"
    ),

    DELETE_USER_KEY(
            "DELETE FROM UserKeys WHERE userId = ?"
    ),

    DELETE_USER(
            "DELETE FROM User WHERE userId = ?"
    ),

    INSERT_USER(
            "INSERT INTO 'User' (userId, userName, email, phone, is_main) VALUES (?, ?, ?, ?, ?)"
    ),

    UPDATE_USER_PRIVATE_KEY(
            "UPDATE User SET privateKey = ? WHERE userId = ?"
    ),

    INSERT_MESSAGE(
            "INSERT INTO 'Message' (senderId, receiverId, content, timestamp) VALUES (?, ?, ?, ?)"
    ),

    INSERT_USERKEY(
            "INSERT INTO 'UserKeys' (userId, privateKey, publicKey) VALUES (?, ?, ?)"
    ),

    UPDATE_USERKEY_REQUEST(
            "UPDATE UserKeys SET is_request = ? WHERE userId = ?"
    ),

    INSERT_USERKEY_REQUEST(
            "INSERT INTO 'UserKeys' (userId, publicKey, is_request) VALUES (?, ?, ?)"
    ),

    INSERT_USERKEY_IS_CLIENT_TAKEN(
            "UPDATE UserKeys SET is_client_taken = ? WHERE userId = ?"
    ),

    INSERT_USERKEY_IS_SUCCESS_SERVER(
            "UPDATE UserKeys SET is_success_server = ? WHERE userId = ?"
    ),

    SELECT_USERKEY(
            "SELECT * FROM UserKeys WHERE userId = ?"
    ),

    SELECT_ALL_USER(
            "SELECT * FROM User"
    ),

    SELECT_USER(
            "SELECT * FROM User WHERE userId = ?"
    ),

    SELECT_MESSAGE_ALL(
            "SELECT * FROM Message WHERE (senderId = ? AND receiverId = ?) OR (senderId = ? AND receiverId = ?)"
    );

    private final String mysql;

    SQLQuery(String mysql) {
        this.mysql = mysql;
    }

    @Override
    public String toString() {
        return mysql;
    }

}
