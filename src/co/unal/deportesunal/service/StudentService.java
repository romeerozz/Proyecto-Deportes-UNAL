package co.unal.deportesunal.service;

import co.unal.deportesunal.domain.Student;
import co.unal.deportesunal.domain.SportEnum;
import co.unal.deportesunal.domain.exception.DataAccessException;
import co.unal.deportesunal.domain.exception.DuplicatedIdException;
import co.unal.deportesunal.domain.exception.NotFoundException;
import co.unal.deportesunal.persistence.StudentRepository;
import co.unal.deportesunal.structure.listadt.LinkedList;
import co.unal.deportesunal.structure.listadt.ListVisitor;
import co.unal.deportesunal.structure.tree.StudentIndex;

import java.io.IOException;

public class StudentService {

    private final StudentIndex index;
    private final StudentRepository repo;

    public StudentService(StudentIndex index, StudentRepository repo) {
        this.index = index;
        this.repo = repo;
    }

    public void loadFromRepository() throws DataAccessException, DuplicatedIdException {
        LinkedList<Student> students = repo.load();

        students.traverse(new ListVisitor<Student>() {
            @Override
            public void visit(Student s) {
                if (s == null) return;
                index.put(s.getId(), s);
            }
        });
    }

    public void saveToRepository() throws DataAccessException, IOException {
        repo.save(index.valuesInOrder());
    }

    public void registerStudent(Student student) throws DuplicatedIdException {
        if (student == null) throw new IllegalArgumentException("Student cannot be null.");
        index.put(student.getId(), student);
    }

    public Student findStudentById(int id) throws NotFoundException {
        return index.get(id);
    }

    public boolean removeStudentById(int id) {
        return index.remove(id);
    }

    public boolean exists(int id) {
        return index.contains(id);
    }

    public LinkedList<Student> listStudentsOrderedById() {
        return index.valuesInOrder();
    }

    public int totalStudents() {
        return index.size();
    }

    public boolean addPracticeSport(int studentId, SportEnum sport) throws NotFoundException {
        Student s = index.get(studentId);
        return s.addPractice(sport);
    }

    public boolean addInterestSport(int studentId, SportEnum sport) throws NotFoundException {
        Student s = index.get(studentId);
        return s.addInterest(sport);
    }

    public boolean removePracticeSport(int studentId, SportEnum sport) throws NotFoundException {
        Student s = index.get(studentId);
        return s.removePractice(sport);
    }

    public boolean removeInterestSport(int studentId, SportEnum sport) throws NotFoundException {
        Student s = index.get(studentId);
        return s.removeInterest(sport);
    }
}