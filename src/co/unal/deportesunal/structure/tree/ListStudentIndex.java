package co.unal.deportesunal.structure.tree;

import co.unal.deportesunal.domain.Student;
import co.unal.deportesunal.domain.exception.DuplicatedIdException;
import co.unal.deportesunal.domain.exception.NotFoundException;
import co.unal.deportesunal.structure.listadt.LinkedList;

public class ListStudentIndex implements StudentIndex{
    @Override
    public void put(int id, Student student) throws DuplicatedIdException {

    }

    @Override
    public Student get(int id) throws NotFoundException {
        return null;
    }

    @Override
    public boolean remove(int id) {
        return false;
    }

    @Override
    public LinkedList<Student> valuesInOrder() {
        return null;
    }

    @Override
    public boolean contains(int id) {
        return false;
    }

    @Override
    public int size() {
        return 0;
    }
}
