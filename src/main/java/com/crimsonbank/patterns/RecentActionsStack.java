package com.crimsonbank.patterns;

import com.crimsonbank.models.AuditLog;

import java.util.Stack;
import java.util.List;

public class RecentActionsStack {

    private final Stack<AuditLog> stack;
    private static final int MAX_SIZE = 100;

    public RecentActionsStack() {
        this.stack = new Stack<>();
    }

    public void push(AuditLog log) {
        if (stack.size() >= MAX_SIZE) {
            stack.remove(0);
        }
        stack.push(log);
    }

    public AuditLog pop() {
        return stack.isEmpty() ? null : stack.pop();
    }

    public AuditLog peek() {
        return stack.isEmpty() ? null : stack.peek();
    }

    public boolean isEmpty() {
        return stack.isEmpty();
    }

    public int size() {
        return stack.size();
    }

    public void clear() {
        stack.clear();
    }

    public List<AuditLog> getAll() {
        return new java.util.ArrayList<>(stack);
    }

}
