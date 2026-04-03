package co.unal.deportesunal.structure.listadt;

public class LinkedList<T> implements listAdt<T> {
    private Node<T> head;
    private Node<T> tail;
    private int size;

    @Override
    public void pushFront(T value) {
        Node<T> newNode = new Node<>(value);

        if (isEmpty()) {
            head = newNode;
            tail = newNode;
            size++;
            return;
        }

        newNode.setNext(head);
        head.setPrev(newNode);
        head = newNode;
        size++;
    }

    @Override
    public void pushBack(T value) {
        Node<T> newNode = new Node<>(value);

        if (isEmpty()) {
            head = newNode;
            tail = newNode;
            size++;
            return;
        }

        newNode.setPrev(tail);
        tail.setNext(newNode);
        tail = newNode;
        size++;
    }

    @Override
    public T popFront() {
        if (isEmpty()) {
            throw new IllegalStateException("The list is empty.");
        }

        T deleted = head.getValue();

        if (head == tail) {
            head = null;
            tail = null;
            size--;
            return deleted;
        }

        head = head.getNext();
        head.setPrev(null);
        size--;
        return deleted;
    }

    @Override
    public T popBack() {
        if (isEmpty()) {
            throw new IllegalStateException("The list is empty.");
        }

        T deleted = tail.getValue();

        if (head == tail) {
            head = null;
            tail = null;
            size--;
            return deleted;
        }

        tail = tail.getPrev();
        tail.setNext(null);
        size--;
        return deleted;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public T topFront() {
        if (isEmpty()) {
            throw new IllegalStateException("The list is empty.");
        }

        return head.getValue();
    }

    @Override
    public T topBack() {
        if (isEmpty()) {
            throw new IllegalStateException("The list is empty.");
        }

        return tail.getValue();
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public Position<T> find(T value) {
        Node<T> aux = head;
        while (aux != null) {
            T current = aux.getValue();
            if (value == null ? current == null : value.equals(current)) {
                return aux;
            }
            aux = aux.getNext();
        }
        return null;
    }

    @Override
    public void erase(Position<T> position) {
        if (position == null) {
            throw new IllegalArgumentException("Position can not be null.");
        }

        Node<T> target = (Node<T>) position;

        if (head == tail && target == head) {
            head = null;
            tail = null;
            size--;
            return;
        }

        if (target == head) {
            head = head.getNext();
            head.setPrev(null);
            target.setNext(null);
            size--;
            return;
        }

        if (target == tail) {
            tail = tail.getPrev();
            tail.setNext(null);
            target.setPrev(null);
            size--;
            return;
        }

        target.getPrev().setNext(target.getNext());
        target.getNext().setPrev(target.getPrev());
        target.setPrev(null);
        target.setNext(null);
        size--;
    }

    @Override
    public boolean remove(T value) {
        Position<T> position = find(value);
        if (position == null) {
            return false;
        }

        erase(position);
        return true;
    }

    @Override
    public void addBefore(Position<T> position, T value) {
        if (position == null) {
            throw new IllegalArgumentException("Position can not be null.");
        }

        Node<T> target = (Node<T>) position;
        Node<T> newNode = new Node<>(value);

        if (target == head) {
            newNode.setNext(head);
            head.setPrev(newNode);
            head = newNode;
            size++;
            return;
        }

        newNode.setPrev(target.getPrev());
        newNode.setNext(target);
        target.getPrev().setNext(newNode);
        target.setPrev(newNode);
        size++;
    }

    @Override
    public void addAfter(Position<T> position, T value) {
        if (position == null) {
            throw new IllegalArgumentException("Position can not be null.");
        }

        Node<T> target = (Node<T>) position;
        Node<T> newNode = new Node<>(value);

        if (target == tail) {
            target.setNext(newNode);
            newNode.setPrev(target);
            tail = newNode;
            size++;
            return;
        }

        newNode.setNext(target.getNext());
        newNode.setPrev(target);
        target.getNext().setPrev(newNode);
        target.setNext(newNode);
        size++;
    }

    @Override
    public boolean contains(T value) {
        return find(value) != null;
    }

    @Override
    public void traverse(ListVisitor<T> visitor) {
        if (visitor == null) {
            throw new IllegalArgumentException("Visitor can not be null.");
        }

        Node<T> current = head;
        while (current != null) {
            visitor.visit(current.getValue());
            current = current.getNext();
        }
    }
}
