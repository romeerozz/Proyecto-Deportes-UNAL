package co.unal.deportesunal.test;

public class SmokeTestsRunner {
    public static void main(String[] args) {
        int failures = 0;
        System.out.println("Running smoke tests...");
        try { if (!HashTableTest.run()) { System.out.println("HashTableTest failed"); failures++; } else System.out.println("HashTableTest passed"); } catch (Throwable t) { t.printStackTrace(); failures++; }
        try { if (!MaxHeapTest.run()) { System.out.println("MaxHeapTest failed"); failures++; } else System.out.println("MaxHeapTest passed"); } catch (Throwable t) { t.printStackTrace(); failures++; }
        try { if (!AdjacencyListGraphTest.run()) { System.out.println("AdjacencyListGraphTest failed"); failures++; } else System.out.println("AdjacencyListGraphTest passed"); } catch (Throwable t) { t.printStackTrace(); failures++; }
        try { if (!HashStudentIndexTest.run()) { System.out.println("HashStudentIndexTest failed"); failures++; } else System.out.println("HashStudentIndexTest passed"); } catch (Throwable t) { t.printStackTrace(); failures++; }

        if (failures == 0) {
            System.out.println("ALL SMOKE TESTS PASSED");
            System.exit(0);
        } else {
            System.out.println(failures + " smoke test(s) failed");
            System.exit(2);
        }
    }
}
