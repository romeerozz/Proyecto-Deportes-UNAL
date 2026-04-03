package co.unal.deportesunal.structure.listadt;

public interface listAdt<T> {
    void pushFront(T value);
    void pushBack(T value);
    T popFront();
    T popBack();
    boolean isEmpty();
    T topFront();
    T topBack();
    int size();
    Position<T> find(T value);
    void erase(Position<T> position);
    boolean remove(T value);
    void addBefore(Position<T> position, T value);
    void addAfter(Position<T> position, T value);
    boolean contains(T value);
    void traverse(ListVisitor<T> visitor);
}
