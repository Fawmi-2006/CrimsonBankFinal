package com.crimsonbank.utils;

import java.util.regex.Pattern;

public class InputValidator {

    private static final String EMAIL_REGEX = "^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$";
    private static final String NIC_REGEX = "^\\d{9}[vVxX]$";

    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);
    private static final Pattern NIC_PATTERN = Pattern.compile(NIC_REGEX);

    private InputValidator() {
    }

    public static boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }

    public static boolean isValidNIC(String nic) {
        return nic != null && NIC_PATTERN.matcher(nic).matches();
    }

    public static boolean isValidPassword(String password) {
        return password != null && password.length() >= 6;
    }

    public static boolean isValidPhoneNumber(String phone) {
        return phone != null && phone.matches("^\\+?94\\d{9}$|^0\\d{9}$");
    }

    public static boolean isValidAccountNumber(String accountNumber) {
        return accountNumber != null && accountNumber.matches("^ACC\\d{6}$");
    }

    public static boolean isPositiveAmount(double amount) {
        return amount > 0;
    }

    public static boolean isValidAccountType(String type) {
        return type != null && (type.equals("SAVINGS") || type.equals("CURRENT"));
    }

    public static String sanitizeInput(String input) {
        if (input == null) {
            return "";
        }
        return input.trim();
    }

}
