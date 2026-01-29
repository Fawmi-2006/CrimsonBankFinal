package com.crimsonbank.controllers;

import javafx.scene.control.Alert;

/**
 * Base controller class for all JavaFX controllers to reduce code duplication.
 * Provides common methods for displaying error, info, and warning dialogs.
 */
public abstract class BaseController {

    /**
     * Show error dialog to user
     */
    protected void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Operation Failed");
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Show information dialog to user
     */
    protected void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Info");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Show warning dialog to user
     */
    protected void showWarning(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Warning");
        alert.setHeaderText("Attention Required");
        alert.setContentText(message);
        alert.showAndWait();
    }

}
