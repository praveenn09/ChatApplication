package com.example.prime;

public class ChatListModel {

    private Users user;
    private String userId;
    private String lastMessage;
    private long timestamp;
    private int unreadCount;

    public ChatListModel() {
    }

    public ChatListModel(
            Users user,
            String userId,
            String lastMessage,
            long timestamp,
            int unreadCount) {

        this.user = user;
        this.userId = userId;
        this.lastMessage = lastMessage;
        this.timestamp = timestamp;
        this.unreadCount = unreadCount;
    }

    public Users getUser() {
        return user;
    }

    public void setUser(Users user) {
        this.user = user;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public int getUnreadCount() {
        return unreadCount;
    }

    public void setUnreadCount(int unreadCount) {
        this.unreadCount = unreadCount;
    }
}