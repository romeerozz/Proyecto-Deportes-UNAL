package co.unal.deportesunal.controller;

import co.unal.deportesunal.domain.Student;
import co.unal.deportesunal.domain.SportEnum;
import co.unal.deportesunal.domain.exception.DataAccessException;
import co.unal.deportesunal.domain.exception.DuplicatedIdException;
import co.unal.deportesunal.domain.exception.NotFoundException;
import co.unal.deportesunal.service.StudentService;
import co.unal.deportesunal.structure.listadt.LinkedList;

import java.io.IOException;

public class AppController {

    private final StudentService studentService;

    public AppController(StudentService studentService) {
        this.studentService = studentService;
    }

    public void load() throws DataAccessException, DuplicatedIdException {
        studentService.loadFromRepository();
    }

    public void save() throws DataAccessException, IOException {
        studentService.saveToRepository();
    }

    public void registerStudent(int id, String name) throws DuplicatedIdException {
        Student s = new Student(id, name);
        studentService.registerStudent(s);
    }

    public Student findStudent(int id) throws NotFoundException {
        return studentService.findStudentById(id);
    }

    public boolean deleteStudent(int id) {
        return studentService.removeStudentById(id);
    }

    public boolean exists(int id) {
        return studentService.exists(id);
    }

    public LinkedList<Student> listStudents() {
        return studentService.listStudentsOrderedById();
    }

    public int totalStudents() {
        return studentService.totalStudents();
    }

    public boolean addPracticeSport(int studentId, SportEnum sport) throws NotFoundException {
        return studentService.addPracticeSport(studentId, sport);
    }

    public boolean removePracticeSport(int studentId, SportEnum sport) throws NotFoundException {
        return studentService.removePracticeSport(studentId, sport);
    }

    public boolean addInterestSport(int studentId, SportEnum sport) throws NotFoundException {
        return studentService.addInterestSport(studentId, sport);
    }

    public boolean removeInterestSport(int studentId, SportEnum sport) throws NotFoundException {
        return studentService.removeInterestSport(studentId, sport);
    }
}