package com.crimsonbank.controllers;

import com.crimsonbank.database.AccountDAO;
import com.crimsonbank.database.TransactionDAO;
import com.crimsonbank.exceptions.DatabaseException;
import com.crimsonbank.models.Account;
import com.crimsonbank.models.Transaction;
import com.crimsonbank.utils.NavigationUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.stage.FileChooser;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class ReportController {

    @FXML
    private ComboBox<String> transactionTypeCombo;

    @FXML
    private PieChart transactionPieChart;

    @FXML
    private BarChart<String, Number> monthlyBarChart;

    @FXML
    private Label totalTransLabel;

    @FXML
    private Label totalVolumeLabel;

    @FXML
    private Label avgTransLabel;

    @FXML
    private Label topAccountLabel;

    @FXML
    private Button backButton;

    private TransactionDAO transactionDAO;
    private AccountDAO accountDAO;
    private List<Transaction> allTransactions = List.of();

    @FXML
    private void handleBackToDashboard() {
        NavigationUtil.navigate(backButton, "/com/crimsonbank/views/dashboard.fxml", "CrimsonBank - Dashboard");
    }

    @FXML
    private void handleGenerateTransactionReport() {
        String selectedType = transactionTypeCombo.getValue();
        List<Transaction> filtered = allTransactions;
        if (selectedType != null && !selectedType.equals("ALL")) {
            filtered = allTransactions.stream()
                .filter(tx -> selectedType.equalsIgnoreCase(tx.getTransactionType()))
                .collect(Collectors.toList());
        }
        Map<String, Long> counts = filtered.stream()
            .collect(Collectors.groupingBy(Transaction::getTransactionType, Collectors.counting()));
        ObservableList<PieChart.Data> data = FXCollections.observableArrayList();
        counts.forEach((type, count) -> data.add(new PieChart.Data(type, count)));
        transactionPieChart.setData(data);
    }

    @FXML
    private void handleGenerateMonthlyReport() {
        Map<YearMonth, BigDecimal> totals = allTransactions.stream()
            .filter(tx -> tx.getCreatedAt() != null)
            .collect(Collectors.groupingBy(
                tx -> YearMonth.from(tx.getCreatedAt()),
                Collectors.reducing(BigDecimal.ZERO, Transaction::getAmount, BigDecimal::add)
            ));
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        totals.entrySet().stream()
            .sorted(Map.Entry.comparingByKey())
            .forEach(entry -> series.getData().add(new XYChart.Data<>(entry.getKey().format(DateTimeFormatter.ofPattern("yyyy-MM")), entry.getValue())));
        monthlyBarChart.getData().clear();
        monthlyBarChart.getData().add(series);
    }

    @FXML
    private void handleExportCsv() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Export Transactions");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        chooser.setInitialFileName("transactions_report.csv");
        java.io.File file = chooser.showSaveDialog(backButton.getScene().getWindow());
        if (file == null) {
            return;
        }
        StringBuilder builder = new StringBuilder();
        builder.append("Transaction ID,Account ID,Type,Amount,Balance After,Created At,Description\n");
        for (Transaction tx : allTransactions) {
            builder.append(tx.getTransactionId()).append(',')
                .append(tx.getAccountId()).append(',')
                .append(safe(tx.getTransactionType())).append(',')
                .append(tx.getAmount()).append(',')
                .append(tx.getBalanceAfter()).append(',')
                .append(tx.getCreatedAt() == null ? "" : tx.getCreatedAt()).append(',')
                .append(escapeCsv(tx.getDescription())).append('\n');
        }
        try {
            Files.writeString(file.toPath(), builder.toString(), StandardCharsets.UTF_8);
            showInfo("CSV exported successfully.");
        } catch (IOException e) {
            showError("Failed to export CSV: " + e.getMessage());
        }
    }

    @FXML
    public void initialize() {
        try {
            transactionDAO = new TransactionDAO();
            accountDAO = new AccountDAO();
            loadData();
            populateTransactionTypes();
            updateSummary();
            handleGenerateTransactionReport();
            handleGenerateMonthlyReport();
        } catch (DatabaseException e) {
            showError("Failed to initialize reports: " + e.getMessage());
        }
    }

    private void loadData() throws DatabaseException {
        allTransactions = transactionDAO.getAllTransactions();
    }

    private void populateTransactionTypes() {
        List<String> types = allTransactions.stream()
            .map(Transaction::getTransactionType)
            .distinct()
            .sorted()
            .collect(Collectors.toList());
        transactionTypeCombo.setItems(FXCollections.observableArrayList(types));
        transactionTypeCombo.getItems().add(0, "ALL");
        transactionTypeCombo.getSelectionModel().select(0);
    }

    private void updateSummary() {
        int totalCount = allTransactions.size();
        BigDecimal totalVolume = allTransactions.stream()
            .map(Transaction::getAmount)
            .filter(amount -> amount != null)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal average = totalCount == 0 ? BigDecimal.ZERO : totalVolume.divide(BigDecimal.valueOf(totalCount), 2, RoundingMode.HALF_UP);

        Map<Integer, BigDecimal> totalsByAccount = allTransactions.stream()
            .collect(Collectors.groupingBy(Transaction::getAccountId,
                Collectors.reducing(BigDecimal.ZERO, Transaction::getAmount, BigDecimal::add)));
        Optional<Map.Entry<Integer, BigDecimal>> topAccount = totalsByAccount.entrySet().stream()
            .max(Comparator.comparing(Map.Entry::getValue));

        totalTransLabel.setText(String.valueOf(totalCount));
        totalVolumeLabel.setText(totalVolume.setScale(2, RoundingMode.HALF_UP).toPlainString());
        avgTransLabel.setText(average.toPlainString());
        topAccountLabel.setText(topAccount.map(entry -> resolveAccountLabel(entry.getKey())).orElse("N/A"));
    }

    private String resolveAccountLabel(int accountId) {
        try {
            Account account = accountDAO.getById(accountId);
            if (account != null) {
                return account.getAccountNumber();
            }
        } catch (DatabaseException e) {
            showError("Failed to load account: " + e.getMessage());
        }
        return "Account " + accountId;
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }

    private String escapeCsv(String value) {
        if (value == null) {
            return "";
        }
        String escaped = value.replace("\"", "\"\"");
        if (escaped.contains(",") || escaped.contains("\n")) {
            return "\"" + escaped + "\"";
        }
        return escaped;
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
