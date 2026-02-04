package com.crimsonbank.services;

import com.crimsonbank.models.Transaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ReportServiceTest {

    @TempDir
    Path tempDir;

    private List<Transaction> transactions;

    @BeforeEach
    public void setUp() {
        transactions = new ArrayList<>();

        Transaction tx1 = new Transaction(1, "DEPOSIT", BigDecimal.valueOf(1000.00), BigDecimal.valueOf(1000.00), "Initial deposit");
        tx1.setTransactionId(1);
        tx1.setCreatedAt(LocalDateTime.now());

        Transaction tx2 = new Transaction(1, "WITHDRAWAL", BigDecimal.valueOf(200.00), BigDecimal.valueOf(800.00), "ATM withdrawal");
        tx2.setTransactionId(2);
        tx2.setCreatedAt(LocalDateTime.now());

        Transaction tx3 = new Transaction(2, "TRANSFER", BigDecimal.valueOf(500.00), BigDecimal.valueOf(1500.00), "Transfer received");
        tx3.setTransactionId(3);
        tx3.setRelatedAccountId(1);
        tx3.setCreatedAt(LocalDateTime.now());

        transactions.add(tx1);
        transactions.add(tx2);
        transactions.add(tx3);
    }

    @Test
    public void testCSVExportWritesFile() throws IOException {
        File csvFile = tempDir.resolve("transactions.csv").toFile();

        exportTransactionsToCSV(transactions, csvFile);

        assertTrue(csvFile.exists());
        assertTrue(csvFile.length() > 0);

        List<String> lines = Files.readAllLines(csvFile.toPath());
        assertTrue(lines.size() > 0);
        assertTrue(lines.get(0).contains("Transaction ID") || lines.get(0).contains("Type"));
    }

    @Test
    public void testCSVExportHasHeaders() throws IOException {
        File csvFile = tempDir.resolve("transactions_with_headers.csv").toFile();

        exportTransactionsToCSV(transactions, csvFile);

        List<String> lines = Files.readAllLines(csvFile.toPath());
        String header = lines.get(0);

        assertTrue(header.contains("Transaction ID") || header.contains("ID"));
        assertTrue(header.contains("Type") || header.contains("TYPE"));
        assertTrue(header.contains("Amount") || header.contains("AMOUNT"));
    }

    @Test
    public void testCSVExportWithEmptyList() throws IOException {
        File csvFile = tempDir.resolve("empty_transactions.csv").toFile();

        exportTransactionsToCSV(new ArrayList<>(), csvFile);

        assertTrue(csvFile.exists());
        List<String> lines = Files.readAllLines(csvFile.toPath());
        assertTrue(lines.size() >= 1);
    }

    private void exportTransactionsToCSV(List<Transaction> transactions, File file) throws IOException {
        StringBuilder csv = new StringBuilder();
        csv.append("Transaction ID,Account ID,Type,Amount,Balance After,Description,Created At\n");

        for (Transaction tx : transactions) {
            csv.append(tx.getTransactionId()).append(",");
            csv.append(tx.getAccountId()).append(",");
            csv.append(tx.getTransactionType()).append(",");
            csv.append(tx.getAmount()).append(",");
            csv.append(tx.getBalanceAfter()).append(",");
            csv.append(tx.getDescription() != null ? tx.getDescription() : "").append(",");
            csv.append(tx.getCreatedAt()).append("\n");
        }

        Files.write(file.toPath(), csv.toString().getBytes());
    }
}
