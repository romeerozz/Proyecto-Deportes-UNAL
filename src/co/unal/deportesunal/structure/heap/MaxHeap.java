package co.unal.deportesunal.structure.heap;

import co.unal.deportesunal.structure.listadt.LinkedList;

/**
 * Montículo binario de máximo (max-heap). Mantiene la propiedad de que
 * cada nodo padre es mayor o igual que sus hijos según un comparador
 * proporcionado. Soporta inserción, extracción del máximo y construcción
 * desde una lista.
 *
 * @param <T> tipo de los elementos almacenados
 */
public class MaxHeap<T> {
    private Object[] data;
    private int size;
    private final Comparator<T> comparator;

    /**
     * Crea un montículo con la capacidad inicial y el comparador dados.
     *
     * @param capacity   capacidad inicial (mínimo 2)
     * @param comparator comparador para ordenar los elementos
     */
    public MaxHeap(int capacity, Comparator<T> comparator) {
        this.data = new Object[Math.max(2, capacity)];
        this.size = 0;
        this.comparator = comparator;
    }

    /**
     * Crea un montículo con capacidad inicial de 16 y el comparador dado.
     *
     * @param comparator comparador para ordenar los elementos
     */
    public MaxHeap(Comparator<T> comparator) {
        this(16, comparator);
    }

    /**
     * Duplica la capacidad del arreglo interno si está lleno.
     */
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

    /**
     * Inserta un nuevo valor en el montículo y reordena para mantener
     * la propiedad de máximo.
     *
     * @param value valor a insertar
     */
    public void push(T value) {
        ensureCapacity();
        data[size] = value;
        siftUp(size);
        size++;
    }

    /**
     * Desplaza el elemento en la posición {@code idx} hacia arriba hasta
     * restaurar la propiedad del montículo.
     *
     * @param idx índice del elemento a reubicar
     */
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

    /**
     * Extrae y retorna el elemento máximo (raíz) del montículo.
     * Luego reordena el montículo para mantener la propiedad de máximo.
     *
     * @return el valor máximo, o {@code null} si el montículo está vacío
     */
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

    /**
     * Desplaza el elemento en la posición {@code idx} hacia abajo hasta
     * restaurar la propiedad del montículo.
     *
     * @param idx índice del elemento a reubicar
     */
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

    /**
     * Retorna la cantidad de elementos en el montículo.
     *
     * @return número de elementos
     */
    public int size() { return size; }

    /**
     * Verifica si el montículo está vacío.
     *
     * @return {@code true} si está vacío
     */
    public boolean isEmpty() { return size == 0; }

    /**
     * Construye un montículo de máximo a partir de una lista enlazada
     * insertando todos sus elementos.
     *
     * @param <E>  tipo de los elementos
     * @param list lista de entrada
     * @param comp comparador para ordenar
     * @return nuevo montículo con los elementos de la lista
     */
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
