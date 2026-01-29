package com.crimsonbank.utils;

import com.crimsonbank.models.Account;
import com.crimsonbank.models.Transaction;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class SortingAlgorithms {

    public static <T extends Comparable<T>> void quickSort(List<T> list) {
        if (list == null || list.isEmpty()) {
            return;
        }
        quickSort(list, 0, list.size() - 1);
    }

    private static <T extends Comparable<T>> void quickSort(List<T> list, int low, int high) {
        if (low < high) {
            int pi = partition(list, low, high);
            quickSort(list, low, pi - 1);
            quickSort(list, pi + 1, high);
        }
    }

    private static <T extends Comparable<T>> int partition(List<T> list, int low, int high) {
        T pivot = list.get(high);
        int i = low - 1;

        for (int j = low; j < high; j++) {
            if (list.get(j).compareTo(pivot) < 0) {
                i++;
                T temp = list.get(i);
                list.set(i, list.get(j));
                list.set(j, temp);
            }
        }

        T temp = list.get(i + 1);
        list.set(i + 1, list.get(high));
        list.set(high, temp);
        return i + 1;
    }

    public static <T> void mergeSort(List<T> list, Comparator<T> comparator) {
        if (list == null || list.size() <= 1) {
            return;
        }
        mergeSort(list, 0, list.size() - 1, comparator);
    }

    private static <T> void mergeSort(List<T> list, int left, int right, Comparator<T> comparator) {
        if (left < right) {
            int mid = left + (right - left) / 2;
            mergeSort(list, left, mid, comparator);
            mergeSort(list, mid + 1, right, comparator);
            merge(list, left, mid, right, comparator);
        }
    }

    private static <T> void merge(List<T> list, int left, int mid, int right, Comparator<T> comparator) {
        List<T> leftList = new ArrayList<>(list.subList(left, mid + 1));
        List<T> rightList = new ArrayList<>(list.subList(mid + 1, right + 1));

        int i = 0, j = 0, k = left;

        while (i < leftList.size() && j < rightList.size()) {
            if (comparator.compare(leftList.get(i), rightList.get(j)) <= 0) {
                list.set(k++, leftList.get(i++));
            } else {
                list.set(k++, rightList.get(j++));
            }
        }

        while (i < leftList.size()) {
            list.set(k++, leftList.get(i++));
        }

        while (j < rightList.size()) {
            list.set(k++, rightList.get(j++));
        }
    }

    public static void sortTransactionsByDateDescending(List<Transaction> transactions) {
        mergeSort(transactions, (t1, t2) -> t2.getCreatedAt().compareTo(t1.getCreatedAt()));
    }

    public static void sortTransactionsByAmountAscending(List<Transaction> transactions) {
        mergeSort(transactions, (t1, t2) -> t1.getAmount().compareTo(t2.getAmount()));
    }

    public static void sortAccountsByBalance(List<Account> accounts) {
        mergeSort(accounts, (a1, a2) -> a2.getBalance().compareTo(a1.getBalance()));
    }

}
