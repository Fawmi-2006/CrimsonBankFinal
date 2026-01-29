package com.crimsonbank.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;

public class AccountTest {
    private Account account;

    @BeforeEach
    public void setUp() {
        account = new Account(1, "ACC001001", "SAVINGS", BigDecimal.valueOf(1000.00));
    }

    @Test
    public void testAccountCreation() {
        assertNotNull(account);
        assertEquals(1, account.getCustomerId());
        assertEquals("ACC001001", account.getAccountNumber());
        assertEquals("SAVINGS", account.getAccountType());
        assertEquals(BigDecimal.valueOf(1000.00), account.getBalance());
    }

    @Test
    public void testAccountStatus() {
        assertTrue(account.isActive());
        account.setStatus("INACTIVE");
        assertFalse(account.isActive());
    }

    @Test
    public void testBalanceUpdate() {
        BigDecimal newBalance = BigDecimal.valueOf(2000.00);
        account.setBalance(newBalance);
        assertEquals(newBalance, account.getBalance());
    }

    @Test
    public void testAccountTypeValidation() {
        assertEquals("SAVINGS", account.getAccountType());
        account.setAccountType("CURRENT");
        assertEquals("CURRENT", account.getAccountType());
    }
}
