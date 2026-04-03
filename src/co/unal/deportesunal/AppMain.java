package co.unal.deportesunal;

import co.unal.deportesunal.controller.AppController;
import co.unal.deportesunal.persistence.StudentRepository;
import co.unal.deportesunal.persistence.TxtStudentRepository;
import co.unal.deportesunal.service.StudentService;
import co.unal.deportesunal.structure.tree.ListStudentIndex;
import co.unal.deportesunal.structure.tree.StudentIndex;
import co.unal.deportesunal.ui.ConsoleUi;

public class AppMain {
    public static void main(String[] args) {
        StudentIndex index = new ListStudentIndex();
        StudentRepository repo = new TxtStudentRepository();
        StudentService studentService= new StudentService(index, repo);
        AppController controller = new AppController(studentService);
        ConsoleUi ui = new ConsoleUi(controller);
        ui.run();
    }
}