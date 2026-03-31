package co.unal.deportesunal.structure.stackadt;

public interface Stack<T> {
	void push(T value);
	T pop();
	T peek();
	boolean isEmpty();
	int size();
}
