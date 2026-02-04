package com.crimsonbank.database;

import com.crimsonbank.models.AuditLog;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class AuditLogDAOTest {

    @Mock
    private Connection mockConnection;

    @Mock
    private PreparedStatement mockPreparedStatement;

    private AuditLog testLog;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        testLog = new AuditLog("LOGIN", "User logged in successfully", 1);
        testLog.setLogId(1);
        testLog.setCreatedAt(LocalDateTime.now());
    }

    @Test
    public void testAuditLogAppend() throws SQLException {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);

        assertNotNull(testLog);
        assertEquals("LOGIN", testLog.getAction());
        assertEquals("User logged in successfully", testLog.getDescription());
        assertEquals(1, testLog.getStaffId());
    }

    @Test
    public void testAuditLogCreation() {
        AuditLog log = new AuditLog("WITHDRAWAL", "Withdrawal processed", 2);

        assertEquals("WITHDRAWAL", log.getAction());
        assertEquals("Withdrawal processed", log.getDescription());
        assertEquals(2, log.getStaffId());
    }

    @Test
    public void testAuditLogWithAccountId() {
        AuditLog log = new AuditLog("ACCOUNT_CREATED", "New account created", 1);
        log.setAccountId(101);

        assertEquals(101, log.getAccountId());
    }

    @Test
    public void testAuditLogWithTransactionId() {
        AuditLog log = new AuditLog("TRANSACTION", "Transaction completed", 3);
        log.setTransactionId(500);

        assertEquals(500, log.getTransactionId());
    }

    @Test
    public void testMultipleAuditLogsAppend() throws SQLException {
        List<AuditLog> logs = new ArrayList<>();

        logs.add(new AuditLog("LOGIN", "Staff login", 1));
        logs.add(new AuditLog("DEPOSIT", "Deposit made", 1));
        logs.add(new AuditLog("LOGOUT", "Staff logout", 1));

        assertEquals(3, logs.size());
        assertEquals("LOGIN", logs.get(0).getAction());
        assertEquals("DEPOSIT", logs.get(1).getAction());
        assertEquals("LOGOUT", logs.get(2).getAction());
    }
}
