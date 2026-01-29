package com.crimsonbank.utils;

import com.crimsonbank.models.Account;

import java.util.ArrayList;
import java.util.List;

public class SearchingAlgorithms {

    public static <T extends Comparable<T>> int binarySearch(List<T> list, T target) {
        return binarySearch(list, target, 0, list.size() - 1);
    }

    private static <T extends Comparable<T>> int binarySearch(List<T> list, T target, int low, int high) {
        if (low > high) {
            return -1;
        }

        int mid = low + (high - low) / 2;
        int comparison = list.get(mid).compareTo(target);

        if (comparison == 0) {
            return mid;
        } else if (comparison < 0) {
            return binarySearch(list, target, mid + 1, high);
        } else {
            return binarySearch(list, target, low, mid - 1);
        }
    }

    public static Account findAccountByNumber(List<Account> accounts, String accountNumber) {
        for (Account account : accounts) {
            if (account.getAccountNumber().equals(accountNumber)) {
                return account;
            }
        }
        return null;
    }

    public static Account findAccountById(List<Account> accounts, int accountId) {
        for (Account account : accounts) {
            if (account.getAccountId() == accountId) {
                return account;
            }
        }
        return null;
    }

    public static List<Account> searchAccountsByType(List<Account> accounts, String type) {
        List<Account> results = new ArrayList<>();
        for (Account account : accounts) {
            if (account.getAccountType().equals(type)) {
                results.add(account);
            }
        }
        return results;
    }

}
