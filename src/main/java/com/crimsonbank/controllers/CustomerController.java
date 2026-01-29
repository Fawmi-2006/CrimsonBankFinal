package com.crimsonbank.controllers;

import com.crimsonbank.database.CustomerDAO;
import com.crimsonbank.exceptions.DatabaseException;
import com.crimsonbank.models.Customer;
import com.crimsonbank.utils.InputValidator;
import com.crimsonbank.utils.NavigationUtil;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DatePicker;
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

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CustomerController {

    @FXML
    private TextField searchField;

    @FXML
    private TableView<Customer> customerTable;

    @FXML
    private TableColumn<Customer, Integer> idColumn;

    @FXML
    private TableColumn<Customer, String> nameColumn;

    @FXML
    private TableColumn<Customer, String> nicColumn;

    @FXML
    private TableColumn<Customer, String> emailColumn;

    @FXML
    private TableColumn<Customer, String> phoneColumn;

    @FXML
    private TableColumn<Customer, String> cityColumn;

    @FXML
    private TableColumn<Customer, Void> actionsColumn;

    @FXML
    private Button backButton;

    private CustomerDAO customerDAO;
    private final ObservableList<Customer> customers = FXCollections.observableArrayList();

    @FXML
    private void handleBackToDashboard() {
        NavigationUtil.navigate(backButton, "/com/crimsonbank/views/dashboard.fxml", "CrimsonBank - Dashboard");
    }

    @FXML
    private void handleSearch() {
        String term = InputValidator.sanitizeInput(searchField.getText());
        if (term.isEmpty()) {
            loadAllCustomers();
            return;
        }
        try {
            List<Customer> results = new ArrayList<>();
            if (term.matches("\\d+")) {
                Customer customer = customerDAO.getById(Integer.parseInt(term));
                if (customer != null) {
                    results.add(customer);
                }
            } else if (InputValidator.isValidNIC(term)) {
                Customer customer = customerDAO.getByNIC(term);
                if (customer != null) {
                    results.add(customer);
                }
            } else {
                results = customerDAO.searchByName(term);
            }
            customers.setAll(results);
            customerTable.setItems(customers);
            if (results.isEmpty()) {
                showInfo("No customers found for the search criteria.");
            }
        } catch (DatabaseException e) {
            showError("Failed to search customers: " + e.getMessage());
        }
    }

    @FXML
    private void handleAddNew() {
        Customer customer = showCustomerDialog(null);
        if (customer == null) {
            return;
        }
        try {
            int id = customerDAO.createCustomer(customer);
            if (id > 0) {
                loadAllCustomers();
                showInfo("Customer created successfully.");
            }
        } catch (DatabaseException e) {
            showError("Failed to create customer: " + e.getMessage());
        }
    }

    @FXML
    public void initialize() {
        try {
            customerDAO = new CustomerDAO();
            configureColumns();
            loadAllCustomers();
        } catch (DatabaseException e) {
            showError("Failed to initialize customer data: " + e.getMessage());
        }
    }

    private void configureColumns() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("customerId"));
        nameColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getFullName()));
        nicColumn.setCellValueFactory(new PropertyValueFactory<>("nic"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
        cityColumn.setCellValueFactory(new PropertyValueFactory<>("city"));
        actionsColumn.setCellFactory(col -> new TableCell<>() {
            private final Button editButton = new Button("Edit");
            private final Button deleteButton = new Button("Delete");
            private final HBox container = new HBox(8, editButton, deleteButton);

            {
                editButton.setOnAction(event -> {
                    Customer customer = getTableView().getItems().get(getIndex());
                    handleEdit(customer);
                });
                deleteButton.setOnAction(event -> {
                    Customer customer = getTableView().getItems().get(getIndex());
                    handleDelete(customer);
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
    }

    private void loadAllCustomers() {
        try {
            customers.setAll(customerDAO.getAllCustomers());
            customerTable.setItems(customers);
        } catch (DatabaseException e) {
            showError("Failed to load customers: " + e.getMessage());
        }
    }

    private void handleEdit(Customer existing) {
        Customer updated = showCustomerDialog(existing);
        if (updated == null) {
            return;
        }
        updated.setCustomerId(existing.getCustomerId());
        try {
            boolean success = customerDAO.updateCustomer(updated);
            if (success) {
                loadAllCustomers();
                showInfo("Customer updated successfully.");
            }
        } catch (DatabaseException e) {
            showError("Failed to update customer: " + e.getMessage());
        }
    }

    private void handleDelete(Customer customer) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Delete Customer");
        confirm.setHeaderText("Delete customer " + customer.getFullName() + "?");
        confirm.setContentText("This action cannot be undone.");
        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isEmpty() || result.get() != ButtonType.OK) {
            return;
        }
        try {
            boolean success = customerDAO.deleteCustomer(customer.getCustomerId());
            if (success) {
                loadAllCustomers();
                showInfo("Customer deleted successfully.");
            }
        } catch (DatabaseException e) {
            showError("Failed to delete customer: " + e.getMessage());
        }
    }

    private Customer showCustomerDialog(Customer existing) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle(existing == null ? "Add Customer" : "Edit Customer");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        TextField firstNameField = new TextField();
        TextField lastNameField = new TextField();
        TextField nicField = new TextField();
        TextField emailField = new TextField();
        TextField phoneField = new TextField();
        TextField addressField = new TextField();
        TextField cityField = new TextField();
        TextField postalField = new TextField();
        DatePicker dobPicker = new DatePicker();

        if (existing != null) {
            firstNameField.setText(existing.getFirstName());
            lastNameField.setText(existing.getLastName());
            nicField.setText(existing.getNic());
            emailField.setText(existing.getEmail());
            phoneField.setText(existing.getPhoneNumber());
            addressField.setText(existing.getAddress());
            cityField.setText(existing.getCity());
            postalField.setText(existing.getPostalCode());
            dobPicker.setValue(existing.getDateOfBirth());
            nicField.setDisable(true);
            dobPicker.setDisable(true);
        }

        grid.addRow(0, new Label("First Name"), firstNameField);
        grid.addRow(1, new Label("Last Name"), lastNameField);
        grid.addRow(2, new Label("NIC"), nicField);
        grid.addRow(3, new Label("Email"), emailField);
        grid.addRow(4, new Label("Phone"), phoneField);
        grid.addRow(5, new Label("Address"), addressField);
        grid.addRow(6, new Label("City"), cityField);
        grid.addRow(7, new Label("Postal Code"), postalField);
        grid.addRow(8, new Label("Date of Birth"), dobPicker);
        GridPane.setHgrow(firstNameField, Priority.ALWAYS);
        GridPane.setHgrow(lastNameField, Priority.ALWAYS);
        GridPane.setHgrow(nicField, Priority.ALWAYS);
        GridPane.setHgrow(emailField, Priority.ALWAYS);
        GridPane.setHgrow(phoneField, Priority.ALWAYS);
        GridPane.setHgrow(addressField, Priority.ALWAYS);
        GridPane.setHgrow(cityField, Priority.ALWAYS);
        GridPane.setHgrow(postalField, Priority.ALWAYS);
        GridPane.setHgrow(dobPicker, Priority.ALWAYS);

        dialog.getDialogPane().setContent(grid);

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isEmpty() || result.get() != ButtonType.OK) {
            return null;
        }

        String firstName = InputValidator.sanitizeInput(firstNameField.getText());
        String lastName = InputValidator.sanitizeInput(lastNameField.getText());
        String nic = InputValidator.sanitizeInput(nicField.getText());
        String email = InputValidator.sanitizeInput(emailField.getText());
        String phone = InputValidator.sanitizeInput(phoneField.getText());
        String address = InputValidator.sanitizeInput(addressField.getText());
        String city = InputValidator.sanitizeInput(cityField.getText());
        String postal = InputValidator.sanitizeInput(postalField.getText());
        LocalDate dob = dobPicker.getValue();

        if (firstName.isEmpty() || lastName.isEmpty() || nic.isEmpty() || email.isEmpty() || phone.isEmpty() || address.isEmpty() || city.isEmpty() || postal.isEmpty() || dob == null) {
            showError("All fields are required.");
            return null;
        }
        if (!InputValidator.isValidNIC(nic)) {
            showError("Invalid NIC format.");
            return null;
        }
        if (!InputValidator.isValidEmail(email)) {
            showError("Invalid email format.");
            return null;
        }
        if (!InputValidator.isValidPhoneNumber(phone)) {
            showError("Invalid phone number format.");
            return null;
        }

        return new Customer(firstName, lastName, nic, email, phone, address, city, postal, dob);
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
