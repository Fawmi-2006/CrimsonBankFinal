package com.crimsonbank.patterns;

import com.crimsonbank.models.Account;

public interface AccountFactory {

    Account createAccount(int customerId, String accountNumber, double initialBalance);

}
