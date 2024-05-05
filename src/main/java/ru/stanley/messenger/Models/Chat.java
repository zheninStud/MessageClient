package ru.stanley.messenger.Models;

import java.util.Date;
import java.util.List;

public class Chat {
    private String chatId;
    private String chatName;
    private List<User> participants;
    private List<UserMessage> messagesHistory;
    private Date creationDate;

    public Chat(String chatId, String chatName, List<User> participants, List<UserMessage> messagesHistory, Date creationDate) {
        this.chatId = chatId;
        this.chatName = chatName;
        this.participants = participants;
        this.messagesHistory = messagesHistory;
        this.creationDate = creationDate;
    }

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public String getChatName() {
        return chatName;
    }

    public void setChatName(String chatName) {
        this.chatName = chatName;
    }

    public List<User> getParticipants() {
        return participants;
    }

    public void setParticipants(List<User> participants) {
        this.participants = participants;
    }

    public List<UserMessage> getMessagesHistory() {
        return messagesHistory;
    }

    public void setMessagesHistory(List<UserMessage> messagesHistory) {
        this.messagesHistory = messagesHistory;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }
}
