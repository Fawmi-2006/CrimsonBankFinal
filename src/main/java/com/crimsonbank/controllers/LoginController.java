package com.crimsonbank.controllers;

import com.crimsonbank.database.StaffDAO;
import com.crimsonbank.database.AuditLogDAO;
import com.crimsonbank.exceptions.DatabaseException;
import com.crimsonbank.models.AuditLog;
import com.crimsonbank.models.StaffMember;
import com.crimsonbank.utils.SessionManager;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.File;
import java.io.InputStream;

public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button loginButton;

    @FXML
    private Label errorLabel;

    @FXML
    private Hyperlink signupLink;

    @FXML
    private ImageView logoImageView;

    @FXML
    public void initialize() {
        loginButton.setOnAction(event -> handleLogin());
        signupLink.setOnAction(event -> navigateToSignup());

        try {
            InputStream is = getClass().getResourceAsStream("/com/crimsonbank/CimsonBank_Logo.png");
            Image logo = null;
            if (is != null) {
                logo = new Image(is);
            } else {
                File f = new File("C:\\Users\\SINGER\\Desktop\\CrimsonBankFinal\\CimsonBank_Logo.png");
                if (f.exists()) {
                    logo = new Image(f.toURI().toString());
                }
            }

            if (logo != null && logoImageView != null) {
                logoImageView.setImage(logo);
            }
        } catch (Exception e) {
            System.err.println("Unable to load logo: " + e.getMessage());
        }
    }

    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            showError("Please enter both username and password");
            return;
        }

        try {
            StaffDAO staffDAO = new StaffDAO();

            if (staffDAO.authenticate(username, password)) {
                StaffMember staff = staffDAO.getByUsername(username);
                if (staff != null) {
                    SessionManager.getInstance().setCurrentUser(
                        staff.getUsername(),
                        staff.getFullName(),
                        staff.getRole(),
                        staff.getProfileImage()
                    );
                }

                logLoginAttempt(username, true);
                navigateToDashboard();
            } else {
                logLoginAttempt(username, false);
                showError("Invalid username or password");
            }

        } catch (DatabaseException e) {
            showError("Database error: " + e.getMessage());
        }
    }

    private void logLoginAttempt(String username, boolean success) {
        try {
            AuditLogDAO auditLogDAO = new AuditLogDAO();
            AuditLog log = new AuditLog(
                "LOGIN_ATTEMPT",
                "Login " + (success ? "successful" : "failed") + " for user: " + username,
                null
            );
            auditLogDAO.createLog(log);

        } catch (DatabaseException e) {
            System.err.println("Failed to log login attempt: " + e.getMessage());
        }
    }

    private void navigateToDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/com/crimsonbank/views/dashboard.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) loginButton.getScene().getWindow();
            Scene scene = new Scene(root);

            java.net.URL styleUrl = getClass().getResource("/com/crimsonbank/styles.css");
            if (styleUrl != null) {
                scene.getStylesheets().add(styleUrl.toExternalForm());
            }

            stage.setScene(scene);
            stage.setTitle("CrimsonBank - Dashboard");
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
            showError("Navigation error: " + e.getMessage());
        }
    }

    private void navigateToSignup() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/com/crimsonbank/views/signup.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) signupLink.getScene().getWindow();
            Scene scene = new Scene(root);

            java.net.URL styleUrl = getClass().getResource("/com/crimsonbank/styles.css");
            if (styleUrl != null) {
                scene.getStylesheets().add(styleUrl.toExternalForm());
            }

            stage.setScene(scene);
            stage.setTitle("CrimsonBank - Sign Up");
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
            showError("Navigation error: " + e.getMessage());
        }
    }

    private void showError(String message) {
        errorLabel.setText(message);

        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Login Error");
        alert.setHeaderText("Login Failed");
        alert.setContentText(message);
        alert.showAndWait();
    }

}
