package ru.stanley.messenger.Models;

import java.util.Date;

public class UserMessage {
    private String messageId;
    private User sender;
    private String content;
    private Date timestamp;

    public UserMessage(String messageId, User sender, String content, Date timestamp) {
        this.messageId = messageId;
        this.sender = sender;
        this.content = content;
        this.timestamp = timestamp;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}
