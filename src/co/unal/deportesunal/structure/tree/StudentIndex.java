package co.unal.deportesunal.structure.tree;

import co.unal.deportesunal.domain.Student;
import co.unal.deportesunal.domain.exception.DuplicatedIdException;
import co.unal.deportesunal.domain.exception.NotFoundException;
import co.unal.deportesunal.structure.listadt.LinkedList;

public interface StudentIndex {
	void put(int id, Student student) throws DuplicatedIdException;
	Student get(int id) throws NotFoundException;
	Student remove(int id) throws NotFoundException;
	LinkedList<Student> valuesInOrder();
	boolean contains(int id);
	int size();
}
