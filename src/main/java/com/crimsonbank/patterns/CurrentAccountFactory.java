package com.crimsonbank.patterns;

import com.crimsonbank.models.Account;

import java.math.BigDecimal;

public class CurrentAccountFactory implements AccountFactory {

    @Override
    public Account createAccount(int customerId, String accountNumber, double initialBalance) {
        Account account = new Account(
            customerId,
            accountNumber,
            "CURRENT",
            BigDecimal.valueOf(initialBalance)
        );
        return account;
    }

}
