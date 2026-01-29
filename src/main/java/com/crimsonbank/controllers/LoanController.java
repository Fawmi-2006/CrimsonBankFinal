package com.crimsonbank.controllers;

import com.crimsonbank.database.CustomerDAO;
import com.crimsonbank.database.LoanDAO;
import com.crimsonbank.exceptions.DatabaseException;
import com.crimsonbank.models.Customer;
import com.crimsonbank.models.Loan;
import com.crimsonbank.utils.InputValidator;
import com.crimsonbank.utils.NavigationUtil;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
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
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class LoanController {

    @FXML
    private TableView<Loan> pendingLoansTable;

    @FXML
    private TableView<Loan> approvedLoansTable;

    @FXML
    private TableColumn<Loan, Integer> pendingIdColumn;

    @FXML
    private TableColumn<Loan, String> pendingCustomerColumn;

    @FXML
    private TableColumn<Loan, BigDecimal> pendingAmountColumn;

    @FXML
    private TableColumn<Loan, BigDecimal> pendingRateColumn;

    @FXML
    private TableColumn<Loan, Integer> pendingTenureColumn;

    @FXML
    private TableColumn<Loan, String> pendingStatusColumn;

    @FXML
    private TableColumn<Loan, Void> pendingActionsColumn;

    @FXML
    private TableColumn<Loan, Integer> approvedIdColumn;

    @FXML
    private TableColumn<Loan, String> approvedCustomerColumn;

    @FXML
    private TableColumn<Loan, BigDecimal> approvedAmountColumn;

    @FXML
    private TableColumn<Loan, String> approvedByColumn;

    @FXML
    private TableColumn<Loan, String> approvedDateColumn;

    @FXML
    private Button backButton;

    private LoanDAO loanDAO;
    private CustomerDAO customerDAO;
    private final ObservableList<Loan> pendingLoans = FXCollections.observableArrayList();
    private final ObservableList<Loan> approvedLoans = FXCollections.observableArrayList();
    private final Map<Integer, String> customerNameCache = new HashMap<>();

    @FXML
    private void handleBackToDashboard() {
        NavigationUtil.navigate(backButton, "/com/crimsonbank/views/dashboard.fxml", "CrimsonBank - Dashboard");
    }

    @FXML
    private void handleNewLoan() {
        Loan loan = showLoanDialog();
        if (loan == null) {
            return;
        }
        try {
            int id = loanDAO.createLoan(loan);
            if (id > 0) {
                loadLoans();
                showInfo("Loan request created.");
            }
        } catch (DatabaseException e) {
            showError("Failed to create loan: " + e.getMessage());
        }
    }

    @FXML
    public void initialize() {
        try {
            loanDAO = new LoanDAO();
            customerDAO = new CustomerDAO();
            configureColumns();
            loadLoans();
        } catch (DatabaseException e) {
            showError("Failed to initialize loans: " + e.getMessage());
        }
    }

    private void configureColumns() {
        pendingIdColumn.setCellValueFactory(new PropertyValueFactory<>("loanId"));
        pendingCustomerColumn.setCellValueFactory(data -> new SimpleStringProperty(resolveCustomerName(data.getValue().getCustomerId())));
        pendingAmountColumn.setCellValueFactory(new PropertyValueFactory<>("loanAmount"));
        pendingRateColumn.setCellValueFactory(new PropertyValueFactory<>("interestRate"));
        pendingTenureColumn.setCellValueFactory(new PropertyValueFactory<>("tenureMonths"));
        pendingStatusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        pendingActionsColumn.setCellFactory(col -> new TableCell<>() {
            private final Button approveButton = new Button("Approve");
            private final Button rejectButton = new Button("Reject");
            private final HBox container = new HBox(8, approveButton, rejectButton);

            {
                approveButton.setOnAction(event -> {
                    Loan loan = getTableView().getItems().get(getIndex());
                    handleApprove(loan);
                });
                rejectButton.setOnAction(event -> {
                    Loan loan = getTableView().getItems().get(getIndex());
                    handleReject(loan);
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

        approvedIdColumn.setCellValueFactory(new PropertyValueFactory<>("loanId"));
        approvedCustomerColumn.setCellValueFactory(data -> new SimpleStringProperty(resolveCustomerName(data.getValue().getCustomerId())));
        approvedAmountColumn.setCellValueFactory(new PropertyValueFactory<>("loanAmount"));
        approvedByColumn.setCellValueFactory(data -> new SimpleStringProperty(
            data.getValue().getApprovedBy() == null ? "" : String.valueOf(data.getValue().getApprovedBy())
        ));
        approvedDateColumn.setCellValueFactory(data -> new SimpleStringProperty(
            data.getValue().getApprovalDate() == null ? "" : data.getValue().getApprovalDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        ));
    }

    private void loadLoans() {
        try {
            pendingLoans.setAll(loanDAO.getPendingLoans());
            List<Loan> approved = loanDAO.getAllLoans().stream()
                .filter(loan -> "APPROVED".equalsIgnoreCase(loan.getStatus()))
                .collect(Collectors.toList());
            approvedLoans.setAll(approved);
            pendingLoansTable.setItems(pendingLoans);
            approvedLoansTable.setItems(approvedLoans);
        } catch (DatabaseException e) {
            showError("Failed to load loans: " + e.getMessage());
        }
    }

    private void handleApprove(Loan loan) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Approve Loan");
        confirm.setHeaderText("Approve loan " + loan.getLoanId() + "?");
        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isEmpty() || result.get() != ButtonType.OK) {
            return;
        }
        try {
            boolean success = loanDAO.approveLoan(loan.getLoanId(), 1);
            if (success) {
                loadLoans();
                showInfo("Loan approved.");
            }
        } catch (DatabaseException e) {
            showError("Failed to approve loan: " + e.getMessage());
        }
    }

    private void handleReject(Loan loan) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Reject Loan");
        confirm.setHeaderText("Reject loan " + loan.getLoanId() + "?");
        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isEmpty() || result.get() != ButtonType.OK) {
            return;
        }
        try {
            boolean success = loanDAO.rejectLoan(loan.getLoanId(), 1);
            if (success) {
                loadLoans();
                showInfo("Loan rejected.");
            }
        } catch (DatabaseException e) {
            showError("Failed to reject loan: " + e.getMessage());
        }
    }

    private Loan showLoanDialog() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("New Loan Request");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        TextField customerIdField = new TextField();
        TextField accountIdField = new TextField();
        TextField amountField = new TextField();
        TextField rateField = new TextField();
        TextField tenureField = new TextField();

        grid.addRow(0, new Label("Customer ID"), customerIdField);
        grid.addRow(1, new Label("Account ID"), accountIdField);
        grid.addRow(2, new Label("Amount"), amountField);
        grid.addRow(3, new Label("Interest Rate"), rateField);
        grid.addRow(4, new Label("Tenure (Months)"), tenureField);
        GridPane.setHgrow(customerIdField, Priority.ALWAYS);
        GridPane.setHgrow(accountIdField, Priority.ALWAYS);
        GridPane.setHgrow(amountField, Priority.ALWAYS);
        GridPane.setHgrow(rateField, Priority.ALWAYS);
        GridPane.setHgrow(tenureField, Priority.ALWAYS);

        dialog.getDialogPane().setContent(grid);
        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isEmpty() || result.get() != ButtonType.OK) {
            return null;
        }

        String customerIdText = InputValidator.sanitizeInput(customerIdField.getText());
        String accountIdText = InputValidator.sanitizeInput(accountIdField.getText());
        String amountText = InputValidator.sanitizeInput(amountField.getText());
        String rateText = InputValidator.sanitizeInput(rateField.getText());
        String tenureText = InputValidator.sanitizeInput(tenureField.getText());

        if (!customerIdText.matches("\\d+") || !accountIdText.matches("\\d+") || amountText.isEmpty() || rateText.isEmpty() || tenureText.isEmpty()) {
            showError("All fields are required with valid numbers.");
            return null;
        }

        BigDecimal amount;
        BigDecimal rate;
        int tenure;
        try {
            amount = new BigDecimal(amountText);
            rate = new BigDecimal(rateText);
            tenure = Integer.parseInt(tenureText);
        } catch (NumberFormatException e) {
            showError("Invalid numeric values.");
            return null;
        }
        if (amount.compareTo(BigDecimal.ZERO) <= 0 || rate.compareTo(BigDecimal.ZERO) <= 0 || tenure <= 0) {
            showError("Amount, rate, and tenure must be positive.");
            return null;
        }

        int customerId = Integer.parseInt(customerIdText);
        int accountId = Integer.parseInt(accountIdText);
        return new Loan(customerId, accountId, amount, rate, tenure);
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
