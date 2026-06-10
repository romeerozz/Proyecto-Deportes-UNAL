package co.unal.deportesunal.structure.listadt;

/**
 * Implementación de una lista doblemente enlazada genérica. Ofrece
 * operaciones de inserción, eliminación y consulta por ambos extremos,
 * así como recorrido mediante un visitante.
 *
 * @param <T> tipo de los elementos almacenados
 */
public class LinkedList<T> implements listAdt<T> {
    private Node<T> head;
    private Node<T> tail;
    private int size;

    /**
     * Inserta un elemento al inicio de la lista.
     *
     * @param value valor a insertar
     */
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

    /**
     * Inserta un elemento al final de la lista.
     *
     * @param value valor a insertar
     */
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

    /**
     * Elimina y retorna el primer elemento de la lista.
     *
     * @return el valor del primer elemento
     * @throws IllegalStateException si la lista está vacía
     */
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

    /**
     * Elimina y retorna el último elemento de la lista.
     *
     * @return el valor del último elemento
     * @throws IllegalStateException si la lista está vacía
     */
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

    /**
     * Verifica si la lista está vacía.
     *
     * @return {@code true} si la lista no contiene elementos
     */
    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * Retorna el valor del primer elemento sin eliminarlo.
     *
     * @return valor del primer elemento
     * @throws IllegalStateException si la lista está vacía
     */
    @Override
    public T topFront() {
        if (isEmpty()) {
            throw new IllegalStateException("The list is empty.");
        }

        return head.getValue();
    }

    /**
     * Retorna el valor del último elemento sin eliminarlo.
     *
     * @return valor del último elemento
     * @throws IllegalStateException si la lista está vacía
     */
    @Override
    public T topBack() {
        if (isEmpty()) {
            throw new IllegalStateException("The list is empty.");
        }

        return tail.getValue();
    }

    /**
     * Retorna la cantidad de elementos en la lista.
     *
     * @return número de elementos
     */
    @Override
    public int size() {
        return size;
    }

    /**
     * Busca la primera ocurrencia de un valor en la lista.
     *
     * @param value valor a buscar
     * @return la posición del valor, o {@code null} si no se encuentra
     */
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

    /**
     * Elimina el elemento en la posición especificada.
     *
     * @param position posición del elemento a eliminar
     * @throws IllegalArgumentException si la posición es {@code null}
     */
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

    /**
     * Elimina la primera ocurrencia del valor especificado.
     *
     * @param value valor a eliminar
     * @return {@code true} si se eliminó, {@code false} si no se encontró
     */
    @Override
    public boolean remove(T value) {
        Position<T> position = find(value);
        if (position == null) {
            return false;
        }

        erase(position);
        return true;
    }

    /**
     * Inserta un nuevo valor antes de la posición especificada.
     *
     * @param position posición de referencia
     * @param value    valor a insertar
     * @throws IllegalArgumentException si la posición es {@code null}
     */
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

    /**
     * Inserta un nuevo valor después de la posición especificada.
     *
     * @param position posición de referencia
     * @param value    valor a insertar
     * @throws IllegalArgumentException si la posición es {@code null}
     */
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

    /**
     * Verifica si la lista contiene el valor especificado.
     *
     * @param value valor a buscar
     * @return {@code true} si el valor está presente
     */
    @Override
    public boolean contains(T value) {
        return find(value) != null;
    }

    /**
     * Recorre la lista aplicando el visitante a cada elemento en orden.
     *
     * @param visitor visitante a aplicar
     * @throws IllegalArgumentException si el visitante es {@code null}
     */
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
