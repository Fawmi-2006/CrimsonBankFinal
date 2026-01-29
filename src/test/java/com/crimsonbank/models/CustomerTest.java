package com.crimsonbank.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

public class CustomerTest {
    private Customer customer;

    @BeforeEach
    public void setUp() {
        customer = new Customer(
            "Rajesh",
            "Kumar",
            "123456789V",
            "rajesh@example.com",
            "+94701234567",
            "123 Main Street",
            "Colombo",
            "00100",
            LocalDate.of(1990, 5, 15)
        );
    }

    @Test
    public void testCustomerCreation() {
        assertNotNull(customer);
        assertEquals("Rajesh", customer.getFirstName());
        assertEquals("Kumar", customer.getLastName());
        assertEquals("123456789V", customer.getNic());
    }

    @Test
    public void testFullName() {
        assertEquals("Rajesh Kumar", customer.getFullName());
    }

    @Test
    public void testCustomerEmailUpdate() {
        String newEmail = "newemail@example.com";
        customer.setEmail(newEmail);
        assertEquals(newEmail, customer.getEmail());
    }

    @Test
    public void testCustomerAddressUpdate() {
        String newAddress = "456 Oak Lane";
        customer.setAddress(newAddress);
        assertEquals(newAddress, customer.getAddress());
    }
}
