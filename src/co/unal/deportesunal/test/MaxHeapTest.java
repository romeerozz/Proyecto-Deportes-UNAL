package co.unal.deportesunal.test;

import co.unal.deportesunal.structure.heap.MaxHeap;
import co.unal.deportesunal.structure.heap.Comparator;

public class MaxHeapTest {
    public static boolean run() {
        Comparator<Integer> comp = new Comparator<Integer>() {
            @Override
            public int compare(Integer a, Integer b) { return Integer.compare(a, b); }
        };
        MaxHeap<Integer> heap = new MaxHeap<>(comp);
        heap.push(5);
        heap.push(1);
        heap.push(3);
        heap.push(10);
        boolean ok = true;
        ok &= heap.size() == 4;
        ok &= 10 == heap.pop();
        ok &= 5 == heap.pop();
        ok &= 3 == heap.pop();
        ok &= 1 == heap.pop();
        ok &= heap.isEmpty();
        return ok;
    }

    public static void main(String[] args) {
        boolean ok = run();
        System.out.println("MaxHeapTest: " + (ok ? "PASS" : "FAIL"));
        if (!ok) System.exit(1);
    }
}
