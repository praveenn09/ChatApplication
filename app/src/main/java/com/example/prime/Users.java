package com.example.prime;

public class Users {

    private String username;
    private String email;
    private String profileImage;
    private String userId;

    private boolean online;
    private long lastSeen;


    // =========================================
    // REQUIRED BY FIREBASE
    // =========================================

    public Users() {
    }


    // =========================================
    // CONSTRUCTOR
    // =========================================

    public Users(
            String username,
            String email,
            String profileImage,
            String userId) {

        this.username = username;
        this.email = email;
        this.profileImage = profileImage;
        this.userId = userId;

        this.online = false;
        this.lastSeen = 0;
    }


    // =========================================
    // USERNAME
    // =========================================

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }


    // =========================================
    // EMAIL
    // =========================================

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    // =========================================
    // PROFILE IMAGE
    // =========================================

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }


    // =========================================
    // USER ID
    // =========================================

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }


    // =========================================
    // ONLINE STATUS
    // =========================================

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }


    // =========================================
    // LAST SEEN
    // =========================================

    public long getLastSeen() {
        return lastSeen;
    }

    public void setLastSeen(long lastSeen) {
        this.lastSeen = lastSeen;
    }
}