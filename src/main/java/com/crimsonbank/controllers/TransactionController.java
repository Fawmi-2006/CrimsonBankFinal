package com.crimsonbank.controllers;

import com.crimsonbank.database.AccountDAO;
import com.crimsonbank.database.TransactionDAO;
import com.crimsonbank.exceptions.DatabaseException;
import com.crimsonbank.exceptions.InsufficientFundsException;
import com.crimsonbank.models.Account;
import com.crimsonbank.models.Transaction;
import com.crimsonbank.services.TransactionService;
import com.crimsonbank.utils.InputValidator;
import com.crimsonbank.utils.NavigationUtil;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.util.StringConverter;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

public class TransactionController {

    @FXML
    private ComboBox<Account> accountCombo;

    @FXML
    private TableView<Transaction> transactionTable;

    @FXML
    private TableColumn<Transaction, String> dateColumn;

    @FXML
    private TableColumn<Transaction, String> typeColumn;

    @FXML
    private TableColumn<Transaction, BigDecimal> amountColumn;

    @FXML
    private TableColumn<Transaction, BigDecimal> balanceColumn;

    @FXML
    private TableColumn<Transaction, String> descriptionColumn;

    @FXML
    private Button backButton;

    private AccountDAO accountDAO;
    private TransactionDAO transactionDAO;
    private TransactionService transactionService;
    private final ObservableList<Transaction> transactions = FXCollections.observableArrayList();

    @FXML
    private void handleBackToDashboard() {
        NavigationUtil.navigate(backButton, "/com/crimsonbank/views/dashboard.fxml", "CrimsonBank - Dashboard");
    }

    @FXML
    private void handleDeposit() {
        if (transactionService == null) {
            showError("Transaction service not initialized.");
            return;
        }
        Account account = accountCombo.getValue();
        if (account == null) {
            showError("Select an account first.");
            return;
        }
        TransactionInput input = showTransactionDialog("Deposit", false);
        if (input == null) {
            return;
        }
        try {
            transactionService.deposit(account.getAccountId(), input.amount, input.description, null);
            loadTransactions(account.getAccountId());
            showInfo("Deposit successful.");
        } catch (DatabaseException | InsufficientFundsException e) {
            showError(e.getMessage());
        }
    }

    @FXML
    private void handleWithdraw() {
        if (transactionService == null) {
            showError("Transaction service not initialized.");
            return;
        }
        Account account = accountCombo.getValue();
        if (account == null) {
            showError("Select an account first.");
            return;
        }
        TransactionInput input = showTransactionDialog("Withdraw", false);
        if (input == null) {
            return;
        }
        try {
            transactionService.withdraw(account.getAccountId(), input.amount, input.description, null);
            loadTransactions(account.getAccountId());
            showInfo("Withdrawal successful.");
        } catch (DatabaseException | InsufficientFundsException e) {
            showError(e.getMessage());
        }
    }

    @FXML
    private void handleTransfer() {
        if (transactionService == null) {
            showError("Transaction service not initialized.");
            return;
        }
        Account account = accountCombo.getValue();
        if (account == null) {
            showError("Select an account first.");
            return;
        }
        TransactionInput input = showTransactionDialog("Transfer", true);
        if (input == null || input.targetAccountNumber == null) {
            return;
        }
        try {
            Account target = accountDAO.getByAccountNumber(input.targetAccountNumber);
            if (target == null) {
                showError("Target account not found.");
                return;
            }
            transactionService.transfer(account.getAccountId(), target.getAccountId(), input.amount, null);
            loadTransactions(account.getAccountId());
            showInfo("Transfer successful.");
        } catch (DatabaseException | InsufficientFundsException e) {
            showError(e.getMessage());
        }
    }

    @FXML
    public void initialize() {
        try {
            accountDAO = new AccountDAO();
            transactionDAO = new TransactionDAO();
            transactionService = new TransactionService();
            configureColumns();
            loadAccounts();
        } catch (DatabaseException e) {
            showError("Failed to initialize transactions: " + e.getMessage());
        }
    }

    private void configureColumns() {
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
                if (string == null || string.isEmpty()) {
                    return null;
                }
                for (Account account : accountCombo.getItems()) {
                    if (account.getAccountNumber().equals(string)) {
                        return account;
                    }
                }
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
            transactionTable.setItems(transactions);
        } catch (DatabaseException e) {
            showError("Failed to load transactions: " + e.getMessage());
        }
    }

    private TransactionInput showTransactionDialog(String title, boolean includeTarget) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle(title);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        TextField amountField = new TextField();
        TextField descriptionField = new TextField();
        TextField targetField = new TextField();

        grid.addRow(0, new Label("Amount"), amountField);
        grid.addRow(1, new Label("Description"), descriptionField);
        if (includeTarget) {
            grid.addRow(2, new Label("Target Account"), targetField);
        }
        GridPane.setHgrow(amountField, Priority.ALWAYS);
        GridPane.setHgrow(descriptionField, Priority.ALWAYS);
        GridPane.setHgrow(targetField, Priority.ALWAYS);

        dialog.getDialogPane().setContent(grid);
        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isEmpty() || result.get() != ButtonType.OK) {
            return null;
        }

        String amountText = InputValidator.sanitizeInput(amountField.getText());
        String description = InputValidator.sanitizeInput(descriptionField.getText());
        String targetAccount = InputValidator.sanitizeInput(targetField.getText());

        if (amountText.isEmpty()) {
            showError("Amount is required.");
            return null;
        }
        BigDecimal amount;
        try {
            amount = new BigDecimal(amountText);
        } catch (NumberFormatException e) {
            showError("Amount must be a valid number.");
            return null;
        }
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            showError("Amount must be positive.");
            return null;
        }
        if (includeTarget && !InputValidator.isValidAccountNumber(targetAccount)) {
            showError("Target account number must be ACC followed by 6 digits.");
            return null;
        }
        if (description.isEmpty()) {
            description = title;
        }

        TransactionInput input = new TransactionInput(amount, description);
        if (includeTarget) {
            input.targetAccountNumber = targetAccount;
        }
        return input;
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Operation Failed");
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Info");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private static class TransactionInput {
        private final BigDecimal amount;
        private final String description;
        private String targetAccountNumber;

        private TransactionInput(BigDecimal amount, String description) {
            this.amount = amount;
            this.description = description;
        }
    }

}
