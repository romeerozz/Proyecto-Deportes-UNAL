package co.unal.deportesunal.structure.tree;

import co.unal.deportesunal.domain.Student;
import co.unal.deportesunal.structure.listadt.LinkedList;

public class BstIndex implements StudentIndex {
	@Override
	public void put(int id, Student student) {
		throw new UnsupportedOperationException("BST implementation pending.");
	}

	@Override
	public Student get(int id) {
		throw new UnsupportedOperationException("BST implementation pending.");
	}

	@Override
	public boolean remove(int id) {
		return false;
	}

	@Override
	public LinkedList<Student> valuesInOrder() {
		throw new UnsupportedOperationException("BST implementation pending.");
	}

	@Override
	public boolean contains(int id) {
		throw new UnsupportedOperationException("BST implementation pending.");
	}

	@Override
	public int size() {
		throw new UnsupportedOperationException("BST implementation pending.");
	}
}
