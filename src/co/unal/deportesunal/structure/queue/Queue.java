package co.unal.deportesunal.structure.queue;

public interface Queue<T> {
	void enqueue(T value);
	T dequeue();
	T peek();
	boolean isEmpty();
	int size();
}
