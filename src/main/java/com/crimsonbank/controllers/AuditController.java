package com.crimsonbank.controllers;

import com.crimsonbank.database.AuditLogDAO;
import com.crimsonbank.exceptions.DatabaseException;
import com.crimsonbank.models.AuditLog;
import com.crimsonbank.utils.NavigationUtil;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class AuditController {

    @FXML
    private ComboBox<String> actionFilterCombo;

    @FXML
    private DatePicker fromDatePicker;

    @FXML
    private DatePicker toDatePicker;

    @FXML
    private TableView<AuditLog> auditTable;

    @FXML
    private TableColumn<AuditLog, Integer> idColumn;

    @FXML
    private TableColumn<AuditLog, String> timestampColumn;

    @FXML
    private TableColumn<AuditLog, String> actionColumn;

    @FXML
    private TableColumn<AuditLog, String> staffColumn;

    @FXML
    private TableColumn<AuditLog, String> descriptionColumn;

    @FXML
    private Button backButton;

    private AuditLogDAO auditLogDAO;
    private final ObservableList<AuditLog> logs = FXCollections.observableArrayList();

    @FXML
    private void handleBackToDashboard() {
        NavigationUtil.navigate(backButton, "/com/crimsonbank/views/dashboard.fxml", "CrimsonBank - Dashboard");
    }

    @FXML
    private void handleFilter() {
        String action = actionFilterCombo.getValue();
        LocalDate from = fromDatePicker.getValue();
        LocalDate to = toDatePicker.getValue();
        List<AuditLog> filtered = logs.stream().filter(log -> {
            if (log.getCreatedAt() == null) {
                return false;
            }
            LocalDate date = log.getCreatedAt().toLocalDate();
            if (action != null && !action.equals("ALL") && !action.equalsIgnoreCase(log.getAction())) {
                return false;
            }
            if (from != null && date.isBefore(from)) {
                return false;
            }
            if (to != null && date.isAfter(to)) {
                return false;
            }
            return true;
        }).collect(Collectors.toList());
        auditTable.setItems(FXCollections.observableArrayList(filtered));
    }

    @FXML
    public void initialize() {
        try {
            auditLogDAO = new AuditLogDAO();
            configureColumns();
            loadLogs();
        } catch (DatabaseException e) {
            showError("Failed to initialize audit logs: " + e.getMessage());
        }
    }

    private void configureColumns() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("logId"));
        timestampColumn.setCellValueFactory(data -> new SimpleStringProperty(
            data.getValue().getCreatedAt() == null ? "" : data.getValue().getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
        ));
        actionColumn.setCellValueFactory(new PropertyValueFactory<>("action"));
        staffColumn.setCellValueFactory(data -> new SimpleStringProperty(
            data.getValue().getStaffId() == null ? "" : String.valueOf(data.getValue().getStaffId())
        ));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
    }

    private void loadLogs() throws DatabaseException {
        logs.setAll(auditLogDAO.getAllLogs());
        auditTable.setItems(logs);
        Set<String> actions = logs.stream().map(AuditLog::getAction).collect(Collectors.toSet());
        actionFilterCombo.setItems(FXCollections.observableArrayList(actions));
        actionFilterCombo.getItems().add(0, "ALL");
        actionFilterCombo.getSelectionModel().select(0);
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Operation Failed");
        alert.setContentText(message);
        alert.showAndWait();
    }

}
