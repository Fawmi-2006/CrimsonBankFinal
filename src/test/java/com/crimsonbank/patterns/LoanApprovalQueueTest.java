package com.crimsonbank.patterns;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class LoanApprovalQueueTest {
    private LoanApprovalQueue queue;

    @BeforeEach
    public void setUp() {
        queue = new LoanApprovalQueue();
    }

    @Test
    public void testEnqueueDequeue() {
        queue.enqueue(1);
        queue.enqueue(2);
        queue.enqueue(3);

        assertEquals(1, queue.dequeue());
        assertEquals(2, queue.dequeue());
        assertEquals(3, queue.dequeue());
    }

    @Test
    public void testFIFOBehavior() {
        queue.enqueue(100);
        queue.enqueue(200);
        queue.enqueue(300);

        Integer first = queue.peek();
        assertEquals(100, first);

        Integer dequeued = queue.dequeue();
        assertEquals(100, dequeued);
    }

    @Test
    public void testEmptyQueue() {
        assertTrue(queue.isEmpty());
        assertNull(queue.dequeue());
        assertNull(queue.peek());
    }

    @Test
    public void testQueueSize() {
        assertEquals(0, queue.size());
        queue.enqueue(1);
        assertEquals(1, queue.size());
        queue.enqueue(2);
        assertEquals(2, queue.size());
        queue.dequeue();
        assertEquals(1, queue.size());
    }

    @Test
    public void testClearQueue() {
        queue.enqueue(1);
        queue.enqueue(2);
        queue.enqueue(3);
        queue.clear();
        assertTrue(queue.isEmpty());
        assertEquals(0, queue.size());
    }
}
