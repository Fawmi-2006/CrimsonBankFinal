package com.crimsonbank.controllers;

import com.crimsonbank.database.AccountDAO;
import com.crimsonbank.database.TransactionDAO;
import com.crimsonbank.exceptions.DatabaseException;
import com.crimsonbank.models.Account;
import com.crimsonbank.models.Transaction;
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
import javafx.util.StringConverter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class HistoryController {

    @FXML
    private ComboBox<Account> accountCombo;

    @FXML
    private DatePicker fromDate;

    @FXML
    private DatePicker toDate;

    @FXML
    private TableView<Transaction> historyTable;

    @FXML
    private TableColumn<Transaction, Integer> idColumn;

    @FXML
    private TableColumn<Transaction, String> dateColumn;

    @FXML
    private TableColumn<Transaction, String> typeColumn;

    @FXML
    private TableColumn<Transaction, java.math.BigDecimal> amountColumn;

    @FXML
    private TableColumn<Transaction, java.math.BigDecimal> balanceColumn;

    @FXML
    private TableColumn<Transaction, String> descriptionColumn;

    @FXML
    private Button backButton;

    private AccountDAO accountDAO;
    private TransactionDAO transactionDAO;
    private final ObservableList<Transaction> transactions = FXCollections.observableArrayList();

    @FXML
    private void handleBackToDashboard() {
        NavigationUtil.navigate(backButton, "/com/crimsonbank/views/dashboard.fxml", "CrimsonBank - Dashboard");
    }

    @FXML
    private void handleFilter() {
        Account account = accountCombo.getValue();
        if (account == null) {
            showError("Select an account first.");
            return;
        }
        try {
            List<Transaction> all = transactionDAO.getByAccountId(account.getAccountId());
            LocalDate from = fromDate.getValue();
            LocalDate to = toDate.getValue();
            List<Transaction> filtered = all.stream().filter(tx -> {
                if (tx.getCreatedAt() == null) {
                    return false;
                }
                LocalDate date = tx.getCreatedAt().toLocalDate();
                if (from != null && date.isBefore(from)) {
                    return false;
                }
                if (to != null && date.isAfter(to)) {
                    return false;
                }
                return true;
            }).collect(Collectors.toList());
            transactions.setAll(filtered);
            historyTable.setItems(transactions);
        } catch (DatabaseException e) {
            showError("Failed to filter history: " + e.getMessage());
        }
    }

    @FXML
    public void initialize() {
        try {
            accountDAO = new AccountDAO();
            transactionDAO = new TransactionDAO();
            configureColumns();
            loadAccounts();
        } catch (DatabaseException e) {
            showError("Failed to initialize history: " + e.getMessage());
        }
    }

    private void configureColumns() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("transactionId"));
        dateColumn.setCellValueFactory(data -> new SimpleStringProperty(
            data.getValue().getCreatedAt() == null ? "" : data.getValue().getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
        ));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("transactionType"));
        amountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));
        balanceColumn.setCellValueFactory(new PropertyValueFactory<>("balanceAfter"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
    }

    private void loadAccounts() throws DatabaseException {
        List<Account> accountList = accountDAO.getAllAccounts();
        accountCombo.setItems(FXCollections.observableArrayList(accountList));
        accountCombo.setConverter(new StringConverter<>() {
            @Override
            public String toString(Account account) {
                return account == null ? "" : account.getAccountNumber();
            }

            @Override
            public Account fromString(String string) {
                return null;
            }
        });
        accountCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                loadTransactions(newVal.getAccountId());
            }
        });
        if (!accountList.isEmpty()) {
            accountCombo.getSelectionModel().select(0);
        }
    }

    private void loadTransactions(int accountId) {
        try {
            transactions.setAll(transactionDAO.getByAccountId(accountId));
            historyTable.setItems(transactions);
        } catch (DatabaseException e) {
            showError("Failed to load history: " + e.getMessage());
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Operation Failed");
        alert.setContentText(message);
        alert.showAndWait();
    }

}
