package com.crimsonbank.patterns;

import java.util.LinkedList;
import java.util.Queue;

public class LoanApprovalQueue {

    private final Queue<Integer> queue;

    public LoanApprovalQueue() {
        this.queue = new LinkedList<>();
    }

    public void enqueue(int loanId) {
        queue.add(loanId);
    }

    public Integer dequeue() {
        return queue.poll();
    }

    public Integer peek() {
        return queue.peek();
    }

    public boolean isEmpty() {
        return queue.isEmpty();
    }

    public int size() {
        return queue.size();
    }

    public void clear() {
        queue.clear();
    }

}
