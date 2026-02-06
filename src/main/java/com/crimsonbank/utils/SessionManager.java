package com.crimsonbank.utils;

public class SessionManager {

    private static SessionManager instance;
    private String currentUsername;
    private String currentUserFullName;
    private String currentUserRole;
    private String currentUserProfileImage;

    private SessionManager() {
    }

    public static synchronized SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    public void setCurrentUser(String username, String fullName, String role, String profileImage) {
        this.currentUsername = username;
        this.currentUserFullName = fullName;
        this.currentUserRole = role;
        this.currentUserProfileImage = profileImage;
    }

    public void clearSession() {
        this.currentUsername = null;
        this.currentUserFullName = null;
        this.currentUserRole = null;
        this.currentUserProfileImage = null;
    }

    public String getCurrentUsername() {
        return currentUsername;
    }

    public String getCurrentUserFullName() {
        return currentUserFullName;
    }

    public String getCurrentUserRole() {
        return currentUserRole;
    }

    public String getCurrentUserProfileImage() {
        return currentUserProfileImage;
    }

    public boolean isUserLoggedIn() {
        return currentUsername != null;
    }

}
