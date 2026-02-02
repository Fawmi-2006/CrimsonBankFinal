package com.crimsonbank.database;

import com.crimsonbank.exceptions.DatabaseException;
import com.crimsonbank.models.AuditLog;
import com.crimsonbank.models.Loan;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LoanDAO {

    private final DatabaseConnection dbConnection;

    public LoanDAO() throws DatabaseException {
        this.dbConnection = DatabaseConnection.getInstance();
    }

    public int createLoan(Loan loan) throws DatabaseException {
        String query = "INSERT INTO loans (customer_id, account_id, loan_amount, interest_rate, tenure_months, status) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, loan.getCustomerId());
            stmt.setInt(2, loan.getAccountId());
            stmt.setBigDecimal(3, loan.getLoanAmount());
            stmt.setBigDecimal(4, loan.getInterestRate());
            stmt.setInt(5, loan.getTenureMonths());
            stmt.setString(6, loan.getStatus());

            stmt.executeUpdate();
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            throw new DatabaseException("Error creating loan: " + e.getMessage(), e);
        }
        return -1;
    }

    public Loan getById(int loanId) throws DatabaseException {
        String query = "SELECT * FROM loans WHERE loan_id = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, loanId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToLoan(rs);
            }

        } catch (SQLException e) {
            throw new DatabaseException("Error retrieving loan: " + e.getMessage(), e);
        }
        return null;
    }

    public List<Loan> getByCustomerId(int customerId) throws DatabaseException {
        List<Loan> loans = new ArrayList<>();
        String query = "SELECT * FROM loans WHERE customer_id = ? ORDER BY created_at DESC";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, customerId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                loans.add(mapResultSetToLoan(rs));
            }

        } catch (SQLException e) {
            throw new DatabaseException("Error retrieving loans: " + e.getMessage(), e);
        }
        return loans;
    }

    public List<Loan> getPendingLoans() throws DatabaseException {
        List<Loan> loans = new ArrayList<>();
        String query = "SELECT * FROM loans WHERE status = 'PENDING' ORDER BY created_at ASC";

        try (Connection conn = dbConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                loans.add(mapResultSetToLoan(rs));
            }

        } catch (SQLException e) {
            throw new DatabaseException("Error retrieving pending loans: " + e.getMessage(), e);
        }
        return loans;
    }

    public List<Loan> getAllLoans() throws DatabaseException {
        List<Loan> loans = new ArrayList<>();
        String query = "SELECT * FROM loans ORDER BY created_at DESC";

        try (Connection conn = dbConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                loans.add(mapResultSetToLoan(rs));
            }

        } catch (SQLException e) {
            throw new DatabaseException("Error retrieving loans: " + e.getMessage(), e);
        }
        return loans;
    }

    public boolean approveLoan(int loanId, int staffId) throws DatabaseException {
        String query = "UPDATE loans SET status = 'APPROVED', approved_by = ?, approval_date = NOW(), updated_at = NOW() WHERE loan_id = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, staffId);
            stmt.setInt(2, loanId);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                AuditLog auditLog = new AuditLog(
                    "LOAN_APPROVED",
                    "Loan approved - Loan ID: " + loanId,
                    staffId
                );
                auditLog.setLoanId(loanId);
                AuditLogDAO auditLogDAO = new AuditLogDAO();
                auditLogDAO.createLog(auditLog);
                return true;
            }
            return false;

        } catch (SQLException e) {
            throw new DatabaseException("Error approving loan: " + e.getMessage(), e);
        }
    }

    public boolean rejectLoan(int loanId, int staffId) throws DatabaseException {
        String query = "UPDATE loans SET status = 'REJECTED', approved_by = ?, approval_date = NOW(), updated_at = NOW() WHERE loan_id = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, staffId);
            stmt.setInt(2, loanId);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                AuditLog auditLog = new AuditLog(
                    "LOAN_REJECTED",
                    "Loan rejected - Loan ID: " + loanId,
                    staffId
                );
                auditLog.setLoanId(loanId);
                AuditLogDAO auditLogDAO = new AuditLogDAO();
                auditLogDAO.createLog(auditLog);
                return true;
            }
            return false;

        } catch (SQLException e) {
            throw new DatabaseException("Error rejecting loan: " + e.getMessage(), e);
        }
    }

    public boolean updateLoan(Loan loan) throws DatabaseException {
        String query = "UPDATE loans SET loan_amount = ?, interest_rate = ?, tenure_months = ?, status = ?, updated_at = NOW() WHERE loan_id = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setBigDecimal(1, loan.getLoanAmount());
            stmt.setBigDecimal(2, loan.getInterestRate());
            stmt.setInt(3, loan.getTenureMonths());
            stmt.setString(4, loan.getStatus());
            stmt.setInt(5, loan.getLoanId());

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            throw new DatabaseException("Error updating loan: " + e.getMessage(), e);
        }
    }

    private Loan mapResultSetToLoan(ResultSet rs) throws SQLException {
        Loan loan = new Loan();
        loan.setLoanId(rs.getInt("loan_id"));
        loan.setCustomerId(rs.getInt("customer_id"));
        loan.setAccountId(rs.getInt("account_id"));
        loan.setLoanAmount(rs.getBigDecimal("loan_amount"));
        loan.setInterestRate(rs.getBigDecimal("interest_rate"));
        loan.setTenureMonths(rs.getInt("tenure_months"));
        loan.setStatus(rs.getString("status"));

        int approvedBy = rs.getInt("approved_by");
        if (!rs.wasNull()) {
            loan.setApprovedBy(approvedBy);
        }

        Timestamp approvalDate = rs.getTimestamp("approval_date");
        if (approvalDate != null) {
            loan.setApprovalDate(approvalDate.toLocalDateTime());
        }

        Timestamp createdTs = rs.getTimestamp("created_at");
        loan.setCreatedAt(createdTs != null ? createdTs.toLocalDateTime() : null);
        Timestamp updatedTs = rs.getTimestamp("updated_at");
        loan.setUpdatedAt(updatedTs != null ? updatedTs.toLocalDateTime() : null);
        return loan;
    }

}
