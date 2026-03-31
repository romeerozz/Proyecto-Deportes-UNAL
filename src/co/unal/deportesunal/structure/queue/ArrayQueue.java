package co.unal.deportesunal.structure.queue;

public class ArrayQueue<T> implements Queue<T> {
	private static final int DEFAULT_CAPACITY = 16;

	private Object[] elements;
	private int head;
	private int tail;
	private int size;

	public ArrayQueue() {
		this.elements = new Object[DEFAULT_CAPACITY];
	}

	@Override
	public void enqueue(T value) {
		ensureCapacity(size + 1);
		elements[tail] = value;
		tail = (tail + 1) % elements.length;
		size++;
	}

	@Override
	@SuppressWarnings("unchecked")
	public T dequeue() {
		if (isEmpty()) {
			throw new IllegalStateException("The queue is empty.");
		}

		T removed = (T) elements[head];
		elements[head] = null;
		head = (head + 1) % elements.length;
		size--;
		return removed;
	}

	@Override
	@SuppressWarnings("unchecked")
	public T peek() {
		if (isEmpty()) {
			throw new IllegalStateException("The queue is empty.");
		}

		return (T) elements[head];
	}

	@Override
	public boolean isEmpty() {
		return size == 0;
	}

	@Override
	public int size() {
		return size;
	}

	private void ensureCapacity(int requiredCapacity) {
		if (requiredCapacity <= elements.length) {
			return;
		}

		int newCapacity = elements.length * 2;
		while (newCapacity < requiredCapacity) {
			newCapacity *= 2;
		}

		Object[] newArray = new Object[newCapacity];
		for (int i = 0; i < size; i++) {
			newArray[i] = elements[(head + i) % elements.length];
		}

		elements = newArray;
		head = 0;
		tail = size;
	}
}
