package co.unal.deportesunal.structure.heap;

import co.unal.deportesunal.structure.listadt.LinkedList;

public class MaxHeap<T> {
    private Object[] data;
    private int size;
    private final Comparator<T> comparator;

    public MaxHeap(int capacity, Comparator<T> comparator) {
        this.data = new Object[Math.max(2, capacity)];
        this.size = 0;
        this.comparator = comparator;
    }

    public MaxHeap(Comparator<T> comparator) {
        this(16, comparator);
    }

    private void ensureCapacity() {
        if (size >= data.length) {
            Object[] nd = new Object[data.length * 2];
            System.arraycopy(data, 0, nd, 0, data.length);
            data = nd;
        }
    }

    @SuppressWarnings("unchecked")
    private int compareAt(int i, int j) {
        return comparator.compare((T) data[i], (T) data[j]);
    }

    public void push(T value) {
        ensureCapacity();
        data[size] = value;
        siftUp(size);
        size++;
    }

    private void siftUp(int idx) {
        while (idx > 0) {
            int parent = (idx - 1) / 2;
            if (compareAt(idx, parent) > 0) {
                swap(idx, parent);
                idx = parent;
            } else break;
        }
    }

    private void swap(int i, int j) {
        Object t = data[i];
        data[i] = data[j];
        data[j] = t;
    }

    @SuppressWarnings("unchecked")
    public T pop() {
        if (size == 0) return null;
        T root = (T) data[0];
        data[0] = data[size - 1];
        data[size - 1] = null;
        size--;
        siftDown(0);
        return root;
    }

    private void siftDown(int idx) {
        while (true) {
            int left = idx * 2 + 1;
            int right = idx * 2 + 2;
            int largest = idx;
            if (left < size && compareAt(left, largest) > 0) largest = left;
            if (right < size && compareAt(right, largest) > 0) largest = right;
            if (largest != idx) {
                swap(largest, idx);
                idx = largest;
            } else break;
        }
    }

    public int size() { return size; }

    public boolean isEmpty() { return size == 0; }

    /** Helper to build a heap from a LinkedList by pushing elements. */
    public static <E> MaxHeap<E> fromList(LinkedList<E> list, Comparator<E> comp) {
        MaxHeap<E> heap = new MaxHeap<>(comp);
        list.traverse(new co.unal.deportesunal.structure.listadt.ListVisitor<E>() {
            @Override
            public void visit(E value) {
                heap.push(value);
            }
        });
        return heap;
    }
}
