package co.unal.deportesunal.service;

import co.unal.deportesunal.domain.Student;
import co.unal.deportesunal.domain.SportEnum;
import co.unal.deportesunal.domain.exception.DuplicatedIdException;
import co.unal.deportesunal.domain.exception.NotFoundException;
import co.unal.deportesunal.structure.listadt.LinkedList;
import co.unal.deportesunal.structure.tree.StudentIndex;

public class StudentService {

    private final StudentIndex index;

    public StudentService(StudentIndex index) {
        this.index = index;
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

    public int totalStudents() {
        return index.size();
    }

    public LinkedList<Student> listStudentsOrderedById() {
        return index.valuesInOrder();
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