package com.crimsonbank.models;

/**
 * Enumeration for loan status values
 */
public enum LoanStatus {
    PENDING("PENDING"),
    APPROVED("APPROVED"),
    REJECTED("REJECTED"),
    DISBURSED("DISBURSED"),
    CLOSED("CLOSED");

    private final String value;

    LoanStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static LoanStatus fromValue(String value) {
        for (LoanStatus status : LoanStatus.values()) {
            if (status.value.equalsIgnoreCase(value)) {
                return status;
            }
        }
        return PENDING;
    }

    @Override
    public String toString() {
        return value;
    }

}
