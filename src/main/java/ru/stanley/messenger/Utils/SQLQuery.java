package ru.stanley.messenger.Utils;

public enum SQLQuery {

    CREATE_TABLE_USER(
            "CREATE TABLE IF NOT EXISTS User (" +
                    "userId INT PRIMARY KEY," +
                    "userName TEXT," +
                    "email TEXT," +
                    "phone TEXT" +
                    ")"
    ),

    CREATE_TABLE_CHAT(
            "CREATE TABLE IF NOT EXISTS Chat (" +
                    "chatId INT PRIMARY KEY," +
                    "chatName TEXT," +
                    "creationDate DATE" +
                    ")"
    ),

    CREATE_TABLE_MESSAGE(
            "CREATE TABLE IF NOT EXISTS Message (" +
                    "messageId INT PRIMARY KEY," +
                    "senderId INT," +
                    "content TEXT," +
                    "timestamp DATE," +
                    "FOREIGN KEY (senderId) REFERENCES User(userId)" +
                    ")"
    ),

    CREATE_TABLE_CHAT_USER(
            "CREATE TABLE IF NOT EXISTS ChatUser (" +
                    "chatId INT," +
                    "userId INT," +
                    "FOREIGN KEY (chatId) REFERENCES Chat(chatId)," +
                    "FOREIGN KEY (userId) REFERENCES User(userId)," +
                    "PRIMARY KEY (chatId, userId)" +
                    ")"
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
