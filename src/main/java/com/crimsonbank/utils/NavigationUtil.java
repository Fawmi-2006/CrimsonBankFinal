package com.crimsonbank.utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public final class NavigationUtil {

    private NavigationUtil() {
    }

    public static void navigate(Node source, String fxmlPath, String title) {
        try {
            Parent root = loadRoot(fxmlPath);
            Stage stage = (Stage) source.getScene().getWindow();
            Scene scene = new Scene(root);

            scene.getStylesheets().add(Objects.requireNonNull(
                    NavigationUtil.class.getResource("/com/crimsonbank/styles.css"),
                    "styles.css not found"
            ).toExternalForm());

            stage.setScene(scene);
            if (title != null && !title.isBlank()) {
                stage.setTitle(title);
            }
            stage.show();

        } catch (Exception e) {
            showError("Navigation error: " + e.getMessage());
        }
    }

    private static Parent loadRoot(String fxmlPath) throws IOException {
        return new FXMLLoader(Objects.requireNonNull(
                NavigationUtil.class.getResource(fxmlPath),
                "FXML not found: " + fxmlPath
        )).load();
    }

    private static void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("An error occurred");
        alert.setContentText(message);
        alert.showAndWait();
    }

}
