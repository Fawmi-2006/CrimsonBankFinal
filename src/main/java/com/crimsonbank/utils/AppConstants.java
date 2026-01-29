package com.crimsonbank.utils;

public final class AppConstants {

    public static final int WINDOW_WIDTH = 1000;
    public static final int WINDOW_HEIGHT = 700;
    public static final int DIALOG_WIDTH = 500;
    public static final int DIALOG_HEIGHT = 400;

    public static final int PAGE_SIZE = 20;
    public static final int ITEMS_PER_PAGE = 20;

    public static final int MIN_PASSWORD_LENGTH = 6;
    public static final int MIN_PIN_LENGTH = 6;
    public static final int NIC_LENGTH = 10;

    public static final String ACCOUNT_TYPE_SAVINGS = "SAVINGS";
    public static final String ACCOUNT_TYPE_CURRENT = "CURRENT";
    public static final String ACCOUNT_STATUS_ACTIVE = "ACTIVE";
    public static final String ACCOUNT_STATUS_INACTIVE = "INACTIVE";
    public static final String ACCOUNT_STATUS_FROZEN = "FROZEN";

    public static final String LOAN_STATUS_PENDING = "PENDING";
    public static final String LOAN_STATUS_APPROVED = "APPROVED";
    public static final String LOAN_STATUS_REJECTED = "REJECTED";

    public static final String TRANSACTION_TYPE_DEPOSIT = "DEPOSIT";
    public static final String TRANSACTION_TYPE_WITHDRAWAL = "WITHDRAWAL";
    public static final String TRANSACTION_TYPE_TRANSFER = "TRANSFER";

    public static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String DATE_TIME_SHORT_FORMAT = "yyyy-MM-dd HH:mm";
    public static final String DATE_FORMAT = "yyyy-MM-dd";

    public static final String AUDIT_LOGIN = "LOGIN_ATTEMPT";
    public static final String AUDIT_DEPOSIT = "DEPOSIT";
    public static final String AUDIT_WITHDRAWAL = "WITHDRAWAL";
    public static final String AUDIT_TRANSFER = "TRANSFER";
    public static final String AUDIT_LOAN_APPROVAL = "LOAN_APPROVAL";

    public static final int CACHE_MAX_SIZE = 100;

    public static final int DEFAULT_STAFF_ID = 1;

    private AppConstants() {
    }

}
