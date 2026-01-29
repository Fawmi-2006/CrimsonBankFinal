package com.crimsonbank.controllers;

import com.crimsonbank.database.AccountDAO;
import com.crimsonbank.database.CustomerDAO;
import com.crimsonbank.exceptions.DatabaseException;
import com.crimsonbank.models.Account;
import com.crimsonbank.models.Customer;
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
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class AccountController {

    @FXML
    private TextField searchField;

    @FXML
    private TableView<Account> accountTable;

    @FXML
    private TableColumn<Account, Integer> idColumn;

    @FXML
    private TableColumn<Account, String> numberColumn;

    @FXML
    private TableColumn<Account, String> customerColumn;

    @FXML
    private TableColumn<Account, String> typeColumn;

    @FXML
    private TableColumn<Account, BigDecimal> balanceColumn;

    @FXML
    private TableColumn<Account, String> statusColumn;

    @FXML
    private TableColumn<Account, Void> actionsColumn;

    @FXML
    private Button backButton;

    private AccountDAO accountDAO;
    private CustomerDAO customerDAO;
    private final ObservableList<Account> accounts = FXCollections.observableArrayList();
    private final Map<Integer, String> customerNameCache = new HashMap<>();

    @FXML
    private void handleBackToDashboard() {
        NavigationUtil.navigate(backButton, "/com/crimsonbank/views/dashboard.fxml", "CrimsonBank - Dashboard");
    }

    @FXML
    private void handleSearch() {
        String term = InputValidator.sanitizeInput(searchField.getText());
        if (term.isEmpty()) {
            loadAllAccounts();
            return;
        }
        try {
            if (term.matches("\\d+")) {
                int id = Integer.parseInt(term);
                Account account = accountDAO.getById(id);
                if (account != null) {
                    accounts.setAll(account);
                } else {
                    accounts.setAll(accountDAO.getByCustomerId(id));
                }
                accountTable.setItems(accounts);
                if (accounts.isEmpty()) {
                    showInfo("No accounts found.");
                }
                return;
            }
            if (InputValidator.isValidAccountNumber(term)) {
                Account account = accountDAO.getByAccountNumber(term);
                if (account != null) {
                    accounts.setAll(account);
                } else {
                    accounts.clear();
                }
                accountTable.setItems(accounts);
                if (accounts.isEmpty()) {
                    showInfo("No accounts found.");
                }
                return;
            }
            showInfo("Enter a valid account number or numeric ID.");
        } catch (DatabaseException e) {
            showError("Failed to search accounts: " + e.getMessage());
        }
    }

    @FXML
    private void handleCreate() {
        Account account = showAccountDialog(null);
        if (account == null) {
            return;
        }
        try {
            int id = accountDAO.createAccount(account);
            if (id > 0) {
                loadAllAccounts();
                showInfo("Account created successfully.");
            }
        } catch (DatabaseException e) {
            showError("Failed to create account: " + e.getMessage());
        }
    }

    @FXML
    public void initialize() {
        try {
            accountDAO = new AccountDAO();
            customerDAO = new CustomerDAO();
            configureColumns();
            loadAllAccounts();
        } catch (DatabaseException e) {
            showError("Failed to initialize accounts: " + e.getMessage());
        }
    }

    private void configureColumns() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("accountId"));
        numberColumn.setCellValueFactory(new PropertyValueFactory<>("accountNumber"));
        customerColumn.setCellValueFactory(data -> new SimpleStringProperty(resolveCustomerName(data.getValue().getCustomerId())));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("accountType"));
        balanceColumn.setCellValueFactory(new PropertyValueFactory<>("balance"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        actionsColumn.setCellFactory(col -> new TableCell<>() {
            private final Button editButton = new Button("Edit");
            private final Button deleteButton = new Button("Delete");
            private final HBox container = new HBox(8, editButton, deleteButton);

            {
                editButton.setOnAction(event -> {
                    Account account = getTableView().getItems().get(getIndex());
                    handleEdit(account);
                });
                deleteButton.setOnAction(event -> {
                    Account account = getTableView().getItems().get(getIndex());
                    handleDelete(account);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(container);
                }
            }
        });
    }

    private void loadAllAccounts() {
        try {
            accounts.setAll(accountDAO.getAllAccounts());
            accountTable.setItems(accounts);
        } catch (DatabaseException e) {
            showError("Failed to load accounts: " + e.getMessage());
        }
    }

    private void handleEdit(Account existing) {
        Account updated = showAccountDialog(existing);
        if (updated == null) {
            return;
        }
        updated.setAccountId(existing.getAccountId());
        try {
            boolean success = accountDAO.updateAccount(updated);
            if (success) {
                loadAllAccounts();
                showInfo("Account updated successfully.");
            }
        } catch (DatabaseException e) {
            showError("Failed to update account: " + e.getMessage());
        }
    }

    private void handleDelete(Account account) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Delete Account");
        confirm.setHeaderText("Delete account " + account.getAccountNumber() + "?");
        confirm.setContentText("This action cannot be undone.");
        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isEmpty() || result.get() != ButtonType.OK) {
            return;
        }
        try {
            boolean success = accountDAO.deleteAccount(account.getAccountId());
            if (success) {
                loadAllAccounts();
                showInfo("Account deleted successfully.");
            }
        } catch (DatabaseException e) {
            showError("Failed to delete account: " + e.getMessage());
        }
    }

    private Account showAccountDialog(Account existing) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle(existing == null ? "Create Account" : "Edit Account");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        TextField customerIdField = new TextField();
        TextField accountNumberField = new TextField();
        ComboBox<String> typeCombo = new ComboBox<>();
        typeCombo.getItems().addAll("SAVINGS", "CURRENT");
        TextField balanceField = new TextField();
        ComboBox<String> statusCombo = new ComboBox<>();
        statusCombo.getItems().addAll("ACTIVE", "INACTIVE");

        if (existing != null) {
            customerIdField.setText(String.valueOf(existing.getCustomerId()));
            accountNumberField.setText(existing.getAccountNumber());
            typeCombo.setValue(existing.getAccountType());
            balanceField.setText(existing.getBalance() != null ? existing.getBalance().toPlainString() : "0.00");
            statusCombo.setValue(existing.getStatus());
            customerIdField.setDisable(true);
            accountNumberField.setDisable(true);
        } else {
            statusCombo.setValue("ACTIVE");
        }

        grid.addRow(0, new Label("Customer ID"), customerIdField);
        grid.addRow(1, new Label("Account Number"), accountNumberField);
        grid.addRow(2, new Label("Account Type"), typeCombo);
        grid.addRow(3, new Label("Balance"), balanceField);
        grid.addRow(4, new Label("Status"), statusCombo);
        GridPane.setHgrow(customerIdField, Priority.ALWAYS);
        GridPane.setHgrow(accountNumberField, Priority.ALWAYS);
        GridPane.setHgrow(typeCombo, Priority.ALWAYS);
        GridPane.setHgrow(balanceField, Priority.ALWAYS);
        GridPane.setHgrow(statusCombo, Priority.ALWAYS);

        dialog.getDialogPane().setContent(grid);
        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isEmpty() || result.get() != ButtonType.OK) {
            return null;
        }

        String customerIdText = InputValidator.sanitizeInput(customerIdField.getText());
        String accountNumber = InputValidator.sanitizeInput(accountNumberField.getText());
        String type = typeCombo.getValue();
        String balanceText = InputValidator.sanitizeInput(balanceField.getText());
        String status = statusCombo.getValue();

        if (customerIdText.isEmpty() || accountNumber.isEmpty() || type == null || balanceText.isEmpty() || status == null) {
            showError("All fields are required.");
            return null;
        }
        if (!customerIdText.matches("\\d+")) {
            showError("Customer ID must be numeric.");
            return null;
        }
        if (!InputValidator.isValidAccountNumber(accountNumber)) {
            showError("Account number format must be ACC followed by 6 digits.");
            return null;
        }
        if (!InputValidator.isValidAccountType(type)) {
            showError("Account type must be SAVINGS or CURRENT.");
            return null;
        }
        BigDecimal balance;
        try {
            balance = new BigDecimal(balanceText);
        } catch (NumberFormatException e) {
            showError("Balance must be a valid number.");
            return null;
        }
        if (balance.compareTo(BigDecimal.ZERO) < 0) {
            showError("Balance must be non-negative.");
            return null;
        }

        int customerId = Integer.parseInt(customerIdText);
        Account account = new Account(customerId, accountNumber, type, balance);
        account.setStatus(status);
        return account;
    }

    private String resolveCustomerName(int customerId) {
        if (customerNameCache.containsKey(customerId)) {
            return customerNameCache.get(customerId);
        }
        try {
            Customer customer = customerDAO.getById(customerId);
            if (customer != null) {
                String name = customer.getFullName();
                if (name != null && !name.trim().isEmpty()) {
                    customerNameCache.put(customerId, name);
                    return name;
                }
            }
        } catch (DatabaseException e) {
            showError("Failed to load customer name: " + e.getMessage());
        }
        return "Unknown";
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

}
