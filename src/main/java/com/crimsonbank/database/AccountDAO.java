package com.crimsonbank.database;

import com.crimsonbank.exceptions.DatabaseException;
import com.crimsonbank.models.Account;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AccountDAO {

    private final DatabaseConnection dbConnection;

    public AccountDAO() throws DatabaseException {
        this.dbConnection = DatabaseConnection.getInstance();
    }

    public int createAccount(Account account) throws DatabaseException {
        String query = "INSERT INTO accounts (customer_id, account_number, account_type, balance, status) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, account.getCustomerId());
            stmt.setString(2, account.getAccountNumber());
            stmt.setString(3, account.getAccountType());
            stmt.setBigDecimal(4, account.getBalance());
            stmt.setString(5, account.getStatus());

            stmt.executeUpdate();
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            throw new DatabaseException("Error creating account: " + e.getMessage(), e);
        }
        return -1;
    }

    public Account getById(int accountId) throws DatabaseException {
        String query = "SELECT * FROM accounts WHERE account_id = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, accountId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToAccount(rs);
            }

        } catch (SQLException e) {
            throw new DatabaseException("Error retrieving account: " + e.getMessage(), e);
        }
        return null;
    }

    public Account getByAccountNumber(String accountNumber) throws DatabaseException {
        String query = "SELECT * FROM accounts WHERE account_number = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, accountNumber);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToAccount(rs);
            }

        } catch (SQLException e) {
            throw new DatabaseException("Error retrieving account: " + e.getMessage(), e);
        }
        return null;
    }

    public List<Account> getByCustomerId(int customerId) throws DatabaseException {
        List<Account> accounts = new ArrayList<>();
        String query = "SELECT * FROM accounts WHERE customer_id = ? ORDER BY created_at";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, customerId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                accounts.add(mapResultSetToAccount(rs));
            }

        } catch (SQLException e) {
            throw new DatabaseException("Error retrieving accounts: " + e.getMessage(), e);
        }
        return accounts;
    }

    public List<Account> getAllAccounts() throws DatabaseException {
        List<Account> accounts = new ArrayList<>();
        String query = "SELECT * FROM accounts ORDER BY account_id";

        try (Connection conn = dbConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                accounts.add(mapResultSetToAccount(rs));
            }

        } catch (SQLException e) {
            throw new DatabaseException("Error retrieving accounts: " + e.getMessage(), e);
        }
        return accounts;
    }

    public boolean updateAccount(Account account) throws DatabaseException {
        String query = "UPDATE accounts SET account_type = ?, balance = ?, status = ?, updated_at = NOW() WHERE account_id = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, account.getAccountType());
            stmt.setBigDecimal(2, account.getBalance());
            stmt.setString(3, account.getStatus());
            stmt.setInt(4, account.getAccountId());

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            throw new DatabaseException("Error updating account: " + e.getMessage(), e);
        }
    }

    public boolean updateBalance(int accountId, BigDecimal newBalance) throws DatabaseException {
        String query = "UPDATE accounts SET balance = ?, updated_at = NOW() WHERE account_id = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setBigDecimal(1, newBalance);
            stmt.setInt(2, accountId);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            throw new DatabaseException("Error updating balance: " + e.getMessage(), e);
        }
    }

    public boolean deleteAccount(int accountId) throws DatabaseException {
        String query = "DELETE FROM accounts WHERE account_id = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, accountId);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            throw new DatabaseException("Error deleting account: " + e.getMessage(), e);
        }
    }

    private Account mapResultSetToAccount(ResultSet rs) throws SQLException {
        Account account = new Account();
        account.setAccountId(rs.getInt("account_id"));
        account.setCustomerId(rs.getInt("customer_id"));
        account.setAccountNumber(rs.getString("account_number"));
        account.setAccountType(rs.getString("account_type"));
        account.setBalance(rs.getBigDecimal("balance"));
        account.setStatus(rs.getString("status"));
        Timestamp createdTs = rs.getTimestamp("created_at");
        account.setCreatedAt(createdTs != null ? createdTs.toLocalDateTime() : null);
        Timestamp updatedTs = rs.getTimestamp("updated_at");
        account.setUpdatedAt(updatedTs != null ? updatedTs.toLocalDateTime() : null);
        return account;
    }

}
