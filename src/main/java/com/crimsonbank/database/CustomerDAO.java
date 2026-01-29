package com.crimsonbank.database;

import com.crimsonbank.exceptions.DatabaseException;
import com.crimsonbank.models.Customer;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CustomerDAO {

    private final DatabaseConnection dbConnection;

    public CustomerDAO() throws DatabaseException {
        this.dbConnection = DatabaseConnection.getInstance();
    }

    public int createCustomer(Customer customer) throws DatabaseException {
        String query = "INSERT INTO customers (first_name, last_name, nic, email, phone_number, address, city, postal_code, date_of_birth) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, customer.getFirstName());
            stmt.setString(2, customer.getLastName());
            stmt.setString(3, customer.getNic());
            stmt.setString(4, customer.getEmail());
            stmt.setString(5, customer.getPhoneNumber());
            stmt.setString(6, customer.getAddress());
            stmt.setString(7, customer.getCity());
            stmt.setString(8, customer.getPostalCode());
            stmt.setDate(9, Date.valueOf(customer.getDateOfBirth()));

            stmt.executeUpdate();
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            throw new DatabaseException("Error creating customer: " + e.getMessage(), e);
        }
        return -1;
    }

    public Customer getById(int customerId) throws DatabaseException {
        String query = "SELECT * FROM customers WHERE customer_id = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, customerId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToCustomer(rs);
            }

        } catch (SQLException e) {
            throw new DatabaseException("Error retrieving customer: " + e.getMessage(), e);
        }
        return null;
    }

    public Customer getByNIC(String nic) throws DatabaseException {
        String query = "SELECT * FROM customers WHERE nic = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, nic);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToCustomer(rs);
            }

        } catch (SQLException e) {
            throw new DatabaseException("Error retrieving customer: " + e.getMessage(), e);
        }
        return null;
    }

    public List<Customer> getAllCustomers() throws DatabaseException {
        List<Customer> customers = new ArrayList<>();
        String query = "SELECT * FROM customers ORDER BY customer_id";

        try (Connection conn = dbConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                customers.add(mapResultSetToCustomer(rs));
            }

        } catch (SQLException e) {
            throw new DatabaseException("Error retrieving customers: " + e.getMessage(), e);
        }
        return customers;
    }

    public List<Customer> searchByName(String name) throws DatabaseException {
        List<Customer> customers = new ArrayList<>();
        String query = "SELECT * FROM customers WHERE first_name LIKE ? OR last_name LIKE ? ORDER BY first_name";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            String searchTerm = "%" + name + "%";
            stmt.setString(1, searchTerm);
            stmt.setString(2, searchTerm);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                customers.add(mapResultSetToCustomer(rs));
            }

        } catch (SQLException e) {
            throw new DatabaseException("Error searching customers: " + e.getMessage(), e);
        }
        return customers;
    }

    public boolean updateCustomer(Customer customer) throws DatabaseException {
        String query = "UPDATE customers SET first_name = ?, last_name = ?, email = ?, phone_number = ?, address = ?, city = ?, postal_code = ?, updated_at = NOW() WHERE customer_id = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, customer.getFirstName());
            stmt.setString(2, customer.getLastName());
            stmt.setString(3, customer.getEmail());
            stmt.setString(4, customer.getPhoneNumber());
            stmt.setString(5, customer.getAddress());
            stmt.setString(6, customer.getCity());
            stmt.setString(7, customer.getPostalCode());
            stmt.setInt(8, customer.getCustomerId());

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            throw new DatabaseException("Error updating customer: " + e.getMessage(), e);
        }
    }

    public boolean deleteCustomer(int customerId) throws DatabaseException {
        String query = "DELETE FROM customers WHERE customer_id = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, customerId);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            throw new DatabaseException("Error deleting customer: " + e.getMessage(), e);
        }
    }

    private Customer mapResultSetToCustomer(ResultSet rs) throws SQLException {
        Customer customer = new Customer();
        customer.setCustomerId(rs.getInt("customer_id"));
        customer.setFirstName(rs.getString("first_name"));
        customer.setLastName(rs.getString("last_name"));
        customer.setNic(rs.getString("nic"));
        customer.setEmail(rs.getString("email"));
        customer.setPhoneNumber(rs.getString("phone_number"));
        customer.setAddress(rs.getString("address"));
        customer.setCity(rs.getString("city"));
        customer.setPostalCode(rs.getString("postal_code"));
        java.sql.Date dobDate = rs.getDate("date_of_birth");
        customer.setDateOfBirth(dobDate != null ? dobDate.toLocalDate() : null);
        Timestamp createdTs = rs.getTimestamp("created_at");
        customer.setCreatedAt(createdTs != null ? createdTs.toLocalDateTime() : null);
        Timestamp updatedTs = rs.getTimestamp("updated_at");
        customer.setUpdatedAt(updatedTs != null ? updatedTs.toLocalDateTime() : null);
        return customer;
    }

}
