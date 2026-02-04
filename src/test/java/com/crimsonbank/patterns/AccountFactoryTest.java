package com.crimsonbank.patterns;

import com.crimsonbank.models.Account;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;

public class AccountFactoryTest {

    @Test
    public void testCurrentAccountFactoryCreation() {
        AccountFactory factory = new CurrentAccountFactory();
        Account account = factory.createAccount(1, "ACC001001", 1000.00);

        assertNotNull(account);
        assertEquals("CURRENT", account.getAccountType());
        assertEquals("ACC001001", account.getAccountNumber());
        assertEquals(BigDecimal.valueOf(1000.00), account.getBalance());
        assertEquals(1, account.getCustomerId());
    }

    @Test
    public void testSavingsAccountFactoryCreation() {
        AccountFactory factory = new SavingsAccountFactory();
        Account account = factory.createAccount(2, "ACC002001", 2000.00);

        assertNotNull(account);
        assertEquals("SAVINGS", account.getAccountType());
        assertEquals("ACC002001", account.getAccountNumber());
        assertEquals(BigDecimal.valueOf(2000.00), account.getBalance());
        assertEquals(2, account.getCustomerId());
    }

    @Test
    public void testFactoryPolymorphism() {
        AccountFactory currentFactory = new CurrentAccountFactory();
        AccountFactory savingsFactory = new SavingsAccountFactory();

        Account currentAccount = currentFactory.createAccount(1, "ACC001001", 500.00);
        Account savingsAccount = savingsFactory.createAccount(1, "ACC001002", 500.00);

        assertEquals("CURRENT", currentAccount.getAccountType());
        assertEquals("SAVINGS", savingsAccount.getAccountType());
    }

    @Test
    public void testFactoryWithZeroBalance() {
        AccountFactory factory = new CurrentAccountFactory();
        Account account = factory.createAccount(1, "ACC001003", 0.00);

        assertNotNull(account);
        assertEquals(BigDecimal.valueOf(0.00), account.getBalance());
    }
}
