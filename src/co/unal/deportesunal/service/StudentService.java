package co.unal.deportesunal.service;

import co.unal.deportesunal.domain.Student;
import co.unal.deportesunal.domain.SportEnum;
import co.unal.deportesunal.domain.exception.DataAccessException;
import co.unal.deportesunal.domain.exception.DuplicatedIdException;
import co.unal.deportesunal.domain.exception.NotFoundException;
import co.unal.deportesunal.persistence.StudentRepository;
import co.unal.deportesunal.structure.listadt.LinkedList;
import co.unal.deportesunal.structure.listadt.ListVisitor;
import co.unal.deportesunal.structure.index.StudentIndex;

import java.io.IOException;

/**
 * StudentService gestiona el ciclo de vida de los estudiantes: carga,
 * registro, consulta, eliminación y modificación de sus deportes
 * (práctica e interés). Actúa como fachada entre las capas de
 * persistencia (repositorio) e indexación (árbol).
 */
public class StudentService {

    private final StudentIndex index;
    private final StudentRepository repo;

    /**
     * Construye el servicio con las dependencias de indexación y persistencia.
     * @param index  índice de estudiantes (árbol binario de búsqueda)
     * @param repo   repositorio de persistencia (archivo)
     */
    public StudentService(StudentIndex index, StudentRepository repo) {
        this.index = index;
        this.repo = repo;
    }

    /**
     * Carga todos los estudiantes desde el repositorio y los inserta en el índice.
     * @throws DataAccessException  si ocurre un error de lectura
     * @throws DuplicatedIdException si hay dos estudiantes con el mismo ID
     */
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

    /**
     * Guarda todos los estudiantes del índice en el repositorio.
     * @throws DataAccessException si ocurre un error de escritura
     * @throws IOException         si ocurre un error de E/S
     */
    public void saveToRepository() throws DataAccessException, IOException {
        repo.save(index.valuesInOrder());
    }

    /**
     * Registra un nuevo estudiante en el índice.
     * @param student estudiante a registrar
     * @throws DuplicatedIdException si ya existe un estudiante con el mismo ID
     */
    public void registerStudent(Student student) throws DuplicatedIdException {
        if (student == null) throw new IllegalArgumentException("Student cannot be null.");
        index.put(student.getId(), student);
    }

    /**
     * Busca un estudiante por su ID.
     * @param id identificador del estudiante
     * @return Student encontrado
     * @throws NotFoundException si no existe estudiante con ese ID
     */
    public Student findStudentById(int id) throws NotFoundException {
        return index.get(id);
    }

    /**
     * Elimina un estudiante del índice por su ID.
     * @param id identificador del estudiante
     * @return true si se eliminó correctamente, false si no existía
     */
    public boolean removeStudentById(int id) {
        return index.remove(id);
    }

    /**
     * Verifica si existe un estudiante con el ID dado.
     * @param id identificador del estudiante
     * @return true si existe, false en caso contrario
     */
    public boolean exists(int id) {
        return index.contains(id);
    }

    /**
     * Retorna todos los estudiantes ordenados por ID (recorrido in-order del árbol).
     * @return LinkedList<Student> con todos los estudiantes
     */
    public LinkedList<Student> listStudentsOrderedById() {
        return index.valuesInOrder();
    }

    /**
     * Retorna el número total de estudiantes registrados.
     * @return cantidad de estudiantes
     */
    public int totalStudents() {
        return index.size();
    }

    /**
     * Agrega un deporte a la lista de prácticas del estudiante.
     * @param studentId ID del estudiante
     * @param sport     deporte a agregar
     * @return true si se agregó correctamente
     * @throws NotFoundException si el estudiante no existe
     */
    public boolean addPracticeSport(int studentId, SportEnum sport) throws NotFoundException {
        Student s = index.get(studentId);
        return s.addPractice(sport);
    }

    /**
     * Agrega un deporte a la lista de intereses del estudiante.
     * @param studentId ID del estudiante
     * @param sport     deporte a agregar
     * @return true si se agregó correctamente
     * @throws NotFoundException si el estudiante no existe
     */
    public boolean addInterestSport(int studentId, SportEnum sport) throws NotFoundException {
        Student s = index.get(studentId);
        return s.addInterest(sport);
    }

    /**
     * Elimina un deporte de la lista de prácticas del estudiante.
     * @param studentId ID del estudiante
     * @param sport     deporte a eliminar
     * @return true si se eliminó correctamente
     * @throws NotFoundException si el estudiante no existe
     */
    public boolean removePracticeSport(int studentId, SportEnum sport) throws NotFoundException {
        Student s = index.get(studentId);
        return s.removePractice(sport);
    }

    /**
     * Elimina un deporte de la lista de intereses del estudiante.
     * @param studentId ID del estudiante
     * @param sport     deporte a eliminar
     * @return true si se eliminó correctamente
     * @throws NotFoundException si el estudiante no existe
     */
    public boolean removeInterestSport(int studentId, SportEnum sport) throws NotFoundException {
        Student s = index.get(studentId);
        return s.removeInterest(sport);
    }
}