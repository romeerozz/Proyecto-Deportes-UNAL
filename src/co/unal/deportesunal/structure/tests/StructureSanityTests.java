package co.unal.deportesunal.structure.tests;

import co.unal.deportesunal.structure.array.DinamicArray;
import co.unal.deportesunal.structure.listadt.LinkedList;
import co.unal.deportesunal.structure.queue.ArrayQueue;
import co.unal.deportesunal.structure.stackadt.ArrayStack;

public class StructureSanityTests {

    public static void main(String[] args) {
        testDynamicArray();
        testLinkedList();
        testStack();
        testQueue();

        System.out.println("All structure sanity tests passed.");
    }

    private static void testDynamicArray() {
        DinamicArray<Integer> arr = new DinamicArray<>();

        assertTrue(arr.isEmpty(), "DinamicArray should start empty");

        arr.add(10);
        arr.add(20);
        arr.add(1, 15);

        assertEquals(3, arr.size(), "DinamicArray size after adds");
        assertEquals(Integer.valueOf(10), arr.get(0), "DinamicArray get index 0");
        assertEquals(Integer.valueOf(15), arr.get(1), "DinamicArray get index 1");
        assertEquals(Integer.valueOf(20), arr.get(2), "DinamicArray get index 2");

        Integer removed = arr.remove(1);
        assertEquals(Integer.valueOf(15), removed, "DinamicArray remove should return removed value");
        assertEquals(2, arr.size(), "DinamicArray size after remove");
        assertTrue(arr.contains(20), "DinamicArray contains existing value");
    }

    private static void testLinkedList() {
        LinkedList<String> list = new LinkedList<>();

        list.pushBack("B");
        list.pushFront("A");
        list.pushBack("C");

        assertEquals(3, list.size(), "LinkedList size after pushes");
        assertEquals("A", list.topFront(), "LinkedList topFront");
        assertEquals("C", list.topBack(), "LinkedList topBack");
        assertTrue(list.contains("B"), "LinkedList should contain middle element");

        String popFront = list.popFront();
        String popBack = list.popBack();

        assertEquals("A", popFront, "LinkedList popFront");
        assertEquals("C", popBack, "LinkedList popBack");
        assertEquals(1, list.size(), "LinkedList size after pops");
        assertEquals("B", list.topFront(), "LinkedList remaining element");

        list.pushBack("D");
        boolean removed = list.remove("B");

        assertTrue(removed, "LinkedList remove(value) should return true when element exists");
        assertTrue(!list.contains("B"), "LinkedList should no longer contain removed value");
        assertEquals(1, list.size(), "LinkedList size after remove(value)");

        StringBuilder iteration = new StringBuilder();
        list.traverse(iteration::append);

        assertEquals("D", iteration.toString(), "LinkedList traverse traversal");
    }

    private static void testStack() {
        ArrayStack<Integer> stack = new ArrayStack<>();

        stack.push(1);
        stack.push(2);
        stack.push(3);

        assertEquals(3, stack.size(), "ArrayStack size after push");
        assertEquals(Integer.valueOf(3), stack.peek(), "ArrayStack peek");
        assertEquals(Integer.valueOf(3), stack.pop(), "ArrayStack pop #1");
        assertEquals(Integer.valueOf(2), stack.pop(), "ArrayStack pop #2");
        assertEquals(Integer.valueOf(1), stack.pop(), "ArrayStack pop #3");
        assertTrue(stack.isEmpty(), "ArrayStack should be empty after pops");
    }

    private static void testQueue() {
        ArrayQueue<String> queue = new ArrayQueue<>();

        queue.enqueue("A");
        queue.enqueue("B");
        queue.enqueue("C");

        assertEquals(3, queue.size(), "ArrayQueue size after enqueue");
        assertEquals("A", queue.peek(), "ArrayQueue peek");
        assertEquals("A", queue.dequeue(), "ArrayQueue dequeue #1");
        assertEquals("B", queue.dequeue(), "ArrayQueue dequeue #2");

        queue.enqueue("D");

        assertEquals("C", queue.dequeue(), "ArrayQueue dequeue #3");
        assertEquals("D", queue.dequeue(), "ArrayQueue dequeue #4");
        assertTrue(queue.isEmpty(), "ArrayQueue should be empty at the end");
    }

    private static void assertTrue(boolean condition, String message) {
        if (!condition) {
            throw new IllegalStateException("Test failed: " + message);
        }
    }

    private static void assertEquals(Object expected, Object actual, String message) {
        boolean equals = (expected == null && actual == null)
                || (expected != null && expected.equals(actual));

        if (!equals) {
            throw new IllegalStateException(
                    "Test failed: " + message + " | expected=" + expected + ", actual=" + actual
            );
        }
    }
}
