package ru.stanley.messenger.Models;

public class UserMessage {
    private User sender;
    private User receiver;
    private String content;

    public UserMessage(User sender, User receiver, String content) {
        this.sender = sender;
        this.receiver = receiver;
        this.content = content;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public User getReceiver() { return receiver; }

    public void setReceiver(User receiver) { this.receiver = receiver; }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
