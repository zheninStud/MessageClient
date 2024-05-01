package ru.stanley.messenger.Utils;

public enum SQLQuery {

    CREATE_TABLE_USER(
            "CREATE TABLE IF NOT EXISTS User (" +
                    "userId INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "userName TEXT," +
                    "email TEXT," +
                    "phone TEXT" +
                    ")"
    ),

    CREATE_TABLE_CHAT(
            "CREATE TABLE IF NOT EXISTS Chat (" +
                    "chatId INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "chatName TEXT," +
                    "creationDate DATE" +
                    ")"
    ),

    CREATE_TABLE_MESSAGE(
            "CREATE TABLE IF NOT EXISTS Message (" +
                    "messageId INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "chatId INT," +
                    "senderId INT," +
                    "content TEXT," +
                    "timestamp DATE," +
                    "FOREIGN KEY (chatId) REFERENCES Chat(chatId)," +
                    "FOREIGN KEY (senderId) REFERENCES User(userId)" +
                    ")"
    ),

    CREATE_TABLE_CHAT_USER(
            "CREATE TABLE IF NOT EXISTS ChatUser (" +
                    "chatId INTEGER," +
                    "userId INTEGER," +
                    "FOREIGN KEY (chatId) REFERENCES Chat(chatId)," +
                    "FOREIGN KEY (userId) REFERENCES User(userId)," +
                    "PRIMARY KEY (chatId, userId)" +
                    ")"
    ),

    INSERT_USER(
            "INSERT INTO 'User' (userName, email, phone) VALUES (?, ?, ?)"
    ),

    INSERT_CHAT(
            "INSERT INTO 'Chat' (chatName, creationDate) VALUES (?, ?)"
    ),

    INSERT_MESSAGE(
            "INSERT INTO 'Message' (chatId, senderId, content, timestamp) VALUES (?, ?, ?, ?)"
    ),

    INSERT_USER_ADD_CHAT(
            "INSERT INTO 'ChatUser' (chatId, userId) VALUES (?, ?)"
    ),

    SELECT_ALL_USER(
            "SELECT * FROM User"
    ),

    SELECT_USER(
            "SELECT * FROM User WHERE userId = ?"
    ),

    SELECT_CHAT(
            "SELECT * FROM Chat WHERE chatId = ?"
    ),

    SELECT_CHAT_USERS(
            "SELECT User.* " +
                    "FROM User " +
                    "JOIN ChatUser ON User.userId = ChatUser.userId " +
                    "WHERE ChatUser.chatId = ?"
    ),

    SELECT_CHAT_MESSAGES(
            "SELECT Message.* " +
                    "FROM Message " +
                    "JOIN Chat ON Message.chatId = Chat.chatId " +
                    "WHERE Chat.chatId = ?"
    ),

    DELETE_CHAT_USER(
            "DELETE FROM ChatUser WHERE chatId = ? AND userId = ?"
    ),

    DELETE_CHAT_USERS(
            "DELETE FROM ChatUser WHERE chatId = ?"
    ),

    DELETE_CHAT(
            "DELETE FROM Chat WHERE chatId = ?"
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
