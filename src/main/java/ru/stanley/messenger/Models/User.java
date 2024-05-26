package ru.stanley.messenger.Models;

import ru.stanley.messenger.Utils.DHUtil;

import javax.crypto.SecretKey;

public class User {
    private String userId;
    private String userName;
    private String email;
    private String phone;
    private String privateKey;

    public User(String userId, String userName, String email, String phone) {
        this.userId = userId;
        this.userName = userName;
        this.email = email;
        this.phone = phone;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) { this.userId = userId;}

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public SecretKey getPrivateKey() { return DHUtil.convertStringToSecretKey(privateKey); }

    public boolean checkPrivateKey() { return privateKey == null; }

    public void setPrivateKey(String privateKey) { this.privateKey = privateKey; }

    @Override
    public String toString() { return userName; }
}
