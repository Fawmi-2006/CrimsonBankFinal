package com.crimsonbank.controllers;

import com.crimsonbank.database.StaffDAO;
import com.crimsonbank.exceptions.DatabaseException;
import com.crimsonbank.models.StaffMember;
import com.crimsonbank.utils.AvatarUtil;
import com.crimsonbank.utils.SessionManager;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

/**
 * Controller for user profile settings dialog.
 * Allows users to upload and manage their profile picture.
 */
public class ProfileSettingsController {

    @FXML
    private VBox avatarContainer;

    @FXML
    private Button uploadPhotoButton;

    @FXML
    private Button removePhotoButton;

    @FXML
    private Button closeButton;

    @FXML
    private Label photoStatusLabel;

    @FXML
    private Label nameLabel;

    @FXML
    private Label roleLabel;

    private String profileImagePath;
    private String username;
    private Stage stage;

    @FXML
    public void initialize() {
        uploadPhotoButton.setOnAction(event -> handleUploadPhoto());
        removePhotoButton.setOnAction(event -> handleRemovePhoto());
        closeButton.setOnAction(event -> handleClose());

        loadUserInfo();
        displayAvatar();
    }

    private void loadUserInfo() {
        SessionManager session = SessionManager.getInstance();
        String fullName = session.getCurrentUserFullName();
        String role = session.getCurrentUserRole();
        this.username = session.getCurrentUsername();
        this.profileImagePath = session.getCurrentUserProfileImage();

        if (fullName != null) {
            nameLabel.setText(fullName);
        }
        if (role != null) {
            roleLabel.setText(role);
        }

        updatePhotoStatus();
    }

    private void displayAvatar() {
        avatarContainer.getChildren().clear();

        String fullName = nameLabel.getText();
        avatarContainer.getChildren().add(
            AvatarUtil.createAvatarWithImage(profileImagePath, fullName, 100)
        );
    }

    private void updatePhotoStatus() {
        if (profileImagePath != null && !profileImagePath.isEmpty()) {
            photoStatusLabel.setText("Photo: " + new File(profileImagePath).getName());
        } else {
            photoStatusLabel.setText("No photo uploaded - showing avatar");
        }
    }

    @FXML
    private void handleUploadPhoto() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Profile Picture");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif"),
            new FileChooser.ExtensionFilter("All Files", "*.*")
        );

        File selectedFile = fileChooser.showOpenDialog(uploadPhotoButton.getScene().getWindow());

        if (selectedFile != null) {
            try {
                // Create profiles directory if it doesn't exist
                String profilesDir = "profiles";
                Files.createDirectories(Paths.get(profilesDir));

                // Copy file to profiles directory
                String fileName = username + "_" + System.currentTimeMillis() + getFileExtension(selectedFile);
                String destPath = profilesDir + File.separator + fileName;
                Files.copy(selectedFile.toPath(), Paths.get(destPath), StandardCopyOption.REPLACE_EXISTING);

                // Update profile image path
                profileImagePath = destPath;

                // Update in database
                updateProfileImageInDatabase(profileImagePath);

                // Update UI
                displayAvatar();
                updatePhotoStatus();

                showSuccess("Profile picture updated successfully!");

            } catch (Exception e) {
                showError("Error uploading photo: " + e.getMessage());
            }
        }
    }

    @FXML
    private void handleRemovePhoto() {
        try {
            profileImagePath = null;
            updateProfileImageInDatabase(null);

            displayAvatar();
            updatePhotoStatus();

            showSuccess("Profile picture removed!");

        } catch (Exception e) {
            showError("Error removing photo: " + e.getMessage());
        }
    }

    private void updateProfileImageInDatabase(String imagePath) throws DatabaseException {
        SessionManager session = SessionManager.getInstance();
        String currentUsername = session.getCurrentUsername();

        StaffDAO staffDAO = new StaffDAO();
        StaffMember staff = staffDAO.getByUsername(currentUsername);

        if (staff != null) {
            staff.setProfileImage(imagePath);
            staffDAO.updateStaffProfileImage(staff.getStaffId(), imagePath);

            // Update session
            session.setCurrentUser(
                session.getCurrentUsername(),
                session.getCurrentUserFullName(),
                session.getCurrentUserRole(),
                imagePath
            );
        }
    }

    @FXML
    private void handleClose() {
        if (stage != null) {
            stage.close();
        }
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    private String getFileExtension(File file) {
        String name = file.getName();
        int lastIndexOf = name.lastIndexOf(".");
        if (lastIndexOf == -1) {
            return ".jpg";
        }
        return name.substring(lastIndexOf);
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Operation Failed");
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText("Operation Successful");
        alert.setContentText(message);
        alert.showAndWait();
    }

}
