package co.unal.deportesunal.structure.tree;

import co.unal.deportesunal.domain.Student;
import co.unal.deportesunal.structure.listadt.LinkedList;

public class AvlIndex implements StudentIndex {
	@Override
	public void put(int id, Student student) {
		throw new UnsupportedOperationException("AVL implementation pending.");
	}

	@Override
	public Student get(int id) {
		throw new UnsupportedOperationException("AVL implementation pending.");
	}

	@Override
	public Student remove(int id) {
		throw new UnsupportedOperationException("AVL implementation pending.");
	}

	@Override
	public LinkedList<Student> valuesInOrder() {
		throw new UnsupportedOperationException("AVL implementation pending.");
	}

	@Override
	public boolean contains(int id) {
		throw new UnsupportedOperationException("AVL implementation pending.");
	}

	@Override
	public int size() {
		throw new UnsupportedOperationException("AVL implementation pending.");
	}
}
