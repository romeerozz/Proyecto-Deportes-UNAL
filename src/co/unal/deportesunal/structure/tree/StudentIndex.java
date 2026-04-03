package co.unal.deportesunal.structure.tree;

import co.unal.deportesunal.domain.Student;
import co.unal.deportesunal.domain.exception.DuplicatedIdException;
import co.unal.deportesunal.domain.exception.NotFoundException;
import co.unal.deportesunal.structure.listadt.LinkedList;

public interface StudentIndex {

    /**
     * Inserta un estudiante indexado por su ID.
     * @throws DuplicatedIdException si el ID ya existe.
     */
    void put(int id, Student student) throws DuplicatedIdException;

    /**
     * Retorna el estudiante asociado a un ID.
     * @throws NotFoundException si el ID no existe.
     */
    Student get(int id) throws NotFoundException;

    /**
     * Elimina el estudiante por ID.
     * @return true si eliminó, false si el ID no existía.
     */
    boolean remove(int id);

    /**
     * Retorna todos los estudiantes (AVL/BST: en orden ascendente por ID).
     * ListStudentIndex: en orden de inserción.
     */
    LinkedList<Student> valuesInOrder();

    boolean contains(int id);

    int size();
}