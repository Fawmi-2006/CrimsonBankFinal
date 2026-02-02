package com.crimsonbank.utils;

/**
 * Simple session management utility to store the current logged-in user information.
 * This is a lightweight solution for tracking the current user across the application.
 */
public class SessionManager {

    private static SessionManager instance;
    private String currentUsername;
    private String currentUserFullName;
    private String currentUserRole;
    private String currentUserProfileImage;

    private SessionManager() {
    }

    /**
     * Gets the singleton instance of SessionManager.
     * @return the SessionManager instance
     */
    public static synchronized SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    /**
     * Sets the current logged-in user information.
     * @param username the username of the logged-in user
     * @param fullName the full name of the logged-in user
     * @param role the role of the logged-in user
     * @param profileImage the profile image path of the user (can be null)
     */
    public void setCurrentUser(String username, String fullName, String role, String profileImage) {
        this.currentUsername = username;
        this.currentUserFullName = fullName;
        this.currentUserRole = role;
        this.currentUserProfileImage = profileImage;
    }

    /**
     * Clears the current session (used on logout).
     */
    public void clearSession() {
        this.currentUsername = null;
        this.currentUserFullName = null;
        this.currentUserRole = null;
        this.currentUserProfileImage = null;
    }

    /**
     * Gets the current username.
     * @return the current username, or null if no user is logged in
     */
    public String getCurrentUsername() {
        return currentUsername;
    }

    /**
     * Gets the current user's full name.
     * @return the current user's full name, or null if no user is logged in
     */
    public String getCurrentUserFullName() {
        return currentUserFullName;
    }

    /**
     * Gets the current user's role.
     * @return the current user's role, or null if no user is logged in
     */
    public String getCurrentUserRole() {
        return currentUserRole;
    }

    /**
     * Gets the current user's profile image path.
     * @return the current user's profile image path, or null if not set
     */
    public String getCurrentUserProfileImage() {
        return currentUserProfileImage;
    }

    /**
     * Checks if a user is currently logged in.
     * @return true if a user is logged in, false otherwise
     */
    public boolean isUserLoggedIn() {
        return currentUsername != null;
    }

}
