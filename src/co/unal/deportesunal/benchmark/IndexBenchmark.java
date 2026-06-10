package co.unal.deportesunal.benchmark;

import co.unal.deportesunal.benchmark.utils.Timer;
import co.unal.deportesunal.domain.Student;
import co.unal.deportesunal.domain.exception.DuplicatedIdException;
import co.unal.deportesunal.domain.exception.NotFoundException;
import co.unal.deportesunal.structure.listadt.LinkedList;
import co.unal.deportesunal.structure.index.StudentIndex;
import co.unal.deportesunal.structure.listadt.ListVisitor;

/**
 * Ejecutor de benchmarks para las operaciones de un índice de estudiantes (StudentIndex).
 * Mide el tiempo de inserción (PUT), consulta (GET) y eliminación (REMOVE)
 * utilizando un Timer de nanosegundos.
 */
public class IndexBenchmark {
    private final Timer timer;

    /**
     * Crea un IndexBenchmark con un Timer por defecto.
     */
    public IndexBenchmark(){
        this.timer = new Timer();
    }

    /**
     * Crea un IndexBenchmark con un Timer personalizado.
     *
     * @param timer cronómetro para medir tiempos
     * @throws IllegalArgumentException si el timer es nulo
     */
    public IndexBenchmark(Timer timer){
        if(timer == null) throw new IllegalArgumentException("Timer cannot be null.");
        this.timer = timer;
    }

    /**
     * Valida que la lista de estudiantes no sea nula.
     *
     * @param students lista a validar
     * @throws IllegalArgumentException si es nula
     */
    private void validateStudents(LinkedList<Student> students) {
        if(students == null) throw new IllegalArgumentException("Students List cannot be null.");
    }

    /**
     * Valida que el índice no sea nulo.
     *
     * @param index índice a validar
     * @throws IllegalArgumentException si es nulo
     */
    private void validateIndex(StudentIndex index) {
        if(index == null) throw new IllegalArgumentException("StudentIndex cannot be null.");
    }

    /**
     * Valida que el arreglo de IDs no sea nulo.
     *
     * @param ids  arreglo a validar
     * @param name nombre descriptivo para el mensaje de error
     * @throws IllegalArgumentException si el arreglo es nulo
     */
    private void validateIds(int[] ids, String name){
        if(ids == null) throw new IllegalArgumentException(name + " cannot be null.");
    }

    /**
     * Mide el tiempo de inserción de todos los estudiantes en el índice.
     *
     * @param index    índice donde insertar
     * @param students lista de estudiantes a insertar
     * @return tiempo total en nanosegundos
     */
    public long benchPut(StudentIndex index, LinkedList<Student> students) {
        validateIndex(index);
        validateStudents(students);
        return timer.measure(new Runnable() {
            @Override
            public void run() {
                students.traverse(new ListVisitor<Student>() {
                    @Override
                    public void visit(Student student) {
                        if(student == null) return;;

                        try {
                            index.put(student.getId(), student);
                        } catch (DuplicatedIdException e){
                            throw new RuntimeException("Duplicated ID during PUT benchmark: " + student.getId(), e);
                        }
                    }
                });
            }
        });
    }

    /**
     * Mide el tiempo de consulta GET para los IDs especificados.
     *
     * @param index    índice donde consultar
     * @param queryIds arreglo de IDs a buscar
     * @return tiempo total en nanosegundos
     */
    public long benchGet(StudentIndex index, int[] queryIds) {
        validateIndex(index);
        validateIds(queryIds, "removeIds");

        return timer.measure(new Runnable() {
            @Override
            public void run() {
                for(int id : queryIds){
                    try {
                        index.get(id);
                    } catch (NotFoundException e){
                        throw  new RuntimeException("ID not found during GET benchmark: " + id);
                    }
                }
            }
        });
    }

    /**
     * Mide el tiempo de eliminación REMOVE para los IDs especificados.
     *
     * @param index     índice donde eliminar
     * @param removeIds arreglo de IDs a eliminar
     * @return tiempo total en nanosegundos
     */
    public long benchRemove(StudentIndex index, int[] removeIds) {
        validateIndex(index);
        validateIds(removeIds, "removeIds");

        return timer.measure(new Runnable() {
            @Override
            public void run() {
                for(int id : removeIds){
                    boolean removed = index.remove(id);

                    if(!removed) throw new RuntimeException("ID was not removed during DELETE benchmark: " + id);
                }
            }
        });
    }
}