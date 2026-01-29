package com.crimsonbank.services;

import com.crimsonbank.database.AccountDAO;
import com.crimsonbank.database.TransactionDAO;
import com.crimsonbank.database.AuditLogDAO;
import com.crimsonbank.exceptions.DatabaseException;
import com.crimsonbank.exceptions.InsufficientFundsException;
import com.crimsonbank.models.Account;
import com.crimsonbank.models.Transaction;
import com.crimsonbank.models.AuditLog;

import java.math.BigDecimal;

public class TransactionService {

    private final AccountDAO accountDAO;
    private final TransactionDAO transactionDAO;
    private final AuditLogDAO auditLogDAO;

    public TransactionService() throws DatabaseException {
        this.accountDAO = new AccountDAO();
        this.transactionDAO = new TransactionDAO();
        this.auditLogDAO = new AuditLogDAO();
    }

    public void deposit(int accountId, BigDecimal amount, String description, Integer staffId)
            throws DatabaseException, InsufficientFundsException {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InsufficientFundsException("Deposit amount must be positive");
        }

        Account account = accountDAO.getById(accountId);
        if (account == null) {
            throw new DatabaseException("Account not found");
        }

        BigDecimal newBalance = account.getBalance().add(amount);
        account.setBalance(newBalance);
        accountDAO.updateAccount(account);

        Transaction transaction = new Transaction(
            accountId, "DEPOSIT", amount, newBalance, description
        );
        int transactionId = transactionDAO.createTransaction(transaction);

        AuditLog log = new AuditLog(
            "DEPOSIT",
            "Deposit of " + amount + " to account " + account.getAccountNumber(),
            staffId
        );
        log.setAccountId(accountId);
        log.setTransactionId(transactionId);
        log.setStaffId(staffId);
        auditLogDAO.createLog(log);
    }

    public void withdraw(int accountId, BigDecimal amount, String description, Integer staffId)
            throws DatabaseException, InsufficientFundsException {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InsufficientFundsException("Withdrawal amount must be positive");
        }

        Account account = accountDAO.getById(accountId);
        if (account == null) {
            throw new DatabaseException("Account not found");
        }

        if (account.getBalance().compareTo(amount) < 0) {
            throw new InsufficientFundsException("Insufficient funds for withdrawal");
        }

        BigDecimal newBalance = account.getBalance().subtract(amount);
        account.setBalance(newBalance);
        accountDAO.updateAccount(account);

        Transaction transaction = new Transaction(
            accountId, "WITHDRAWAL", amount, newBalance, description
        );
        int transactionId = transactionDAO.createTransaction(transaction);

        AuditLog log = new AuditLog(
            "WITHDRAWAL",
            "Withdrawal of " + amount + " from account " + account.getAccountNumber(),
            staffId
        );
        log.setAccountId(accountId);
        log.setTransactionId(transactionId);
        log.setStaffId(staffId);
        auditLogDAO.createLog(log);
    }

    public void transfer(int fromAccountId, int toAccountId, BigDecimal amount, Integer staffId)
            throws DatabaseException, InsufficientFundsException {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InsufficientFundsException("Transfer amount must be positive");
        }

        Account fromAccount = accountDAO.getById(fromAccountId);
        Account toAccount = accountDAO.getById(toAccountId);

        if (fromAccount == null || toAccount == null) {
            throw new DatabaseException("One or both accounts not found");
        }

        if (!fromAccount.isActive() || !toAccount.isActive()) {
            throw new DatabaseException("One or both accounts are inactive");
        }

        if (fromAccount.getBalance().compareTo(amount) < 0) {
            throw new InsufficientFundsException("Insufficient funds for transfer");
        }

        BigDecimal fromNewBalance = fromAccount.getBalance().subtract(amount);
        BigDecimal toNewBalance = toAccount.getBalance().add(amount);

        fromAccount.setBalance(fromNewBalance);
        toAccount.setBalance(toNewBalance);

        accountDAO.updateAccount(fromAccount);
        accountDAO.updateAccount(toAccount);

        Transaction fromTransaction = new Transaction(
            fromAccountId,
            "TRANSFER",
            amount,
            fromNewBalance,
            "Transfer to " + toAccount.getAccountNumber()
        );
        fromTransaction.setRelatedAccountId(toAccountId);
        int transactionId1 = transactionDAO.createTransaction(fromTransaction);

        Transaction toTransaction = new Transaction(
            toAccountId,
            "TRANSFER",
            amount,
            toNewBalance,
            "Transfer from " + fromAccount.getAccountNumber()
        );
        toTransaction.setRelatedAccountId(fromAccountId);
        int transactionId2 = transactionDAO.createTransaction(toTransaction);

        AuditLog log = new AuditLog(
            "TRANSFER",
            "Transfer of " + amount + " from " + fromAccount.getAccountNumber()
                + " to " + toAccount.getAccountNumber(),
            staffId
        );
        log.setAccountId(fromAccountId);
        log.setTransactionId(transactionId1);
        log.setStaffId(staffId);
        auditLogDAO.createLog(log);
    }

}
