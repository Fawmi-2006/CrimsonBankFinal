package com.crimsonbank.patterns;

import com.crimsonbank.models.AuditLog;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class RecentActionsStackTest {
    private RecentActionsStack stack;

    @BeforeEach
    public void setUp() {
        stack = new RecentActionsStack();
    }

    @Test
    public void testPushPop() {
        AuditLog log1 = new AuditLog("LOGIN", "User login", 1);
        AuditLog log2 = new AuditLog("WITHDRAWAL", "Withdrawal", 1);
        AuditLog log3 = new AuditLog("TRANSFER", "Transfer", 1);

        stack.push(log1);
        stack.push(log2);
        stack.push(log3);

        assertEquals(log3, stack.pop());
        assertEquals(log2, stack.pop());
        assertEquals(log1, stack.pop());
    }

    @Test
    public void testLIFOBehavior() {
        AuditLog log1 = new AuditLog("ACTION1", "Description 1", 1);
        AuditLog log2 = new AuditLog("ACTION2", "Description 2", 1);

        stack.push(log1);
        stack.push(log2);

        AuditLog top = stack.peek();
        assertEquals(log2, top);
        assertFalse(stack.isEmpty());
    }

    @Test
    public void testEmptyStack() {
        assertTrue(stack.isEmpty());
        assertNull(stack.pop());
        assertNull(stack.peek());
    }

    @Test
    public void testStackSize() {
        assertEquals(0, stack.size());
        stack.push(new AuditLog("TEST", "Test", 1));
        assertEquals(1, stack.size());
    }

    @Test
    public void testClearStack() {
        stack.push(new AuditLog("TEST1", "Test1", 1));
        stack.push(new AuditLog("TEST2", "Test2", 1));
        stack.clear();
        assertTrue(stack.isEmpty());
    }
}
