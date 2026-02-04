package com.crimsonbank.services;

import com.crimsonbank.models.Loan;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

public class LoanServiceTest {

    private Loan loan;

    @BeforeEach
    public void setUp() {
        loan = new Loan(1, 1, BigDecimal.valueOf(10000.00), BigDecimal.valueOf(5.5), 12);
        loan.setLoanId(1);
    }

    @Test
    public void testApproveLoanUpdatesStatus() {
        loan.setStatus("APPROVED");
        loan.setApprovedBy(1);

        assertEquals("APPROVED", loan.getStatus());
        assertTrue(loan.isApproved());
        assertNotNull(loan.getApprovedBy());
    }

    @Test
    public void testRejectLoanUpdatesStatus() {
        loan.setStatus("REJECTED");

        assertEquals("REJECTED", loan.getStatus());
        assertFalse(loan.isApproved());
        assertFalse(loan.isPending());
    }

    @Test
    public void testLoanCreatedWithPendingStatus() {
        Loan newLoan = new Loan(2, 2, BigDecimal.valueOf(5000.00), BigDecimal.valueOf(6.0), 24);

        assertEquals("PENDING", newLoan.getStatus());
        assertTrue(newLoan.isPending());
    }

    @Test
    public void testApproveLoanSetsApprovedBy() {
        int staffId = 5;

        loan.setStatus("APPROVED");
        loan.setApprovedBy(staffId);

        assertEquals("APPROVED", loan.getStatus());
        assertEquals(staffId, loan.getApprovedBy());
    }
}
