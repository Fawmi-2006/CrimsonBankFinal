package com.crimsonbank.database;

import com.crimsonbank.exceptions.DatabaseException;
import com.crimsonbank.models.AuditLog;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AuditLogDAO {

    private final DatabaseConnection dbConnection;

    public AuditLogDAO() throws DatabaseException {
        this.dbConnection = DatabaseConnection.getInstance();
    }

    public int createLog(AuditLog log) throws DatabaseException {
        String query = "INSERT INTO audit_log (action, description, staff_id, customer_id, account_id, transaction_id, loan_id) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, log.getAction());
            stmt.setString(2, log.getDescription());

            if (log.getStaffId() != null) {
                stmt.setInt(3, log.getStaffId());
            } else {
                stmt.setNull(3, Types.INTEGER);
            }

            if (log.getCustomerId() != null) {
                stmt.setInt(4, log.getCustomerId());
            } else {
                stmt.setNull(4, Types.INTEGER);
            }

            if (log.getAccountId() != null) {
                stmt.setInt(5, log.getAccountId());
            } else {
                stmt.setNull(5, Types.INTEGER);
            }

            if (log.getTransactionId() != null) {
                stmt.setInt(6, log.getTransactionId());
            } else {
                stmt.setNull(6, Types.INTEGER);
            }

            if (log.getLoanId() != null) {
                stmt.setInt(7, log.getLoanId());
            } else {
                stmt.setNull(7, Types.INTEGER);
            }

            stmt.executeUpdate();
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            throw new DatabaseException("Error creating audit log: " + e.getMessage(), e);
        }
        return -1;
    }

    public AuditLog getById(int logId) throws DatabaseException {
        String query = "SELECT * FROM audit_log WHERE log_id = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, logId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToAuditLog(rs);
            }

        } catch (SQLException e) {
            throw new DatabaseException("Error retrieving audit log: " + e.getMessage(), e);
        }
        return null;
    }

    public List<AuditLog> getRecentLogs(int limit) throws DatabaseException {
        List<AuditLog> logs = new ArrayList<>();
        String query = "SELECT * FROM audit_log ORDER BY created_at DESC LIMIT ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, limit);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                logs.add(mapResultSetToAuditLog(rs));
            }

        } catch (SQLException e) {
            throw new DatabaseException("Error retrieving recent logs: " + e.getMessage(), e);
        }
        return logs;
    }

    public List<AuditLog> getLogsByStaffId(int staffId) throws DatabaseException {
        List<AuditLog> logs = new ArrayList<>();
        String query = "SELECT * FROM audit_log WHERE staff_id = ? ORDER BY created_at DESC";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, staffId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                logs.add(mapResultSetToAuditLog(rs));
            }

        } catch (SQLException e) {
            throw new DatabaseException("Error retrieving audit logs: " + e.getMessage(), e);
        }
        return logs;
    }

    public List<AuditLog> getLogsByAction(String action) throws DatabaseException {
        List<AuditLog> logs = new ArrayList<>();
        String query = "SELECT * FROM audit_log WHERE action = ? ORDER BY created_at DESC";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, action);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                logs.add(mapResultSetToAuditLog(rs));
            }

        } catch (SQLException e) {
            throw new DatabaseException("Error retrieving audit logs: " + e.getMessage(), e);
        }
        return logs;
    }

    public List<AuditLog> getAllLogs() throws DatabaseException {
        List<AuditLog> logs = new ArrayList<>();
        String query = "SELECT * FROM audit_log ORDER BY created_at DESC";

        try (Connection conn = dbConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                logs.add(mapResultSetToAuditLog(rs));
            }

        } catch (SQLException e) {
            throw new DatabaseException("Error retrieving audit logs: " + e.getMessage(), e);
        }
        return logs;
    }

    private AuditLog mapResultSetToAuditLog(ResultSet rs) throws SQLException {
        AuditLog log = new AuditLog();
        log.setLogId(rs.getInt("log_id"));
        log.setAction(rs.getString("action"));
        log.setDescription(rs.getString("description"));

        int staffId = rs.getInt("staff_id");
        if (!rs.wasNull()) {
            log.setStaffId(staffId);
        }

        int customerId = rs.getInt("customer_id");
        if (!rs.wasNull()) {
            log.setCustomerId(customerId);
        }

        int accountId = rs.getInt("account_id");
        if (!rs.wasNull()) {
            log.setAccountId(accountId);
        }

        int transactionId = rs.getInt("transaction_id");
        if (!rs.wasNull()) {
            log.setTransactionId(transactionId);
        }

        int loanId = rs.getInt("loan_id");
        if (!rs.wasNull()) {
            log.setLoanId(loanId);
        }

        Timestamp createdTs = rs.getTimestamp("created_at");
        log.setCreatedAt(createdTs != null ? createdTs.toLocalDateTime() : null);
        return log;
    }

}
