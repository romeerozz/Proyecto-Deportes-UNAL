package co.unal.deportesunal.structure.tree;

import co.unal.deportesunal.domain.Student;
import co.unal.deportesunal.domain.exception.DuplicatedIdException;
import co.unal.deportesunal.domain.exception.NotFoundException;
import co.unal.deportesunal.structure.listadt.LinkedList;
import co.unal.deportesunal.structure.listadt.ListVisitor;

public class ListStudentIndex implements StudentIndex {

    private final LinkedList<Student> data = new LinkedList<>();

    @Override
    public void put(int id, Student student) throws DuplicatedIdException {
        if (student == null) {
            throw new IllegalArgumentException("Student cannot be null.");
        }
        if (contains(id)) {
            throw new DuplicatedIdException("Duplicate ID: " + id);
        }
        if (student.getId() != id && student.getId() != id) {
            throw new IllegalArgumentException("ID mismatch: param id=" + id + ", student.id=" + student.getId());
        }

        data.pushBack(student);
    }

    @Override
    public Student get(int id) throws NotFoundException {
        Student found = findById(id);
        if (found == null) {
            throw new NotFoundException("Student not found: " + id);
        }
        return found;
    }

    @Override
    public boolean remove(int id) {
        Student found = findById(id);
        if (found == null) return false;
        return data.remove(found);
    }

    @Override
    public LinkedList<Student> valuesInOrder() {
        // En un índice por lista no hay "in-order" real; se devuelve el orden de inserción.
        return data;
    }

    @Override
    public boolean contains(int id) {
        return findById(id) != null;
    }

    @Override
    public int size() {
        return data.size();
    }

    private Student findById(int id) {
        final Student[] result = new Student[1];

        data.traverse(new ListVisitor<Student>() {
            @Override
            public void visit(Student s) {
                if (result[0] != null) return;
                if (s != null && s.getId() == id) {
                    result[0] = s;
                }
            }
        });

        return result[0];
    }
}