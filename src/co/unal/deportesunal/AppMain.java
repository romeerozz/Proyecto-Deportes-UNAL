package co.unal.deportesunal;

import co.unal.deportesunal.benchmark.BenchmarkRunner;
import co.unal.deportesunal.controller.AppController;
import co.unal.deportesunal.persistence.StudentRepository;
import co.unal.deportesunal.persistence.TxtStudentRepository;
import co.unal.deportesunal.service.StudentService;
import co.unal.deportesunal.structure.index.AvlIndex;
import co.unal.deportesunal.structure.index.BstIndex;
import co.unal.deportesunal.structure.index.HashStudentIndex;
import co.unal.deportesunal.structure.index.ListIndex;
import co.unal.deportesunal.structure.index.StudentIndex;
import co.unal.deportesunal.ui.ConsoleUi;
import co.unal.deportesunal.ui.MainWindow;

public class AppMain {
        public static void main(String[] args) {
                StudentIndex index = new HashStudentIndex();
                //StudentIndex index = new BstIndex();
                //StudentIndex index = new AvlIndex();
                //StudentIndex index = new ListIndex();
                StudentRepository repo = new TxtStudentRepository();
                StudentService studentService= new StudentService(index, repo);
                AppController controller = new AppController(studentService);
                BenchmarkRunner benchmarkRunner = new BenchmarkRunner();

                // Si se pasa argumento "cli" se usa consola, si no se inicia GUI.
                if (args != null && args.length > 0 && "cli".equalsIgnoreCase(args[0])) {
                        ConsoleUi ui = new ConsoleUi(controller, benchmarkRunner);
                        ui.run();
                } else {
                        javax.swing.SwingUtilities.invokeLater(new Runnable() {
                                public void run() {
                                        MainWindow mw = new MainWindow(controller, benchmarkRunner);
                                        mw.setVisible(true);
                                }
                        });
                }
    }
}