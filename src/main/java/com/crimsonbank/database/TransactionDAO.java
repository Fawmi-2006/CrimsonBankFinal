package com.crimsonbank.database;

import com.crimsonbank.exceptions.DatabaseException;
import com.crimsonbank.models.Transaction;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TransactionDAO {

    private final DatabaseConnection dbConnection;

    public TransactionDAO() throws DatabaseException {
        this.dbConnection = DatabaseConnection.getInstance();
    }

    public int createTransaction(Transaction transaction) throws DatabaseException {
        String query = "INSERT INTO transactions (account_id, transaction_type, amount, balance_after, description, related_account_id) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, transaction.getAccountId());
            stmt.setString(2, transaction.getTransactionType());
            stmt.setBigDecimal(3, transaction.getAmount());
            stmt.setBigDecimal(4, transaction.getBalanceAfter());
            stmt.setString(5, transaction.getDescription());

            if (transaction.getRelatedAccountId() != null) {
                stmt.setInt(6, transaction.getRelatedAccountId());
            } else {
                stmt.setNull(6, Types.INTEGER);
            }

            stmt.executeUpdate();
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            throw new DatabaseException("Error creating transaction: " + e.getMessage(), e);
        }
        return -1;
    }

    public Transaction getById(int transactionId) throws DatabaseException {
        String query = "SELECT * FROM transactions WHERE transaction_id = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, transactionId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToTransaction(rs);
            }

        } catch (SQLException e) {
            throw new DatabaseException("Error retrieving transaction: " + e.getMessage(), e);
        }
        return null;
    }

    public List<Transaction> getByAccountId(int accountId) throws DatabaseException {
        List<Transaction> transactions = new ArrayList<>();
        String query = "SELECT * FROM transactions WHERE account_id = ? ORDER BY created_at DESC";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, accountId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                transactions.add(mapResultSetToTransaction(rs));
            }

        } catch (SQLException e) {
            throw new DatabaseException("Error retrieving transactions: " + e.getMessage(), e);
        }
        return transactions;
    }

    public List<Transaction> getByAccountIdPaginated(int accountId, int offset, int limit) throws DatabaseException {
        List<Transaction> transactions = new ArrayList<>();
        String query = "SELECT * FROM transactions WHERE account_id = ? ORDER BY created_at DESC LIMIT ? OFFSET ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, accountId);
            stmt.setInt(2, limit);
            stmt.setInt(3, offset);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                transactions.add(mapResultSetToTransaction(rs));
            }

        } catch (SQLException e) {
            throw new DatabaseException("Error retrieving transactions: " + e.getMessage(), e);
        }
        return transactions;
    }

    public List<Transaction> getAllTransactions() throws DatabaseException {
        List<Transaction> transactions = new ArrayList<>();
        String query = "SELECT * FROM transactions ORDER BY created_at DESC";

        try (Connection conn = dbConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                transactions.add(mapResultSetToTransaction(rs));
            }

        } catch (SQLException e) {
            throw new DatabaseException("Error retrieving transactions: " + e.getMessage(), e);
        }
        return transactions;
    }

    public long getTransactionCount(int accountId) throws DatabaseException {
        String query = "SELECT COUNT(*) FROM transactions WHERE account_id = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, accountId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getLong(1);
            }

        } catch (SQLException e) {
            throw new DatabaseException("Error counting transactions: " + e.getMessage(), e);
        }
        return 0;
    }

    private Transaction mapResultSetToTransaction(ResultSet rs) throws SQLException {
        Transaction transaction = new Transaction();
        transaction.setTransactionId(rs.getInt("transaction_id"));
        transaction.setAccountId(rs.getInt("account_id"));
        transaction.setTransactionType(rs.getString("transaction_type"));
        transaction.setAmount(rs.getBigDecimal("amount"));
        transaction.setBalanceAfter(rs.getBigDecimal("balance_after"));
        transaction.setDescription(rs.getString("description"));

        int relatedId = rs.getInt("related_account_id");
        if (!rs.wasNull()) {
            transaction.setRelatedAccountId(relatedId);
        }

        Timestamp createdTs = rs.getTimestamp("created_at");
        transaction.setCreatedAt(createdTs != null ? createdTs.toLocalDateTime() : null);
        return transaction;
    }

}
