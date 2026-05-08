package co.unal.deportesunal;

import co.unal.deportesunal.benchmark.BenchmarkRunner;
import co.unal.deportesunal.controller.AppController;
import co.unal.deportesunal.persistence.StudentRepository;
import co.unal.deportesunal.persistence.TxtStudentRepository;
import co.unal.deportesunal.service.StudentService;
import co.unal.deportesunal.structure.index.AvlIndex;
import co.unal.deportesunal.structure.index.StudentIndex;
import co.unal.deportesunal.ui.ConsoleUi;

public class AppMain {
    public static void main(String[] args) {
        //StudentIndex index = new BstIndex();
        StudentIndex index = new AvlIndex();
        //StudentIndex index = new ListStudentIndex();
        StudentRepository repo = new TxtStudentRepository();
        StudentService studentService= new StudentService(index, repo);
        AppController controller = new AppController(studentService);
        BenchmarkRunner benchmarkRunner = new BenchmarkRunner();
        ConsoleUi ui = new ConsoleUi(controller, benchmarkRunner);
        ui.run();
    }
}