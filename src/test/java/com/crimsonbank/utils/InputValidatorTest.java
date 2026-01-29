package com.crimsonbank.utils;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class InputValidatorTest {

    @Test
    public void testValidEmail() {
        assertTrue(InputValidator.isValidEmail("test@example.com"));
        assertTrue(InputValidator.isValidEmail("user.name+tag@example.co.uk"));
        assertFalse(InputValidator.isValidEmail("invalid.email@"));
        assertFalse(InputValidator.isValidEmail("@example.com"));
    }

    @Test
    public void testValidNIC() {
        assertTrue(InputValidator.isValidNIC("123456789V"));
        assertTrue(InputValidator.isValidNIC("123456789v"));
        assertTrue(InputValidator.isValidNIC("123456789X"));
        assertFalse(InputValidator.isValidNIC("12345678V"));
        assertFalse(InputValidator.isValidNIC("1234567890V"));
    }

    @Test
    public void testValidPassword() {
        assertTrue(InputValidator.isValidPassword("password123"));
        assertTrue(InputValidator.isValidPassword("123456"));
        assertFalse(InputValidator.isValidPassword("12345"));
        assertFalse(InputValidator.isValidPassword(""));
    }

    @Test
    public void testValidPhoneNumber() {
        assertTrue(InputValidator.isValidPhoneNumber("+94701234567"));
        assertTrue(InputValidator.isValidPhoneNumber("0701234567"));
        assertFalse(InputValidator.isValidPhoneNumber("123456"));
    }

    @Test
    public void testValidAccountNumber() {
        assertTrue(InputValidator.isValidAccountNumber("ACC001001"));
        assertTrue(InputValidator.isValidAccountNumber("ACC999999"));
        assertFalse(InputValidator.isValidAccountNumber("ACCA01001"));
    }

    @Test
    public void testPositiveAmount() {
        assertTrue(InputValidator.isPositiveAmount(100.0));
        assertTrue(InputValidator.isPositiveAmount(0.01));
        assertFalse(InputValidator.isPositiveAmount(0.0));
        assertFalse(InputValidator.isPositiveAmount(-100.0));
    }

    @Test
    public void testValidAccountType() {
        assertTrue(InputValidator.isValidAccountType("SAVINGS"));
        assertTrue(InputValidator.isValidAccountType("CURRENT"));
        assertFalse(InputValidator.isValidAccountType("BUSINESS"));
        assertFalse(InputValidator.isValidAccountType(""));
    }

    @Test
    public void testSanitizeInput() {
        assertEquals("test", InputValidator.sanitizeInput("  test  "));
        assertEquals("test", InputValidator.sanitizeInput("test"));
        assertEquals("", InputValidator.sanitizeInput(null));
    }
}
