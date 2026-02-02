package com.crimsonbank.database;

import com.crimsonbank.exceptions.DatabaseException;
import com.crimsonbank.models.StaffMember;
import com.crimsonbank.utils.PasswordUtil;

import java.sql.*;
import java.time.LocalDateTime;

public class StaffDAO {

    private final DatabaseConnection dbConnection;

    public StaffDAO() throws DatabaseException {
        this.dbConnection = DatabaseConnection.getInstance();
    }

    public boolean authenticate(String username, String password) throws DatabaseException {
        String query = "SELECT password FROM staff WHERE username = ? AND is_active = TRUE";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String hashedPassword = rs.getString("password");
                return PasswordUtil.verifyPassword(password, hashedPassword);
            }
            return false;

        } catch (SQLException e) {
            throw new DatabaseException("Authentication failed: " + e.getMessage(), e);
        }
    }

    public StaffMember getByUsername(String username) throws DatabaseException {
        String query = "SELECT * FROM staff WHERE username = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToStaff(rs);
            }

        } catch (SQLException e) {
            throw new DatabaseException("Error retrieving staff: " + e.getMessage(), e);
        }
        return null;
    }

    public StaffMember getById(int staffId) throws DatabaseException {
        String query = "SELECT * FROM staff WHERE staff_id = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, staffId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToStaff(rs);
            }

        } catch (SQLException e) {
            throw new DatabaseException("Error retrieving staff: " + e.getMessage(), e);
        }
        return null;
    }

    public boolean createStaff(StaffMember staff) throws DatabaseException {
        String query = "INSERT INTO staff (email, username, password, full_name, role, profile_image, is_active) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, staff.getEmail());
            stmt.setString(2, staff.getUsername());
            stmt.setString(3, PasswordUtil.hashPassword(staff.getPassword()));
            stmt.setString(4, staff.getFullName());
            stmt.setString(5, staff.getRole());
            stmt.setString(6, staff.getProfileImage());
            stmt.setBoolean(7, staff.isActive());

            stmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            throw new DatabaseException("Error creating staff: " + e.getMessage(), e);
        }
    }

    public boolean updateStaff(StaffMember staff) throws DatabaseException {
        String query = "UPDATE staff SET email = ?, password = ?, full_name = ?, role = ?, profile_image = ?, is_active = ?, updated_at = NOW() WHERE staff_id = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, staff.getEmail());
            // Hash the password before updating
            stmt.setString(2, PasswordUtil.hashPassword(staff.getPassword()));
            stmt.setString(3, staff.getFullName());
            stmt.setString(4, staff.getRole());
            stmt.setString(5, staff.getProfileImage());
            stmt.setBoolean(6, staff.isActive());
            stmt.setInt(7, staff.getStaffId());

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            throw new DatabaseException("Error updating staff: " + e.getMessage(), e);
        }
    }

    public boolean updateStaffProfileImage(int staffId, String profileImagePath) throws DatabaseException {
        String query = "UPDATE staff SET profile_image = ?, updated_at = NOW() WHERE staff_id = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, profileImagePath);
            stmt.setInt(2, staffId);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            throw new DatabaseException("Error updating profile image: " + e.getMessage(), e);
        }
    }

    private StaffMember mapResultSetToStaff(ResultSet rs) throws SQLException {
        StaffMember staff = new StaffMember();
        staff.setStaffId(rs.getInt("staff_id"));
        staff.setEmail(rs.getString("email"));
        staff.setUsername(rs.getString("username"));
        staff.setPassword(rs.getString("password"));
        staff.setFullName(rs.getString("full_name"));
        staff.setRole(rs.getString("role"));
        staff.setProfileImage(rs.getString("profile_image"));
        staff.setActive(rs.getBoolean("is_active"));
        Timestamp createdTs = rs.getTimestamp("created_at");
        staff.setCreatedAt(createdTs != null ? createdTs.toLocalDateTime() : null);
        Timestamp updatedTs = rs.getTimestamp("updated_at");
        staff.setUpdatedAt(updatedTs != null ? updatedTs.toLocalDateTime() : null);
        return staff;
    }

}
