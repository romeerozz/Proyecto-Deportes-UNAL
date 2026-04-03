package co.unal.deportesunal.service;

import co.unal.deportesunal.domain.Student;
import co.unal.deportesunal.structure.listadt.LinkedList;
import co.unal.deportesunal.structure.tree.StudentIndex;

public class StudentService {
	private final StudentIndex studentIndex;

	public StudentService(StudentIndex studentIndex) {
		this.studentIndex = studentIndex;
	}

	public void registerStudent(Student student) {
		studentIndex.put(student.getID(), student);
	}

	public Student findStudentById(int id) {
		return studentIndex.get(id);
	}

	public boolean removeStudentById(int id) {
		return studentIndex.remove(id);
	}

	public boolean exists(int id) {
		return studentIndex.contains(id);
	}

	public LinkedList<Student> listStudentsOrderedById() {
		return studentIndex.valuesInOrder();
	}

	public int totalStudents() {
		return studentIndex.size();
	}
}
