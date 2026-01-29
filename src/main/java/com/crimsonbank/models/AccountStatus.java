package com.crimsonbank.models;

public enum AccountStatus {
    ACTIVE("ACTIVE"),
    INACTIVE("INACTIVE"),
    FROZEN("FROZEN"),
    SUSPENDED("SUSPENDED"),
    CLOSED("CLOSED");

    private final String value;

    AccountStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static AccountStatus fromValue(String value) {
        for (AccountStatus status : AccountStatus.values()) {
            if (status.value.equalsIgnoreCase(value)) {
                return status;
            }
        }
        return ACTIVE;
    }

    @Override
    public String toString() {
        return value;
    }

}
