package com.crimsonbank.utils;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.ArrayList;
import java.util.List;

public class SortingAlgorithmsTest {

    @Test
    public void testQuickSortIntegers() {
        List<Integer> numbers = new ArrayList<>();
        numbers.add(5);
        numbers.add(2);
        numbers.add(8);
        numbers.add(1);
        numbers.add(9);

        SortingAlgorithms.quickSort(numbers);

        assertEquals(1, numbers.get(0));
        assertEquals(2, numbers.get(1));
        assertEquals(5, numbers.get(2));
        assertEquals(8, numbers.get(3));
        assertEquals(9, numbers.get(4));
    }

    @Test
    public void testQuickSortEmptyList() {
        List<Integer> numbers = new ArrayList<>();
        SortingAlgorithms.quickSort(numbers);
        assertEquals(0, numbers.size());
    }

    @Test
    public void testQuickSortSingleElement() {
        List<Integer> numbers = new ArrayList<>();
        numbers.add(5);
        SortingAlgorithms.quickSort(numbers);
        assertEquals(1, numbers.size());
        assertEquals(5, numbers.get(0));
    }

    @Test
    public void testMergeSortIntegers() {
        List<Integer> numbers = new ArrayList<>();
        numbers.add(5);
        numbers.add(2);
        numbers.add(8);
        numbers.add(1);
        numbers.add(9);

        SortingAlgorithms.mergeSort(numbers, Integer::compareTo);

        assertEquals(1, numbers.get(0));
        assertEquals(2, numbers.get(1));
        assertEquals(5, numbers.get(2));
        assertEquals(8, numbers.get(3));
        assertEquals(9, numbers.get(4));
    }

    @Test
    public void testQuickSortDescending() {
        List<Integer> numbers = new ArrayList<>();
        numbers.add(3);
        numbers.add(1);
        numbers.add(4);
        numbers.add(1);
        numbers.add(5);

        SortingAlgorithms.quickSort(numbers);

        for (int i = 0; i < numbers.size() - 1; i++) {
            assertTrue(numbers.get(i) <= numbers.get(i + 1));
        }
    }
}
