package co.unal.deportesunal.structure.stackadt;

import co.unal.deportesunal.structure.array.DinamicArray;

public class ArrayStack<T> implements Stack<T> {
	private final DinamicArray<T> elements;

	public ArrayStack() {
		this.elements = new DinamicArray<>();
	}

	@Override
	public void push(T value) {
		elements.add(value);
	}

	@Override
	public T pop() {
		if (isEmpty()) {
			throw new IllegalStateException("The stack is empty.");
		}
		return elements.remove(elements.size() - 1);
	}

	@Override
	public T peek() {
		if (isEmpty()) {
			throw new IllegalStateException("The stack is empty.");
		}
		return elements.get(elements.size() - 1);
	}

	@Override
	public boolean isEmpty() {
		return elements.isEmpty();
	}

	@Override
	public int size() {
		return elements.size();
	}
}
