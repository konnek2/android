package com.quickblox.sample.chat.models;

/**
 * Created by Lenovo on 30-06-2017.
 */

public class MessageStatusModel {
    private String userId;
    private String recipientId;
    private String messageId;
    private String qbUserName;
    private String qbUserId;
    private String qbUserLogin;
    private String qbUserPassword;
    private String getQbUserTag;
    private int isUpdateServer;
    private int isDelivered;
    private int isRead;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getRecipientId() {
        return recipientId;
    }

    public void setRecipientId(String recipientId) {
        this.recipientId = recipientId;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public int getIsUpdateServer() {
        return isUpdateServer;
    }

    public void setIsUpdateServer(int isUpdateServer) {
        this.isUpdateServer = isUpdateServer;
    }

    public int getIsDelivered() {
        return isDelivered;
    }

    public void setIsDelivered(int isDelivered) {
        this.isDelivered = isDelivered;
    }

    public int getIsRead() {
        return isRead;
    }

    public void setIsRead(int isRead) {
        this.isRead = isRead;
    }

    public String getQbUserName() {
        return qbUserName;
    }

    public void setQbUserName(String qbUserName) {
        this.qbUserName = qbUserName;
    }

    public String getQbUserId() {
        return qbUserId;
    }

    public void setQbUserId(String qbUserId) {
        this.qbUserId = qbUserId;
    }

    public String getQbUserLogin() {
        return qbUserLogin;
    }

    public void setQbUserLogin(String qbUserLogin) {
        this.qbUserLogin = qbUserLogin;
    }

    public String getQbUserPassword() {
        return qbUserPassword;
    }

    public void setQbUserPassword(String qbUserPassword) {
        this.qbUserPassword = qbUserPassword;
    }

    public String getGetQbUserTag() {
        return getQbUserTag;
    }

    public void setGetQbUserTag(String getQbUserTag) {
        this.getQbUserTag = getQbUserTag;
    }
}
