package co.unal.deportesunal.structure.index;

import co.unal.deportesunal.domain.Student;
import co.unal.deportesunal.domain.exception.DuplicatedIdException;
import co.unal.deportesunal.domain.exception.NotFoundException;
import co.unal.deportesunal.structure.listadt.LinkedList;
import co.unal.deportesunal.structure.tree.BstTree;

public class BstIndex implements StudentIndex {

	private final BstTree<Integer, Student> tree;

	public BstIndex() {
		this.tree = new BstTree<>();
	}

	@Override
	public void put(int id, Student student) throws DuplicatedIdException {
		if (student == null) {
			throw new IllegalArgumentException("Student cannot be null.");
		}

		if (tree.contains(id)) {
			throw new DuplicatedIdException("Duplicate ID: " + id);
		}

		if (student.getId() != id) {
			throw new IllegalArgumentException(
					"ID mismatch: key=" + id + ", student.id=" + student.getId()
			);
		}
		tree.put(id, student);
	}

	@Override
	public Student get(int id) throws NotFoundException {
		Student student = tree.get(id);

		if (student == null) {
			throw new NotFoundException("Student not found with ID: " + id);
		}
		return student;
	}

	@Override
	public boolean remove(int id) {
		return tree.remove(id);
	}

	@Override
	public LinkedList<Student> valuesInOrder() {
		return tree.valuesInOrder();
	}

	@Override
	public boolean contains(int id) {
		return tree.contains(id);
	}

	@Override
	public int size() {
		return tree.size();
	}
}