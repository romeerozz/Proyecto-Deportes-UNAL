package co.unal.deportesunal.structure.heap;

import co.unal.deportesunal.domain.SportCount;

public class MaxHeapSportCount {

    private SportCount[] heap;
    private int size;

    public MaxHeapSportCount() {
        heap = new SportCount[16];
        size = 0;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public int size() {
        return size;
    }

    public void insert(SportCount value) {
        if (size == heap.length) {
            resize();
        }

        heap[size] = value;
        siftUp(size);
        size++;
    }

    public SportCount extractMax() {
        if (isEmpty()) {
            throw new IllegalStateException("Heap vacío");
        }

        SportCount max = heap[0];

        size--;
        heap[0] = heap[size];
        heap[size] = null;

        if (size > 0) {
            siftDown(0);
        }

        return max;
    }

    private void siftUp(int index) {
        while (index > 0) {

            int parent = (index - 1) / 2;

            if (heap[parent].getCount() >= heap[index].getCount()) {
                break;
            }

            swap(parent, index);
            index = parent;
        }
    }

    private void siftDown(int index) {

        while (true) {

            int left = 2 * index + 1;
            int right = 2 * index + 2;

            int largest = index;

            if (left < size &&
                heap[left].getCount() > heap[largest].getCount()) {
                largest = left;
            }

            if (right < size &&
                heap[right].getCount() > heap[largest].getCount()) {
                largest = right;
            }

            if (largest == index) {
                break;
            }

            swap(index, largest);
            index = largest;
        }
    }

    private void swap(int i, int j) {
        SportCount temp = heap[i];
        heap[i] = heap[j];
        heap[j] = temp;
    }

    private void resize() {
        SportCount[] newHeap = new SportCount[heap.length * 2];

        for (int i = 0; i < heap.length; i++) {
            newHeap[i] = heap[i];
        }

        heap = newHeap;
    }
}