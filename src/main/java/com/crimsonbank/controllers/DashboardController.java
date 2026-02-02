package com.crimsonbank.controllers;

import com.crimsonbank.database.CustomerDAO;
import com.crimsonbank.database.AccountDAO;
import com.crimsonbank.database.TransactionDAO;
import com.crimsonbank.database.LoanDAO;
import com.crimsonbank.database.StaffDAO;
import com.crimsonbank.exceptions.DatabaseException;
import com.crimsonbank.models.StaffMember;
import com.crimsonbank.utils.AvatarUtil;
import com.crimsonbank.utils.SessionManager;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DashboardController {

    @FXML
    private Label totalCustomersLabel;

    @FXML
    private Label totalAccountsLabel;

    @FXML
    private Label totalTransactionsLabel;

    @FXML
    private Label pendingLoansLabel;

    @FXML
    private Label dbStatusLabel;

    @FXML
    private Label lastUpdatedLabel;

    @FXML
    private Button logoutButton;

    @FXML
    private Button dashboardBtn;

    @FXML
    private Button customerBtn;

    @FXML
    private Button accountBtn;

    @FXML
    private Button transactionBtn;

    @FXML
    private Button historyBtn;

    @FXML
    private Button loanBtn;

    @FXML
    private Button reportBtn;

    @FXML
    private Button auditBtn;

    @FXML
    private Button quickAddCustomerButton;

    @FXML
    private Button quickCreateAccountButton;

    @FXML
    private Button quickNewTransactionButton;

    @FXML
    private VBox userProfilePane;

    @FXML
    private Label userNameLabel;

    @FXML
    private Button profileButton;

    @FXML
    public void initialize() {
        logoutButton.setOnAction(event -> handleLogout());
        profileButton.setOnAction(event -> openProfileSettings());
        customerBtn.setOnAction(event -> navigateToPage("customer"));
        accountBtn.setOnAction(event -> navigateToPage("account"));
        transactionBtn.setOnAction(event -> navigateToPage("transaction"));
        historyBtn.setOnAction(event -> navigateToPage("history"));
        loanBtn.setOnAction(event -> navigateToPage("loan"));
        reportBtn.setOnAction(event -> navigateToPage("report"));
        auditBtn.setOnAction(event -> navigateToPage("audit"));
        dashboardBtn.setOnAction(event -> refreshDashboard());
        quickAddCustomerButton.setOnAction(event -> handleQuickAddCustomer());
        quickCreateAccountButton.setOnAction(event -> handleQuickCreateAccount());
        quickNewTransactionButton.setOnAction(event -> handleQuickNewTransaction());

        loadUserProfile();
        loadDashboardData();
    }

    private void loadUserProfile() {
        SessionManager session = SessionManager.getInstance();
        String fullName = session.getCurrentUserFullName();
        String profileImage = session.getCurrentUserProfileImage();

        if (fullName != null && !fullName.isEmpty()) {
            userNameLabel.setText(fullName);

            userProfilePane.getChildren().add(0, AvatarUtil.createAvatarWithImage(profileImage, fullName, 50));
        }
    }

    private void loadDashboardData() {
        try {
            CustomerDAO customerDAO = new CustomerDAO();
            AccountDAO accountDAO = new AccountDAO();
            TransactionDAO transactionDAO = new TransactionDAO();
            LoanDAO loanDAO = new LoanDAO();

            int totalCustomers = customerDAO.getAllCustomers().size();
            int totalAccounts = accountDAO.getAllAccounts().size();
            int totalTransactions = (int) transactionDAO.getAllTransactions().size();
            int pendingLoans = loanDAO.getPendingLoans().size();

            totalCustomersLabel.setText(String.valueOf(totalCustomers));
            totalAccountsLabel.setText(String.valueOf(totalAccounts));
            totalTransactionsLabel.setText(String.valueOf(totalTransactions));
            pendingLoansLabel.setText(String.valueOf(pendingLoans));

            dbStatusLabel.setText("Connected");
            dbStatusLabel.setStyle("-fx-text-fill: #27ae60;");

            updateLastUpdated();

        } catch (DatabaseException e) {
            dbStatusLabel.setText("Error: " + e.getMessage());
            dbStatusLabel.setStyle("-fx-text-fill: #c0392b;");
        }
    }

    private void updateLastUpdated() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        lastUpdatedLabel.setText(now.format(formatter));
    }

    private void refreshDashboard() {
        loadDashboardData();
    }

    private void navigateToPage(String page) {
        try {
            String fxmlFile = "/com/crimsonbank/views/" + page + ".fxml";
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource(fxmlFile));
            Parent root = loader.load();

            Stage stage = (Stage) customerBtn.getScene().getWindow();
            Scene scene = new Scene(root);

            java.net.URL styleUrl = getClass().getResource("/com/crimsonbank/styles.css");
            if (styleUrl != null) {
                scene.getStylesheets().add(styleUrl.toExternalForm());
            }

            stage.setScene(scene);
            stage.setTitle("CrimsonBank - " + page.substring(0, 1).toUpperCase() + page.substring(1));
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
            showError("Navigation error: " + e.getMessage());
        }
    }

    private void handleLogout() {
        try {
            SessionManager.getInstance().clearSession();

            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/com/crimsonbank/views/login.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) logoutButton.getScene().getWindow();
            Scene scene = new Scene(root);

            java.net.URL styleUrl = getClass().getResource("/com/crimsonbank/styles.css");
            if (styleUrl != null) {
                scene.getStylesheets().add(styleUrl.toExternalForm());
            }

            stage.setScene(scene);
            stage.setTitle("CrimsonBank - Login");
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
            showError("Logout error: " + e.getMessage());
        }
    }

    private void openProfileSettings() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/com/crimsonbank/views/profile-settings.fxml"));
            VBox root = loader.load();

            ProfileSettingsController controller = loader.getController();

            Stage stage = new Stage();
            stage.setTitle("Profile Settings");
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.initModality(javafx.stage.Modality.APPLICATION_MODAL);

            java.net.URL styleUrl = getClass().getResource("/com/crimsonbank/styles.css");
            if (styleUrl != null) {
                stage.getScene().getStylesheets().add(styleUrl.toExternalForm());
            }

            controller.setStage(stage);
            stage.showAndWait();

            refreshDashboard();

        } catch (Exception e) {
            e.printStackTrace();
            showError("Error opening profile settings: " + e.getMessage());
        }
    }

    @FXML
    private void handleQuickAddCustomer() {
        navigateToPage("customer");
    }

    @FXML
    private void handleQuickCreateAccount() {
        navigateToPage("account");
    }

    @FXML
    private void handleQuickNewTransaction() {
        navigateToPage("transaction");
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("An error occurred");
        alert.setContentText(message);
        alert.showAndWait();
    }

}
