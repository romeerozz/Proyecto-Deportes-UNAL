package co.unal.deportesunal.persistence;

import co.unal.deportesunal.domain.Student;
import co.unal.deportesunal.domain.exception.DataAccessException;
import co.unal.deportesunal.structure.listadt.LinkedList;

public interface StudentRepository {
    LinkedList<Student> load() throws DataAccessException;
    void save(LinkedList<Student> students) throws DataAccessException;
}