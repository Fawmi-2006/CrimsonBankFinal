package com.crimsonbank.controllers;

import com.crimsonbank.database.StaffDAO;
import com.crimsonbank.exceptions.DatabaseException;
import com.crimsonbank.models.StaffMember;
import com.crimsonbank.utils.InputValidator;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class SignupController {

    @FXML
    private TextField fullNameField;

    @FXML
    private TextField emailField;

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private ComboBox<String> roleCombo;

    @FXML
    private Button signupButton;

    @FXML
    private Label messageLabel;

    @FXML
    private Hyperlink backLink;

    @FXML
    public void initialize() {
        roleCombo.setItems(FXCollections.observableArrayList("ADMIN", "SUPERVISOR", "STAFF", "MANAGER", "TELLER", "ACCOUNTANT", "COMPLIANCE_OFFICER"));
        roleCombo.setValue("STAFF");

        signupButton.setOnAction(event -> handleSignup());
        backLink.setOnAction(event -> navigateToLogin());
    }

    private void handleSignup() {
        String fullName = fullNameField.getText().trim();
        String email = emailField.getText().trim();
        String username = usernameField.getText().trim();
        String password = passwordField.getText();
        String role = roleCombo.getValue();

        if (fullName.isEmpty() || email.isEmpty() || username.isEmpty() || password.isEmpty()) {
            showError("All fields are required");
            return;
        }

        if (!InputValidator.isValidEmail(email)) {
            showError("Invalid email format");
            return;
        }

        if (!InputValidator.isValidPassword(password)) {
            showError("Password must be at least 6 characters");
            return;
        }

        try {
            StaffDAO staffDAO = new StaffDAO();
            StaffMember staff = new StaffMember(email, username, password, fullName, role);

            if (staffDAO.createStaff(staff)) {
                showSuccess("Account created successfully!");
                navigateToLogin();
            } else {
                showError("Failed to create account");
            }

        } catch (DatabaseException e) {
            showError("Database error: " + e.getMessage());
        }
    }

    private void navigateToLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/crimsonbank/views/login.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) backLink.getScene().getWindow();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/com/crimsonbank/styles.css").toExternalForm());

            stage.setScene(scene);
            stage.setTitle("CrimsonBank - Login");
            stage.show();

        } catch (Exception e) {
            showError("Navigation error: " + e.getMessage());
        }
    }

    private void showError(String message) {
        messageLabel.setText(message);
        messageLabel.setStyle("-fx-text-fill: #c0392b;");

        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Signup Error");
        alert.setHeaderText("Signup Failed");
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showSuccess(String message) {
        messageLabel.setText(message);
        messageLabel.setStyle("-fx-text-fill: #27ae60;");

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText("Account Created");
        alert.setContentText(message);
        alert.showAndWait();
    }

}
