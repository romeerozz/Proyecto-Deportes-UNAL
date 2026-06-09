package co.unal.deportesunal.benchmark;

import co.unal.deportesunal.benchmark.utils.Timer;
import co.unal.deportesunal.domain.Student;
import co.unal.deportesunal.domain.exception.DuplicatedIdException;
import co.unal.deportesunal.domain.exception.NotFoundException;
import co.unal.deportesunal.structure.listadt.LinkedList;
import co.unal.deportesunal.structure.index.StudentIndex;
import co.unal.deportesunal.structure.listadt.ListVisitor;

public class IndexBenchmark {
    private final Timer timer;

    public IndexBenchmark(){
        this.timer = new Timer();
    }

    public IndexBenchmark(Timer timer){
        if(timer == null) throw new IllegalArgumentException("Timer cannot be null.");
        this.timer = timer;
    }

    private void validateStudents(LinkedList<Student> students) {
        if(students == null) throw new IllegalArgumentException("Students List cannot be null.");
    }

    private void validateIndex(StudentIndex index) {
        if(index == null) throw new IllegalArgumentException("StudentIndex cannot be null.");
    }

    private void validateIds(int[] ids, String name){
        if(ids == null) throw new IllegalArgumentException(name + " cannot be null.");
    }

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