package co.unal.deportesunal.ui;

import co.unal.deportesunal.benchmark.BenchmarkConfig;
import co.unal.deportesunal.benchmark.BenchmarkOperation;
import co.unal.deportesunal.benchmark.BenchmarkRunner;
import co.unal.deportesunal.controller.AppController;
import co.unal.deportesunal.domain.SportCount;
import co.unal.deportesunal.domain.SportEnum;
import co.unal.deportesunal.domain.Student;
import co.unal.deportesunal.persistence.FileConstant;
import co.unal.deportesunal.structure.listadt.ListVisitor;
import co.unal.deportesunal.structure.listadt.LinkedList;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.CompoundBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.CompoundBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class MainWindow extends JFrame {

    private static final Color COLOR_PRIMARY = new Color(0x1B, 0x5E, 0x20);
    private static final Color COLOR_PRIMARY_DARK = new Color(0x14, 0x47, 0x18);
    private static final Color COLOR_ACCENT = new Color(0xFF, 0xD5, 0x4F);
    private static final Color COLOR_BG = new Color(0xF5, 0xF5, 0xF0);
    private static final Color COLOR_CARD = Color.WHITE;
    private static final Color COLOR_TEXT = new Color(0x2D, 0x2D, 0x2D);
    private static final Color COLOR_TEXT_SECONDARY = new Color(0x75, 0x75, 0x75);
    private static final Color COLOR_BORDER = new Color(0xE0, 0xE0, 0xE0);
    private static final Color COLOR_SUCCESS = new Color(0x2E, 0x7D, 0x32);

    private static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 20);
    private static final Font FONT_BUTTON = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font FONT_MONO = new Font("Consolas", Font.PLAIN, 13);
    private static final Font FONT_STATUS = new Font("Segoe UI", Font.PLAIN, 12);

    private final AppController controller;
    private final BenchmarkRunner benchmarkRunner;
    private volatile Thread currentTask;
    private JPanel buttonPanel;
    private JLabel statusLabel;
    private JProgressBar progressBar;
    private final List<JButton> actionButtons = new ArrayList<>();
    private JPanel mainPanel;
    private JScrollPane centerScroll;
    private JTextArea outputArea;
    private String lastBenchmarkPath;

    public MainWindow(AppController controller, BenchmarkRunner benchmarkRunner) {
        super("Deportes UNAL - Sistema de Gestión Deportiva");
        this.controller = controller;
        this.benchmarkRunner = benchmarkRunner;
        initLookAndFeel();
        initUi();
    }

    private void initLookAndFeel() {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            try {
                UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            } catch (Exception ex) {
            }
        }

        UIManager.put("nimbusBase", COLOR_PRIMARY);
        UIManager.put("nimbusBlueGrey", new Color(0x60, 0x60, 0x60));
        UIManager.put("control", COLOR_BG);
        UIManager.put("text", COLOR_TEXT);
        UIManager.put("nimbusSelectionBackground", COLOR_PRIMARY);
        UIManager.put("nimbusSelectionForeground", Color.WHITE);
        UIManager.put("nimbusFocus", COLOR_ACCENT);
        UIManager.put("Table.font", FONT_MONO);
        UIManager.put("Table.alternateRowColor", new Color(0xF0, 0xF4, 0xF0));
        UIManager.put("ProgressBar.foreground", COLOR_PRIMARY);
        UIManager.put("ProgressBar.background", new Color(0xE0, 0xE0, 0xE0));
    }

    private void initUi() {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(960, 680);
        setMinimumSize(new Dimension(720, 520));
        setLocationRelativeTo(null);
        getContentPane().setBackground(COLOR_BG);

        JPanel panel = new JPanel(new BorderLayout(12, 12));
        panel.setBorder(new EmptyBorder(12, 12, 12, 12));
        panel.setBackground(COLOR_BG);

        JPanel headerPanel = createHeaderPanel();

        buttonPanel = new JPanel(new GridLayout(0, 2, 8, 8));
        buttonPanel.setOpaque(false);

        JPanel scrollWrapper = new JPanel(new BorderLayout());
        scrollWrapper.setOpaque(false);
        scrollWrapper.add(buttonPanel, BorderLayout.NORTH);

        statusLabel = new JLabel("Listo.");
        statusLabel.setFont(FONT_STATUS);
        statusLabel.setForeground(COLOR_TEXT_SECONDARY);

        progressBar = new JProgressBar();
        progressBar.setStringPainted(true);
        progressBar.setFont(FONT_STATUS);
        progressBar.setPreferredSize(new Dimension(200, 18));

        JTextArea output = new JTextArea();
        output.setEditable(false);
        output.setFont(FONT_MONO);
        output.setBackground(COLOR_CARD);
        output.setForeground(COLOR_TEXT);
        output.setBorder(new CompoundBorder(
                new LineBorder(COLOR_BORDER, 1, true),
                new EmptyBorder(8, 8, 8, 8)
        ));
        this.outputArea = output;
        JScrollPane scroll = new JScrollPane(output);
        scroll.setBorder(null);
        this.centerScroll = scroll;
        this.mainPanel = panel;

        JPanel statusPanel = new JPanel(new BorderLayout(8, 0));
        statusPanel.setOpaque(false);
        statusPanel.add(statusLabel, BorderLayout.WEST);
        statusPanel.add(progressBar, BorderLayout.EAST);

        showMainMenu(output);

        try {
            controller.load();
            output.append(">>> Datos cargados desde archivo. Total estudiantes: " + controller.totalStudents() + "\n");
        } catch (Exception ex) {
            output.append(">>> No se pudieron cargar datos al iniciar: " + ex.getMessage() + "\n");
        }

        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(scrollWrapper, BorderLayout.WEST);
        panel.add(centerScroll, BorderLayout.CENTER);
        panel.add(statusPanel, BorderLayout.SOUTH);

        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(COLOR_PRIMARY_DARK);

        JMenu menuAcciones = new JMenu("Acciones");
        menuAcciones.setForeground(Color.WHITE);
        menuAcciones.setFont(FONT_BUTTON);
        styleMenu(menuAcciones);

        menuAcciones.add(createMenuItem("Estudiantes...", e -> showStudentMenu(output)));
        menuAcciones.add(createMenuItem("Comunidades...", e -> showCommunityMenu(output)));
        menuAcciones.add(createMenuItem("Conexiones...", e -> showConnectionMenu(output)));
        menuAcciones.add(createMenuItem("Estadísticas...", e -> showStatsMenu(output)));
        menuAcciones.add(createMenuItem("Benchmarks...", e -> showBenchmarkButtons(output)));

        menuBar.add(menuAcciones);
        setJMenuBar(menuBar);

        add(panel);
        setBusy(false, "Listo.");
    }

    private JPanel createHeaderPanel() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JLabel title = new JLabel("Deportes UNAL");
        title.setFont(FONT_TITLE);
        title.setForeground(COLOR_PRIMARY);

        JLabel subtitle = new JLabel("Sistema de Gestión Deportiva");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitle.setForeground(COLOR_TEXT_SECONDARY);

        JPanel textPanel = new JPanel(new BorderLayout());
        textPanel.setOpaque(false);
        textPanel.add(title, BorderLayout.NORTH);
        textPanel.add(subtitle, BorderLayout.SOUTH);

        header.add(textPanel, BorderLayout.WEST);
        header.setBorder(new EmptyBorder(4, 4, 8, 4));

        return header;
    }

    private JMenuItem createMenuItem(String text, java.awt.event.ActionListener listener) {
        JMenuItem item = new JMenuItem(text);
        item.setFont(FONT_BUTTON);
        item.addActionListener(listener);
        item.setBackground(COLOR_CARD);
        item.setForeground(COLOR_TEXT);
        item.setBorder(new EmptyBorder(6, 12, 6, 12));
        return item;
    }

    private void styleMenu(JMenu menu) {
        menu.getPopupMenu().setBorder(new LineBorder(COLOR_BORDER, 1));
        menu.getPopupMenu().setBackground(COLOR_CARD);
    }

    private void setCenter(Component comp) {
        SwingUtilities.invokeLater(() -> {
            mainPanel.remove(centerScroll);
            if (comp instanceof JScrollPane jsp) {
                centerScroll = jsp;
            } else {
                centerScroll = new JScrollPane(comp);
            }
            centerScroll.setBorder(null);
            mainPanel.add(centerScroll, BorderLayout.CENTER);
            mainPanel.revalidate();
            mainPanel.repaint();
        });
    }

    private void showStudentsTable() {
        LinkedList<Student> students = controller.listStudents();
        String[] cols = new String[]{"ID", "Nombre", "Prácticas", "Intereses"};
        javax.swing.table.DefaultTableModel model = new javax.swing.table.DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        students.traverse(new ListVisitor<Student>() {
            @Override
            public void visit(Student s) {
                if (s == null) return;
                model.addRow(new Object[]{s.getId(), s.getName(), s.getPractice().size(), s.getInterest().size()});
            }
        });
        JTable table = new JTable(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setFont(FONT_MONO);
        table.setRowHeight(24);
        table.getTableHeader().setFont(FONT_BUTTON);
        table.getTableHeader().setBackground(COLOR_PRIMARY);
        table.getTableHeader().setForeground(Color.WHITE);
        table.setShowGrid(true);
        table.setGridColor(COLOR_BORDER);
        setCenter(new JScrollPane(table));
    }

    private void showCommunitiesTable() {
        LinkedList<LinkedList<Student>> communities = controller.getCommunities();
        String[] cols = new String[]{"Comunidad #", "Tamaño"};
        javax.swing.table.DefaultTableModel model = new javax.swing.table.DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        final int[] counter = {1};
        communities.traverse(new ListVisitor<LinkedList<Student>>() {
            @Override
            public void visit(LinkedList<Student> community) {
                if (community == null) return;
                model.addRow(new Object[]{counter[0], community.size()});
                counter[0]++;
            }
        });
        JTable table = new JTable(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setFont(FONT_MONO);
        table.setRowHeight(24);
        table.getTableHeader().setFont(FONT_BUTTON);
        table.getTableHeader().setBackground(COLOR_PRIMARY);
        table.getTableHeader().setForeground(Color.WHITE);
        setCenter(new JScrollPane(table));
    }

    private JButton createStyledButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(FONT_BUTTON);
        btn.setForeground(COLOR_TEXT);
        btn.setBackground(COLOR_CARD);
        btn.setBorder(new CompoundBorder(
                new LineBorder(COLOR_BORDER, 1, true),
                new EmptyBorder(10, 16, 10, 16)
        ));
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setOpaque(true);

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btn.setBackground(new Color(0xE8, 0xF5, 0xE9));
                btn.setBorder(new CompoundBorder(
                        new LineBorder(COLOR_PRIMARY, 1, true),
                        new EmptyBorder(10, 16, 10, 16)
                ));
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                btn.setBackground(COLOR_CARD);
                btn.setBorder(new CompoundBorder(
                        new LineBorder(COLOR_BORDER, 1, true),
                        new EmptyBorder(10, 16, 10, 16)
                ));
            }
        });

        return btn;
    }

    private void setButtons(String[] labels, Runnable[] actions) {
        if (labels == null || actions == null || labels.length != actions.length) {
            throw new IllegalArgumentException("Labels and actions must be non-null and same length.");
        }
        SwingUtilities.invokeLater(() -> {
            buttonPanel.removeAll();
            actionButtons.clear();
            for (int i = 0; i < labels.length; i++) {
                String lbl = labels[i];
                Runnable act = actions[i];
                JButton b = createStyledButton(lbl);
                b.addActionListener(e -> {
                    try {
                        act.run();
                    } catch (Exception ex) {
                        showError(ex.getMessage());
                    }
                });
                buttonPanel.add(b);
                actionButtons.add(b);
            }
            buttonPanel.revalidate();
            buttonPanel.repaint();
        });
    }

    private void exitButtonStyle(JButton btn) {
        btn.setBackground(new Color(0xDC, 0x35, 0x35));
        btn.setForeground(Color.WHITE);
        btn.setBorder(new CompoundBorder(
                new LineBorder(new Color(0xDC, 0x35, 0x35), 1, true),
                new EmptyBorder(10, 16, 10, 16)
        ));
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btn.setBackground(new Color(0xC6, 0x28, 0x28));
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                btn.setBackground(new Color(0xDC, 0x35, 0x35));
            }
        });
    }

    private void showMainMenu(JTextArea output) {
        String[] labels = new String[]{
            "Modo interactivo (CRUD)",
            "Análisis de comunidades",
            "Análisis de conexiones",
            "Análisis de estadísticas",
            "Ejecutar benchmarks",
            "Recargar desde archivo",
            "Guardar a archivo",
            "Salir"
        };
        Runnable[] actions = new Runnable[]{
            () -> showCrudMenu(output),
            () -> showCommunityButtons(output),
            () -> showConnectionButtons(output),
            () -> showStatsButtons(output),
            () -> showBenchmarkButtons(output),
            () -> { try { controller.load(); appendOutput(output, ">>> Datos recargados. Total estudiantes: " + controller.totalStudents() + "\n"); } catch (Exception ex) { showError(ex.getMessage()); } },
            () -> { try { controller.save(); appendOutput(output, ">>> Datos guardados.\n"); } catch (Exception ex) { showError(ex.getMessage()); } },
            () -> System.exit(0)
        };
        setButtons(labels, actions);
    }

    private void showCrudMenu(JTextArea output) {
        String[] labels = new String[]{"Registrar estudiante", "Consultar por ID", "Actualizar deportes", "Eliminar estudiante", "Listar estudiantes", "Volver", "Terminar operación", "Salir"};
        Runnable[] actions = new Runnable[]{
            () -> registerStudent(output),
            () -> findStudent(output),
            () -> updateSports(output),
            () -> deleteStudent(output),
            () -> { LinkedList<Student> students = controller.listStudents(); appendOutput(output, "Total: " + controller.totalStudents() + "\n"); students.traverse(new ListVisitor<Student>() { @Override public void visit(Student s) { appendOutput(output, s.toString() + "\n"); } }); },
            () -> showMainMenu(output),
            () -> cancelCurrentTask(output),
            () -> System.exit(0)
        };
        setButtons(labels, actions);
    }

    private void showCommunityButtons(JTextArea output) {
        String[] labels = new String[]{"Listar todas las comunidades", "Ver comunidad de un estudiante", "Volver", "Terminar operación", "Salir"};
        Runnable[] actions = new Runnable[]{
            () -> listAllCommunities(output),
            () -> viewStudentCommunity(output),
            () -> showMainMenu(output),
            () -> cancelCurrentTask(output),
            () -> System.exit(0)
        };
        setButtons(labels, actions);
    }

    private void showConnectionButtons(JTextArea output) {
        String[] labels = new String[]{"Conexión a deporte", "Practicantes en comunidad", "Deportes en comunidad", "Volver", "Terminar operación", "Salir"};
        Runnable[] actions = new Runnable[]{
            () -> checkConnectionToSport(output),
            () -> viewPractitionersOfSport(output),
            () -> viewAllSportsInCommunity(output),
            () -> showMainMenu(output),
            () -> cancelCurrentTask(output),
            () -> System.exit(0)
        };
        setButtons(labels, actions);
    }

    private void showStatsButtons(JTextArea output) {
        String[] labels = new String[]{"Ranking deportes", "Deporte más/menos practicado", "Contar practicantes", "Estadísticas generales", "Volver", "Terminar operación", "Salir"};
        Runnable[] actions = new Runnable[]{
            () -> showRanking(output),
            () -> showTopAndLeast(output),
            () -> countPractitioners(output),
            () -> showGeneralStats(output),
            () -> showMainMenu(output),
            () -> cancelCurrentTask(output),
            () -> System.exit(0)
        };
        setButtons(labels, actions);
    }

private void showBenchmarkButtons(JTextArea output) {
        String[] labels = new String[]{"Configuración rápida (10¹–10⁴)", "Configuración completa (10⁵–10⁸)", "Por estructura", "Por operación", "Ver últimos resultados", "Volver", "Terminar operación", "Salir"};
        Runnable[] actions = new Runnable[]{
            () -> {
                lastBenchmarkPath = FileConstant.indexBenchmarkResult("quick");
                runBackgroundTask("Benchmark rápido", output, () -> {
                    try {
                        benchmarkRunner.runAll(BenchmarkConfig.quickConfig(), new co.unal.deportesunal.benchmark.factories.IndexFactory[]{
                            new co.unal.deportesunal.benchmark.factories.ListIndexFactory(),
                            new co.unal.deportesunal.benchmark.factories.BstIndexFactory(),
                            new co.unal.deportesunal.benchmark.factories.AvlIndexFactory(),
                            new co.unal.deportesunal.benchmark.factories.HashIndexFactory()
                        });
                        appendOutput(output, "Benchmark rápido terminado.\n");
                        SwingUtilities.invokeLater(() -> showBenchmarkResults());
                    } catch (Exception ex) { throw new RuntimeException(ex); }
                });
            },
            () -> {
                lastBenchmarkPath = FileConstant.INDEX_BENCHMARK_FULL;
                runBackgroundTask("Benchmark completo", output, () -> {
                    try {
                        benchmarkRunner.runAll(BenchmarkConfig.defaultConfig(), new co.unal.deportesunal.benchmark.factories.IndexFactory[]{
                            new co.unal.deportesunal.benchmark.factories.ListIndexFactory(),
                            new co.unal.deportesunal.benchmark.factories.BstIndexFactory(),
                            new co.unal.deportesunal.benchmark.factories.AvlIndexFactory(),
                            new co.unal.deportesunal.benchmark.factories.HashIndexFactory()
                        });
                        appendOutput(output, "Benchmark completo terminado.\n");
                        SwingUtilities.invokeLater(() -> showBenchmarkResults());
                    } catch (Exception ex) { throw new RuntimeException(ex); }
                });
            },
            () -> {
                String[] structs = new String[]{"LIST", "BST", "AVL", "HASH", "Cancelar"};
                int s = JOptionPane.showOptionDialog(this, "Selecciona estructura", "Estructuras", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, structs, structs[0]);
                if (s < 0 || s == 4) return;
                co.unal.deportesunal.benchmark.factories.IndexFactory factory = switch (s) {
                    case 0 -> new co.unal.deportesunal.benchmark.factories.ListIndexFactory();
                    case 1 -> new co.unal.deportesunal.benchmark.factories.BstIndexFactory();
                    case 2 -> new co.unal.deportesunal.benchmark.factories.AvlIndexFactory();
                    case 3 -> new co.unal.deportesunal.benchmark.factories.HashIndexFactory();
                    default -> null;
                };
                if (factory == null) return;
                lastBenchmarkPath = FileConstant.indexBenchmarkResult("structure");
                runBackgroundTask("Benchmark por estructura", output, () -> {
                    try {
                        benchmarkRunner.runOperations(BenchmarkConfig.quickConfig(), new co.unal.deportesunal.benchmark.factories.IndexFactory[]{factory}, new BenchmarkOperation[]{BenchmarkOperation.PUT, BenchmarkOperation.GET, BenchmarkOperation.REMOVE}, lastBenchmarkPath, false);
                        appendOutput(output, "Benchmark por estructura completado.\n");
                        SwingUtilities.invokeLater(() -> showBenchmarkResults());
                    } catch (Exception ex) { throw new RuntimeException(ex); }
                });
            },
            () -> {
                String[] ops = new String[]{"PUT", "GET", "REMOVE", "Todos", "Cancelar"};
                int o = JOptionPane.showOptionDialog(this, "Selecciona operación", "Operaciones", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, ops, ops[0]);
                if (o < 0 || o == 4) return;
                BenchmarkOperation[] selectedOps = switch (o) {
                    case 0 -> new BenchmarkOperation[]{BenchmarkOperation.PUT};
                    case 1 -> new BenchmarkOperation[]{BenchmarkOperation.GET};
                    case 2 -> new BenchmarkOperation[]{BenchmarkOperation.REMOVE};
                    default -> new BenchmarkOperation[]{BenchmarkOperation.PUT, BenchmarkOperation.GET, BenchmarkOperation.REMOVE};
                };
                lastBenchmarkPath = FileConstant.indexBenchmarkResult("operation");
                runBackgroundTask("Benchmark por operación", output, () -> {
                    try {
                        benchmarkRunner.runOperations(BenchmarkConfig.quickConfig(), new co.unal.deportesunal.benchmark.factories.IndexFactory[]{
                            new co.unal.deportesunal.benchmark.factories.ListIndexFactory(),
                            new co.unal.deportesunal.benchmark.factories.BstIndexFactory(),
                            new co.unal.deportesunal.benchmark.factories.AvlIndexFactory(),
                            new co.unal.deportesunal.benchmark.factories.HashIndexFactory()
                        }, selectedOps, lastBenchmarkPath, false);
                        appendOutput(output, "Benchmark por operación completado.\n");
                        SwingUtilities.invokeLater(() -> showBenchmarkResults());
                    } catch (Exception ex) { throw new RuntimeException(ex); }
                });
            },
            () -> { if (lastBenchmarkPath != null) showBenchmarkResults(); else showInfo("Ejecuta un benchmark primero."); },
            () -> showMainMenu(output),
            () -> cancelCurrentTask(output),
            () -> System.exit(0)
        };
        setButtons(labels, actions);
    }

    private void showBenchmarkResults() {
        if (lastBenchmarkPath == null) return;
        try (BufferedReader br = new BufferedReader(new FileReader(lastBenchmarkPath))) {
            String headerLine = br.readLine();
            if (headerLine == null) { showInfo("El archivo CSV está vacío."); return; }
            String[] cols = headerLine.split(",");
            DefaultTableModel model = new DefaultTableModel(cols, 0) {
                @Override public boolean isCellEditable(int row, int col) { return false; }
            };
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] vals = line.split(",");
                model.addRow(vals);
            }
            JTable table = new JTable(model);
            table.setFont(FONT_MONO);
            table.setRowHeight(22);
            table.getTableHeader().setFont(FONT_BUTTON);
            table.getTableHeader().setBackground(COLOR_PRIMARY);
            table.getTableHeader().setForeground(Color.WHITE);
            table.setShowGrid(true);
            table.setGridColor(COLOR_BORDER);
            setCenter(new JScrollPane(table));
        } catch (IOException ex) {
            showError("No se pudo leer el archivo de resultados: " + ex.getMessage());
        }
    }

    private void showStudentMenu(JTextArea output) {
        String[] options = new String[]{"Registrar", "Consultar por ID", "Actualizar deportes", "Eliminar", "Listar", "Cancelar"};
        int sel = JOptionPane.showOptionDialog(this, "Estudiantes - elige acción", "Estudiantes", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
        if (sel < 0 || sel == 5) return;
        switch (sel) {
            case 0 -> registerStudent(output);
            case 1 -> findStudent(output);
            case 2 -> updateSports(output);
            case 3 -> deleteStudent(output);
            case 4 -> {
                LinkedList<Student> students = controller.listStudents();
                output.append("Total: " + controller.totalStudents() + "\n");
                students.traverse(new ListVisitor<Student>() {
                    @Override
                    public void visit(Student s) {
                        output.append(s.toString() + "\n");
                    }
                });
            }
        }
    }

    private void showCommunityMenu(JTextArea output) {
        String[] options = new String[]{"Listar comunidades", "Ver comunidad por estudiante", "Resumen total", "Cancelar"};
        int sel = JOptionPane.showOptionDialog(this, "Comunidades - elige acción", "Comunidades", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
        if (sel < 0 || sel == 3) return;
        switch (sel) {
            case 0 -> listAllCommunities(output);
            case 1 -> viewStudentCommunity(output);
            case 2 -> {
                try {
                    int total = controller.getTotalCommunities();
                    output.append("Total comunidades: " + total + "\n");
                } catch (Exception ex) { showError(ex.getMessage()); }
            }
        }
    }

    private void showConnectionMenu(JTextArea output) {
        String[] options = new String[]{"Conexión a deporte", "Practicantes en comunidad", "Deportes en comunidad", "Cancelar"};
        int sel = JOptionPane.showOptionDialog(this, "Conexiones - elige acción", "Conexiones", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
        if (sel < 0 || sel == 3) return;
        switch (sel) {
            case 0 -> checkConnectionToSport(output);
            case 1 -> viewPractitionersOfSport(output);
            case 2 -> viewAllSportsInCommunity(output);
        }
    }

    private void showStatsMenu(JTextArea output) {
        String[] options = new String[]{"Ranking deportes", "Deporte más/menos practicado", "Contar practicantes", "Estadísticas generales", "Cancelar"};
        int sel = JOptionPane.showOptionDialog(this, "Estadísticas - elige acción", "Estadísticas", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
        if (sel < 0 || sel == 4) return;
        switch (sel) {
            case 0 -> showRanking(output);
            case 1 -> showTopAndLeast(output);
            case 2 -> countPractitioners(output);
            case 3 -> showGeneralStats(output);
        }
    }

    private void runBackgroundTask(String status, JTextArea output, Runnable task) {
        setBusy(true, status);
        Thread worker = new Thread(() -> {
            try {
                task.run();
            } catch (Exception ex) {
                SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(MainWindow.this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE));
            } finally {
                currentTask = null;
                SwingUtilities.invokeLater(() -> setBusy(false, "Listo."));
            }
        });
        currentTask = worker;
        worker.start();
    }

    private void setBusy(boolean busy, String status) {
        SwingUtilities.invokeLater(() -> {
            statusLabel.setText(status);
            progressBar.setIndeterminate(busy);
            progressBar.setString(busy ? "Procesando..." : "");
            for (JButton button : actionButtons) {
                String text = button.getText();
                boolean keepEnabled = "Cancelar operación".equals(text) || "Terminar operación".equals(text) || "Salir".equals(text);
                button.setEnabled(busy ? keepEnabled : true);
            }
        });
    }

    private void appendOutput(JTextArea output, String message) {
        SwingUtilities.invokeLater(() -> output.append(message));
    }

    private void registerStudent(JTextArea output) {
        Integer id = readInt("ID del estudiante: ");
        if (id == null) return;

        String name = promptText("Nombre del estudiante:");
        if (name == null || name.trim().isEmpty()) {
            showInfo("Nombre vacío o cancelado.");
            return;
        }

        try {
            controller.registerStudent(id, name.trim());
            output.append(">>> Estudiante registrado: " + id + " - " + name.trim() + "\n");
        } catch (Exception ex) {
            showError(ex.getMessage());
        }
    }

    private void findStudent(JTextArea output) {
        Integer id = readInt("ID a consultar:");
        if (id == null) return;
        try {
            Student student = controller.findStudent(id);
            output.append(">>> " + formatStudent(student) + "\n");
        } catch (Exception ex) {
            showError(ex.getMessage());
        }
    }

    private void deleteStudent(JTextArea output) {
        Integer id = readInt("ID a eliminar:");
        if (id == null) return;
        boolean removed = controller.deleteStudent(id);
        output.append(removed ? ">>> Eliminado: " + id + "\n" : ">>> No existía: " + id + "\n");
    }

    private void updateSports(JTextArea output) {
        Integer id = readInt("ID del estudiante:");
        if (id == null) return;

        try {
            controller.findStudent(id);
        } catch (Exception ex) {
            showError(ex.getMessage());
            return;
        }

        String[] options = new String[]{"Agregar práctica", "Remover práctica", "Agregar interés", "Remover interés", "Cancelar"};
        int option = JOptionPane.showOptionDialog(this, "Elige acción", "Actualizar deportes", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
        if (option < 0 || option == 4) return;

        SportEnum sport = readSport();
        if (sport == null) return;

        try {
            boolean result = switch (option) {
                case 0 -> controller.addPracticeSport(id, sport);
                case 1 -> controller.removePracticeSport(id, sport);
                case 2 -> controller.addInterestSport(id, sport);
                case 3 -> controller.removeInterestSport(id, sport);
                default -> false;
            };
            output.append((result ? ">>> Operación exitosa" : ">>> Sin cambios") + " para " + sport.displayName() + "\n");
        } catch (Exception ex) {
            showError(ex.getMessage());
        }
    }

    private void listAllCommunities(JTextArea output) {
        showCommunitiesTable();
    }

    private void viewStudentCommunity(JTextArea output) {
        Integer id = readInt("ID del estudiante:");
        if (id == null) return;

        try {
            Student student = controller.findStudent(id);
            LinkedList<Student> community = controller.getStudentCommunity(id);

            String[] cols = new String[]{"ID", "Nombre", "Eres tú"};
            javax.swing.table.DefaultTableModel model = new javax.swing.table.DefaultTableModel(cols, 0) {
                @Override public boolean isCellEditable(int row, int col) { return false; }
            };
            community.traverse(new ListVisitor<Student>() {
                @Override
                public void visit(Student s) {
                    if (s != null) {
                        model.addRow(new Object[]{s.getId(), s.getName(), s.getId() == id ? "✓" : ""});
                    }
                }
            });
            JTable table = new JTable(model);
            table.setFont(FONT_MONO);
            table.setRowHeight(24);
            table.getTableHeader().setFont(FONT_BUTTON);
            table.getTableHeader().setBackground(COLOR_PRIMARY);
            table.getTableHeader().setForeground(Color.WHITE);
            table.setShowGrid(true);
            table.setGridColor(COLOR_BORDER);
            setCenter(new JScrollPane(table));
        } catch (Exception ex) {
            showError(ex.getMessage());
        }
    }

    private void checkConnectionToSport(JTextArea output) {
        Integer id = readInt("Tu ID:");
        if (id == null) return;

        SportEnum sport = readSport();
        if (sport == null) return;

        try {
            boolean hasConnection = controller.hasConnectionToSport(id, sport);
            output.append(hasConnection
                    ? ">>> SI hay conexión hacia " + sport.displayName() + "\n"
                    : ">>> NO hay conexión hacia " + sport.displayName() + "\n");
            if (hasConnection) {
                LinkedList<Student> practitioners = controller.getPractitionersInCommunity(id, sport);

                String[] cols = new String[]{"ID", "Nombre"};
                javax.swing.table.DefaultTableModel model = new javax.swing.table.DefaultTableModel(cols, 0) {
                    @Override public boolean isCellEditable(int row, int col) { return false; }
                };
                practitioners.traverse(new ListVisitor<Student>() {
                    @Override
                    public void visit(Student s) {
                        if (s != null) model.addRow(new Object[]{s.getId(), s.getName()});
                    }
                });
                JTable table = new JTable(model);
                table.setFont(FONT_MONO);
                table.setRowHeight(24);
                table.getTableHeader().setFont(FONT_BUTTON);
                table.getTableHeader().setBackground(COLOR_PRIMARY);
                table.getTableHeader().setForeground(Color.WHITE);
                table.setShowGrid(true);
                table.setGridColor(COLOR_BORDER);
                setCenter(new JScrollPane(table));
            }
        } catch (Exception ex) {
            showError(ex.getMessage());
        }
    }

    private void viewPractitionersOfSport(JTextArea output) {
        Integer id = readInt("Tu ID:");
        if (id == null) return;

        SportEnum sport = readSport();
        if (sport == null) return;

        try {
            LinkedList<Student> practitioners = controller.getPractitionersInCommunity(id, sport);

            String[] cols = new String[]{"ID", "Nombre"};
            javax.swing.table.DefaultTableModel model = new javax.swing.table.DefaultTableModel(cols, 0) {
                @Override public boolean isCellEditable(int row, int col) { return false; }
            };
            practitioners.traverse(new ListVisitor<Student>() {
                @Override
                public void visit(Student s) {
                    if (s != null) model.addRow(new Object[]{s.getId(), s.getName()});
                }
            });
            JTable table = new JTable(model);
            table.setFont(FONT_MONO);
            table.setRowHeight(24);
            table.getTableHeader().setFont(FONT_BUTTON);
            table.getTableHeader().setBackground(COLOR_PRIMARY);
            table.getTableHeader().setForeground(Color.WHITE);
            table.setShowGrid(true);
            table.setGridColor(COLOR_BORDER);
            setCenter(new JScrollPane(table));
        } catch (Exception ex) {
            showError(ex.getMessage());
        }
    }

    private void viewAllSportsInCommunity(JTextArea output) {
        Integer id = readInt("Tu ID:");
        if (id == null) return;

        try {
            LinkedList<SportEnum> sports = controller.getSportsInCommunity(id);

            String[] cols = new String[]{"Deporte", "Practicantes"};
            javax.swing.table.DefaultTableModel model = new javax.swing.table.DefaultTableModel(cols, 0) {
                @Override public boolean isCellEditable(int row, int col) { return false; }
            };
            sports.traverse(new ListVisitor<SportEnum>() {
                @Override
                public void visit(SportEnum sport) {
                    if (sport != null) {
                        int practitioners = controller.countPractitionersInCommunity(id, sport);
                        model.addRow(new Object[]{sport.displayName(), practitioners});
                    }
                }
            });
            JTable table = new JTable(model);
            table.setFont(FONT_MONO);
            table.setRowHeight(24);
            table.getTableHeader().setFont(FONT_BUTTON);
            table.getTableHeader().setBackground(COLOR_PRIMARY);
            table.getTableHeader().setForeground(Color.WHITE);
            table.setShowGrid(true);
            table.setGridColor(COLOR_BORDER);
            setCenter(new JScrollPane(table));
        } catch (Exception ex) {
            showError(ex.getMessage());
        }
    }

    private void showRanking(JTextArea output) {
        try {
            LinkedList<SportCount> ranking = controller.getRankingSports();
            String[] cols = new String[]{"#", "Deporte", "Practicantes"};
            javax.swing.table.DefaultTableModel model = new javax.swing.table.DefaultTableModel(cols, 0) {
                @Override public boolean isCellEditable(int row, int col) { return false; }
            };
            final int[] pos = {1};
            ranking.traverse(new ListVisitor<SportCount>() {
                @Override
                public void visit(SportCount sc) {
                    if (sc != null) {
                        model.addRow(new Object[]{pos[0]++, sc.getSport().displayName(), sc.getCount()});
                    }
                }
            });
            JTable table = new JTable(model);
            table.setFont(FONT_MONO);
            table.setRowHeight(24);
            table.getTableHeader().setFont(FONT_BUTTON);
            table.getTableHeader().setBackground(COLOR_PRIMARY);
            table.getTableHeader().setForeground(Color.WHITE);
            table.setShowGrid(true);
            table.setGridColor(COLOR_BORDER);
            setCenter(new JScrollPane(table));
        } catch (Exception ex) {
            showError(ex.getMessage());
        }
    }

    private void showTopAndLeast(JTextArea output) {
        try {
            SportCount top = controller.getMostPracticedSport();
            SportCount least = controller.getLeastPracticedSport();

            JPanel panel = new JPanel(new GridBagLayout());
            panel.setBackground(COLOR_BG);
            GridBagConstraints c = new GridBagConstraints();
            c.insets = new Insets(8, 12, 8, 12);
            c.fill = GridBagConstraints.HORIZONTAL;
            c.gridwidth = GridBagConstraints.REMAINDER;

            JLabel title = new JLabel("Estadística rápida");
            title.setFont(FONT_TITLE);
            title.setForeground(COLOR_PRIMARY);
            c.anchor = GridBagConstraints.WEST;
            panel.add(title, c);

            JPanel card1 = createStatCard("Deporte más practicado",
                    top == null ? "—" : top.getSport().displayName(),
                    top == null ? "—" : String.valueOf(top.getCount()));
            c.insets = new Insets(12, 12, 4, 12);
            panel.add(card1, c);

            JPanel card2 = createStatCard("Deporte menos practicado",
                    least == null ? "—" : least.getSport().displayName(),
                    least == null ? "—" : String.valueOf(least.getCount()));
            c.insets = new Insets(4, 12, 12, 12);
            panel.add(card2, c);

            setCenter(new JScrollPane(panel));
        } catch (Exception ex) {
            showError(ex.getMessage());
        }
    }

    private JPanel createStatCard(String label, String sport, String count) {
        JPanel card = new JPanel(new BorderLayout(8, 4));
        card.setBackground(COLOR_CARD);
        card.setBorder(new CompoundBorder(
                new LineBorder(COLOR_BORDER, 1, true),
                new EmptyBorder(14, 18, 14, 18)
        ));

        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lbl.setForeground(COLOR_TEXT_SECONDARY);

        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        row.setOpaque(false);
        JLabel sportLbl = new JLabel(sport);
        sportLbl.setFont(new Font("Segoe UI", Font.BOLD, 18));
        sportLbl.setForeground(COLOR_PRIMARY);
        JLabel countLbl = new JLabel(count + " estudiantes");
        countLbl.setFont(FONT_BUTTON);
        countLbl.setForeground(COLOR_SUCCESS);
        row.add(sportLbl);
        row.add(countLbl);

        card.add(lbl, BorderLayout.NORTH);
        card.add(row, BorderLayout.CENTER);

        return card;
    }

    private void countPractitioners(JTextArea output) {
        SportEnum sport = readSport();
        if (sport == null) return;

        try {
            int count = controller.getPractitionersCount(sport);
            output.append("Practicantes de " + sport.displayName() + ": " + count + "\n");
        } catch (Exception ex) {
            showError(ex.getMessage());
        }
    }

    private void showGeneralStats(JTextArea output) {
        try {
            int totalStudents = controller.totalStudents();
            LinkedList<SportCount> ranking = controller.getRankingSports();
            int totalSports = ranking.size();
            SportCount top = controller.getMostPracticedSport();

            JPanel panel = new JPanel(new GridBagLayout());
            panel.setBackground(COLOR_BG);
            GridBagConstraints c = new GridBagConstraints();
            c.insets = new Insets(8, 12, 8, 12);
            c.fill = GridBagConstraints.HORIZONTAL;
            c.gridwidth = GridBagConstraints.REMAINDER;

            JLabel title = new JLabel("Estadísticas Generales");
            title.setFont(FONT_TITLE);
            title.setForeground(COLOR_PRIMARY);
            c.anchor = GridBagConstraints.WEST;
            panel.add(title, c);

            c.insets = new Insets(6, 12, 6, 12);
            panel.add(createStatCard("Total de estudiantes", String.valueOf(totalStudents), "estudiantes"), c);
            panel.add(createStatCard("Deportes practicados", String.valueOf(totalSports), "deportes"), c);
            if (top != null) {
                panel.add(createStatCard("Deporte más practicado", top.getSport().displayName(), top.getCount() + " estudiantes"), c);
            }

            setCenter(new JScrollPane(panel));
        } catch (Exception ex) {
            showError(ex.getMessage());
        }
    }

    private void cancelCurrentTask(JTextArea output) {
        terminateCurrentTask();
    }

    private void terminateCurrentTask() {
        Thread task = currentTask;
        if (task == null) {
            appendOutput("No hay operación en curso.\n");
            return;
        }
        task.interrupt();
        appendOutput("Cancelacion solicitada.\n");
        setBusy(true, "Cancelando operación...");
        new Thread(() -> {
            try {
                task.join(5000);
                if (task.isAlive()) {
                    appendOutput("La operación no terminó en 5s. Puedes intentar nuevamente.\n");
                } else {
                    appendOutput("Operación terminada.\n");
                }
            } catch (InterruptedException e) {
                appendOutput("Espera de terminación interrumpida.\n");
            } finally {
                currentTask = null;
                SwingUtilities.invokeLater(() -> setBusy(false, "Listo."));
            }
        }).start();
    }

    private void appendOutput(String message) {
        if (outputArea == null) return;
        SwingUtilities.invokeLater(() -> outputArea.append(message));
    }

    private Integer readInt(String prompt) {
        String raw = promptText(prompt);
        if (raw == null) return null;
        try {
            return Integer.valueOf(raw.trim());
        } catch (NumberFormatException ex) {
            showError("Numero invalido: " + raw);
            return null;
        }
    }

    private String promptText(String prompt) {
        return JOptionPane.showInputDialog(this, prompt);
    }

    private SportEnum readSport() {
        SportEnum[] values = SportEnum.values();
        JComboBox<String> combo = new JComboBox<>();
        for (SportEnum sp : values) {
            combo.addItem(sp.displayName() + " (" + sp.name() + ")");
        }
        combo.setSelectedIndex(0);
        JPanel panel = new JPanel(new BorderLayout(6, 6));
        panel.setOpaque(false);
        panel.add(new JLabel("Selecciona un deporte:"), BorderLayout.NORTH);
        panel.add(combo, BorderLayout.CENTER);
        panel.setPreferredSize(new Dimension(260, 60));
        int result = JOptionPane.showConfirmDialog(this, panel, "Deportes", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result != JOptionPane.OK_OPTION) return null;
        int idx = combo.getSelectedIndex();
        if (idx < 0 || idx >= values.length) return null;
        return values[idx];
    }

    private String formatStudent(Student student) {
        if (student == null) return "<null>";
        return student.getId() + " | " + student.getName();
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void showInfo(String message) {
        JOptionPane.showMessageDialog(this, message, "Informacion", JOptionPane.INFORMATION_MESSAGE);
    }
}