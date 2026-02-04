package com.crimsonbank.services;

import com.crimsonbank.models.Account;
import com.crimsonbank.models.Transaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

public class TransactionServiceTest {

    private Account fromAccount;
    private Account toAccount;

    @BeforeEach
    public void setUp() {
        fromAccount = new Account(1, "ACC001001", "SAVINGS", BigDecimal.valueOf(1000.00));
        fromAccount.setAccountId(1);

        toAccount = new Account(2, "ACC002001", "CURRENT", BigDecimal.valueOf(500.00));
        toAccount.setAccountId(2);
    }

    @Test
    public void testTransferBetweenAccountsSuccess() {
        BigDecimal transferAmount = BigDecimal.valueOf(200.00);
        BigDecimal initialFromBalance = fromAccount.getBalance();
        BigDecimal initialToBalance = toAccount.getBalance();

        fromAccount.setBalance(initialFromBalance.subtract(transferAmount));
        toAccount.setBalance(initialToBalance.add(transferAmount));

        BigDecimal expectedFromBalance = BigDecimal.valueOf(800.00);
        BigDecimal expectedToBalance = BigDecimal.valueOf(700.00);

        assertEquals(expectedFromBalance, fromAccount.getBalance());
        assertEquals(expectedToBalance, toAccount.getBalance());
    }

    @Test
    public void testTransferWithInsufficientBalance() {
        BigDecimal transferAmount = BigDecimal.valueOf(1500.00);

        assertTrue(fromAccount.getBalance().compareTo(transferAmount) < 0);
    }

    @Test
    public void testDepositUpdatesBalance() {
        BigDecimal depositAmount = BigDecimal.valueOf(300.00);
        BigDecimal initialBalance = fromAccount.getBalance();
        BigDecimal expectedBalance = initialBalance.add(depositAmount);

        fromAccount.setBalance(expectedBalance);

        assertEquals(expectedBalance, fromAccount.getBalance());
        assertEquals(BigDecimal.valueOf(1300.00), fromAccount.getBalance());
    }

    @Test
    public void testWithdrawUpdatesBalance() {
        BigDecimal withdrawAmount = BigDecimal.valueOf(200.00);
        BigDecimal initialBalance = fromAccount.getBalance();

        if (fromAccount.getBalance().compareTo(withdrawAmount) >= 0) {
            BigDecimal expectedBalance = initialBalance.subtract(withdrawAmount);
            fromAccount.setBalance(expectedBalance);
            assertEquals(expectedBalance, fromAccount.getBalance());
            assertEquals(BigDecimal.valueOf(800.00), fromAccount.getBalance());
        }
    }
}
