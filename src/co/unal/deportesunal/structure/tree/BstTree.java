package co.unal.deportesunal.structure.tree;

import co.unal.deportesunal.structure.listadt.LinkedList;
import co.unal.deportesunal.structure.stackadt.ArrayStack;
import co.unal.deportesunal.structure.stackadt.Stack;

public class BstTree<K extends Comparable<K>, V> implements Tree<K, V> {

    private Node root;
    private int size;

    private class Node {
        K key;
        V value;
        Node left;
        Node right;

        Node(K key, V value) {
            this.key = key;
            this.value = value;
        }
    }

    public BstTree() {
        this.root = null;
        this.size = 0;
    }

    @Override
    public void put(K key, V value) {
        if (key == null) {
            throw new IllegalArgumentException("Key cannot be null.");
        }

        root = put(root, key, value);
    }

    private Node put(Node node, K key, V value) {
        if (node == null) {
            size++;
            return new Node(key, value);
        }

        int cmp = key.compareTo(node.key);

        if (cmp < 0) {
            node.left = put(node.left, key, value);
        } else if (cmp > 0) {
            node.right = put(node.right, key, value);
        } else {
            node.value = value;
        }

        return node;
    }

    @Override
    public V get(K key) {
        if (key == null) {
            throw new IllegalArgumentException("Key cannot be null.");
        }

        Node node = getNode(root, key);
        return node == null ? null : node.value;
    }

    private Node getNode(Node node, K key) {
        if (node == null) return null;

        int cmp = key.compareTo(node.key);

        if (cmp == 0) return node;
        if (cmp < 0) return getNode(node.left, key);
        return getNode(node.right, key);
    }

    @Override
    public boolean contains(K key) {
        if (key == null) {
            throw new IllegalArgumentException("Key cannot be null.");
        }

        return getNode(root, key) != null;
    }

    @Override
    public boolean remove(K key) {
        if (key == null) {
            throw new IllegalArgumentException("Key cannot be null.");
        }

        if (!contains(key)) return false;

        root = remove(root, key);
        size--;
        return true;
    }

    private Node remove(Node node, K key) {
        if (node == null) return null;

        int cmp = key.compareTo(node.key);

        if (cmp < 0) {
            node.left = remove(node.left, key);
        } else if (cmp > 0) {
            node.right = remove(node.right, key);
        } else {
            if (node.left == null) return node.right;

            if (node.right == null) return node.left;

            Node successor = getMin(node.right);

            node.key = successor.key;
            node.value = successor.value;

            node.right = remove(node.right, successor.key);
        }

        return node;
    }

    private Node getMin(Node node) {
        while (node.left != null) {
            node = node.left;
        }
        return node;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public LinkedList<V> valuesInOrder() {
        LinkedList<V> values = new LinkedList<>();
        Stack<Node> stack = new ArrayStack<>();

        Node current = root;

        while (current != null || !stack.isEmpty()) {
            while (current != null) {
                stack.push(current);
                current = current.left;
            }

            current = stack.pop();
            values.pushBack(current.value);

            current = current.right;
        }

        return values;
    }

}