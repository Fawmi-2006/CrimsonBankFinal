package com.crimsonbank.tests;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class DeliberateFailuresTest {

    @Test
    public void testWrongSum() {
        int sum = 1 + 1;
        assertEquals(3, sum, "Intentional failing test: 1+1 should equal 3");
    }

    @Test
    public void testNullPointerNotThrown() {
        String s = "not null";
        assertThrows(NullPointerException.class, () -> s.length(), "Intentional failing test: expected NullPointerException");
    }

    @Test
    public void testBooleanFalse() {
        fail("Intentional failing test: expected true");
    }
}
