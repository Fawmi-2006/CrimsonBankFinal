package com.crimsonbank.models;

import java.time.LocalDateTime;

public class AuditLog {

    private int logId;
    private String action;
    private String description;
    private Integer staffId;
    private Integer customerId;
    private Integer accountId;
    private Integer transactionId;
    private Integer loanId;
    private LocalDateTime createdAt;

    public AuditLog() {
    }

    public AuditLog(String action, String description, Integer staffId) {
        this.action = action;
        this.description = description;
        this.staffId = staffId;
    }

    public int getLogId() {
        return logId;
    }

    public void setLogId(int logId) {
        this.logId = logId;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getStaffId() {
        return staffId;
    }

    public void setStaffId(Integer staffId) {
        this.staffId = staffId;
    }

    public Integer getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }

    public Integer getAccountId() {
        return accountId;
    }

    public void setAccountId(Integer accountId) {
        this.accountId = accountId;
    }

    public Integer getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(Integer transactionId) {
        this.transactionId = transactionId;
    }

    public Integer getLoanId() {
        return loanId;
    }

    public void setLoanId(Integer loanId) {
        this.loanId = loanId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "AuditLog{" +
                "logId=" + logId +
                ", action='" + action + '\'' +
                ", description='" + description + '\'' +
                ", staffId=" + staffId +
                ", customerId=" + customerId +
                ", accountId=" + accountId +
                ", transactionId=" + transactionId +
                ", loanId=" + loanId +
                ", createdAt=" + createdAt +
                '}';
    }

}
