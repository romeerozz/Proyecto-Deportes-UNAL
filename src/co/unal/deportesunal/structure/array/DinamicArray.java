package co.unal.deportesunal.structure.array;

public class DinamicArray<T> {
	private static final int DEFAULT_CAPACITY = 16;

	private Object[] elements;
	private int size;

	public DinamicArray() {
		this(DEFAULT_CAPACITY);
	}

	public DinamicArray(int initialCapacity) {
		if (initialCapacity <= 0) {
			throw new IllegalArgumentException("Initial capacity must be greater than 0.");
		}
		this.elements = new Object[initialCapacity];
	}

	public int size() {
		return size;
	}

	public boolean isEmpty() {
		return size == 0;
	}

	public void add(T value) {
		ensureCapacity(size + 1);
		elements[size++] = value;
	}

	public void add(int index, T value) {
		validateInsertIndex(index);
		ensureCapacity(size + 1);

		for (int i = size; i > index; i--) {
			elements[i] = elements[i - 1];
		}

		elements[index] = value;
		size++;
	}

	@SuppressWarnings("unchecked")
	public T get(int index) {
		validateIndex(index);
		return (T) elements[index];
	}

	@SuppressWarnings("unchecked")
	public T set(int index, T value) {
		validateIndex(index);
		T previous = (T) elements[index];
		elements[index] = value;
		return previous;
	}

	@SuppressWarnings("unchecked")
	public T remove(int index) {
		validateIndex(index);

		T removed = (T) elements[index];
		for (int i = index; i < size - 1; i++) {
			elements[i] = elements[i + 1];
		}

		elements[size - 1] = null;
		size--;
		return removed;
	}

	public boolean contains(T value) {
		return indexOf(value) >= 0;
	}

	public int indexOf(T value) {
		for (int i = 0; i < size; i++) {
			Object current = elements[i];
			if (value == null ? current == null : value.equals(current)) {
				return i;
			}
		}
		return -1;
	}

	public void clear() {
		for (int i = 0; i < size; i++) {
			elements[i] = null;
		}
		size = 0;
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
			newArray[i] = elements[i];
		}

		elements = newArray;
	}

	private void validateIndex(int index) {
		if (index < 0 || index >= size) {
			throw new IndexOutOfBoundsException("Index out of bounds: " + index);
		}
	}

	private void validateInsertIndex(int index) {
		if (index < 0 || index > size) {
			throw new IndexOutOfBoundsException("Index out of bounds: " + index);
		}
	}
}
