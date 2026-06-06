package co.unal.deportesunal.structure.tree;

import co.unal.deportesunal.structure.listadt.LinkedList;

public interface Tree<K extends Comparable<K>, V> {

    void put(K key, V value);

    V get(K key);

    boolean contains(K key);

    boolean remove(K key);

    int size();

    boolean isEmpty();

    LinkedList<V> valuesInOrder();
}