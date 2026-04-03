package co.unal.deportesunal.structure.listadt;

public interface ListVisitor<T> {
    void visit(T value);
}
