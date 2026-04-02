package co.unal.deportesunal.persistence;

import co.unal.deportesunal.domain.Student;
import co.unal.deportesunal.structure.listadt.LinkedList;

import java.io.FileNotFoundException;
import java.io.IOException;

public interface StudentRepository {
    LinkedList<Student> load();
    void save(LinkedList<Student> students) throws IOException;
}
