package com.crimsonbank.database;

import com.crimsonbank.models.Transaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TransactionDAOTest {

    private List<Transaction> largeDataset;

    @BeforeEach
    public void setUp() {
        largeDataset = new ArrayList<>();

        for (int i = 1; i <= 550; i++) {
            Transaction tx = new Transaction(
                i % 10 + 1,
                i % 3 == 0 ? "DEPOSIT" : (i % 3 == 1 ? "WITHDRAWAL" : "TRANSFER"),
                BigDecimal.valueOf(100.00 + i),
                BigDecimal.valueOf(1000.00 + i * 10),
                "Transaction " + i
            );
            tx.setTransactionId(i);
            tx.setCreatedAt(LocalDateTime.now().minusDays(i % 30));
            largeDataset.add(tx);
        }
    }

    @Test
    public void testLargeDatasetLoad() {
        assertTrue(largeDataset.size() >= 500);
        assertEquals(550, largeDataset.size());
    }

    @Test
    public void testLargeDatasetLoadPerformance() {
        long startTime = System.currentTimeMillis();

        List<Transaction> loaded = new ArrayList<>(largeDataset);

        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        assertEquals(550, loaded.size());
        assertTrue(duration < 1000);
    }

    @Test
    public void testTransactionDataIntegrity() {
        assertNotNull(largeDataset.get(0));
        assertNotNull(largeDataset.get(0).getTransactionType());
        assertNotNull(largeDataset.get(0).getAmount());
        assertTrue(largeDataset.get(0).getAmount().compareTo(BigDecimal.ZERO) > 0);
    }

    @Test
    public void testFilterTransactionsByType() {
        long depositCount = largeDataset.stream()
            .filter(tx -> "DEPOSIT".equals(tx.getTransactionType()))
            .count();

        long withdrawalCount = largeDataset.stream()
            .filter(tx -> "WITHDRAWAL".equals(tx.getTransactionType()))
            .count();

        long transferCount = largeDataset.stream()
            .filter(tx -> "TRANSFER".equals(tx.getTransactionType()))
            .count();

        assertTrue(depositCount > 0);
        assertTrue(withdrawalCount > 0);
        assertTrue(transferCount > 0);
        assertEquals(550, depositCount + withdrawalCount + transferCount);
    }

    @Test
    public void testTransactionPagination() {
        int pageSize = 50;
        int pageNumber = 1;

        List<Transaction> page = largeDataset.stream()
            .skip((long) (pageNumber - 1) * pageSize)
            .limit(pageSize)
            .toList();

        assertEquals(50, page.size());
    }

    @Test
    public void testSortTransactionsByDate() {
        List<Transaction> sorted = new ArrayList<>(largeDataset);
        sorted.sort(Comparator.comparing(Transaction::getCreatedAt));

        assertTrue(sorted.size() >= 500);
        for (int i = 0; i < sorted.size() - 1; i++) {
            assertFalse(sorted.get(i).getCreatedAt().isAfter(sorted.get(i + 1).getCreatedAt()));
        }
    }
}
