package co.unal.deportesunal.ui;

import co.unal.deportesunal.benchmark.BenchmarkConfig;
import co.unal.deportesunal.benchmark.BenchmarkOperation;
import co.unal.deportesunal.benchmark.BenchmarkRunner;
import co.unal.deportesunal.benchmark.GraphBenchmarkRunner;
import co.unal.deportesunal.controller.AppController;
import co.unal.deportesunal.domain.SportCount;
import co.unal.deportesunal.domain.SportEnum;
import co.unal.deportesunal.domain.Student;
import co.unal.deportesunal.structure.listadt.ListVisitor;
import co.unal.deportesunal.structure.listadt.LinkedList;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

public class MainWindow extends JFrame {

	private final AppController controller;
	private final BenchmarkRunner benchmarkRunner;
	private final GraphBenchmarkRunner graphBenchmarkRunner;
	private volatile Thread currentTask;
	private JPanel buttonPanel;
	private JLabel statusLabel;
	private JProgressBar progressBar;
	private final List<JButton> actionButtons = new ArrayList<>();
	private JPanel mainPanel;
	private JScrollPane centerScroll;
	private JTextArea outputArea;

	public MainWindow(AppController controller, BenchmarkRunner benchmarkRunner) {
		this(controller, benchmarkRunner, new GraphBenchmarkRunner());
	}

	private BenchmarkConfig smallConfig() {
		return new BenchmarkConfig(new int[]{100, 500}, 1, 42L, 1, 10, 0, 100);
	}

	private void showBenchmarkMenu(JTextArea output) {
		String[] options = new String[]{"Ejecutar todos (rápido)", "Ejecutar todos (corto)", "Por estructura", "Por operación", "Cancelar"};
		int sel = JOptionPane.showOptionDialog(this, "Selecciona una opción de benchmark", "Benchmarks", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
		if (sel < 0 || sel == 4) return;
		switch (sel) {
			case 0 -> runBackgroundTask("Benchmarks (rápido)", output, () -> {
				try {
					benchmarkRunner.runAll(BenchmarkConfig.quickConfig(), new co.unal.deportesunal.benchmark.factories.IndexFactory[]{
						new co.unal.deportesunal.benchmark.factories.ListIndexFactory(),
						new co.unal.deportesunal.benchmark.factories.BstIndexFactory(),
						new co.unal.deportesunal.benchmark.factories.AvlIndexFactory(),
						new co.unal.deportesunal.benchmark.factories.HashIndexFactory()
					});
					appendOutput(output, "Benchmarks (rápido) completados.\n");
				} catch (Exception ex) {
					throw new RuntimeException(ex);
				}
			});

			case 1 -> runBackgroundTask("Benchmarks (corto)", output, () -> {
				try {
					benchmarkRunner.runAll(smallConfig(), new co.unal.deportesunal.benchmark.factories.IndexFactory[]{
						new co.unal.deportesunal.benchmark.factories.ListIndexFactory(),
						new co.unal.deportesunal.benchmark.factories.BstIndexFactory(),
						new co.unal.deportesunal.benchmark.factories.AvlIndexFactory(),
						new co.unal.deportesunal.benchmark.factories.HashIndexFactory()
					});
					appendOutput(output, "Benchmarks (corto) completados.\n");
				} catch (Exception ex) {
					throw new RuntimeException(ex);
				}
			});

			case 2 -> {
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
				runBackgroundTask("Benchmark por estructura", output, () -> {
					try {
						benchmarkRunner.runOperations(BenchmarkConfig.quickConfig(), new co.unal.deportesunal.benchmark.factories.IndexFactory[]{factory}, new BenchmarkOperation[]{BenchmarkOperation.PUT, BenchmarkOperation.GET, BenchmarkOperation.REMOVE}, co.unal.deportesunal.persistence.FileConstant.indexBenchmarkResult("gui_quick"), false);
						appendOutput(output, "Benchmark por estructura completado.\n");
					} catch (Exception ex) {
						throw new RuntimeException(ex);
					}
				});
			}

			case 3 -> {
				String[] ops = new String[]{"PUT", "GET", "REMOVE", "Todos", "Cancelar"};
				int o = JOptionPane.showOptionDialog(this, "Selecciona operación", "Operaciones", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, ops, ops[0]);
				if (o < 0 || o == 4) return;
				BenchmarkOperation[] selectedOps = switch (o) {
					case 0 -> new BenchmarkOperation[]{BenchmarkOperation.PUT};
					case 1 -> new BenchmarkOperation[]{BenchmarkOperation.GET};
					case 2 -> new BenchmarkOperation[]{BenchmarkOperation.REMOVE};
					default -> new BenchmarkOperation[]{BenchmarkOperation.PUT, BenchmarkOperation.GET, BenchmarkOperation.REMOVE};
				};
				runBackgroundTask("Benchmark por operación", output, () -> {
					try {
						benchmarkRunner.runOperations(BenchmarkConfig.quickConfig(), new co.unal.deportesunal.benchmark.factories.IndexFactory[]{
							new co.unal.deportesunal.benchmark.factories.ListIndexFactory(),
							new co.unal.deportesunal.benchmark.factories.BstIndexFactory(),
							new co.unal.deportesunal.benchmark.factories.AvlIndexFactory(),
							new co.unal.deportesunal.benchmark.factories.HashIndexFactory()
						}, selectedOps, co.unal.deportesunal.persistence.FileConstant.indexBenchmarkResult("gui_quick"), false);
						appendOutput(output, "Benchmark por operación completado.\n");
					} catch (Exception ex) {
						throw new RuntimeException(ex);
					}
				});
			}
		}
	}

	public MainWindow(AppController controller, BenchmarkRunner benchmarkRunner, GraphBenchmarkRunner graphBenchmarkRunner) {
		super("Deportes UNAL - GUI");
		this.controller = controller;
		this.benchmarkRunner = benchmarkRunner;
		this.graphBenchmarkRunner = graphBenchmarkRunner;

		initUi();
	}

	private void initUi() {
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setSize(800, 600);
		setLocationRelativeTo(null);

		JPanel panel = new JPanel(new BorderLayout(8,8));
		buttonPanel = new JPanel(new GridLayout(0,2,6,6));
		statusLabel = new JLabel("Listo.");
		progressBar = new JProgressBar();
		progressBar.setStringPainted(true);

		JTextArea output = new JTextArea();
		output.setEditable(false);
		JScrollPane scroll = new JScrollPane(output);
		this.outputArea = output;
		this.centerScroll = scroll;
		this.mainPanel = panel;

		JPanel statusPanel = new JPanel(new BorderLayout(6, 6));
		statusPanel.add(statusLabel, BorderLayout.WEST);
		statusPanel.add(progressBar, BorderLayout.CENTER);

		// Main menu buttons (7) that open submenus mirroring the terminal
		// initialize main menu using the button panel swapping mechanism
		showMainMenu(output);

		// Auto-cargar datos al iniciar (como la consola)
		try {
			controller.load();
			output.append("Datos cargados desde archivo. Total estudiantes: " + controller.totalStudents() + "\n");
		} catch (Exception ex) {
			output.append("No se pudieron cargar datos al iniciar: " + ex.getMessage() + "\n");
		}

		panel.add(buttonPanel, BorderLayout.NORTH);
		panel.add(centerScroll, BorderLayout.CENTER);
		panel.add(statusPanel, BorderLayout.SOUTH);

		// Menu bar with submenu for benchmarks and other sections (mirrors console)
		JMenuBar menuBar = new JMenuBar();
		JMenu menuAcciones = new JMenu("Acciones");
		JMenuItem benchmarksItem = new JMenuItem("Benchmarks...");
		benchmarksItem.addActionListener(e -> showBenchmarkMenu(output));
		menuAcciones.add(benchmarksItem);

		JMenuItem estudiantesItem = new JMenuItem("Estudiantes...");
		estudiantesItem.addActionListener(e -> showStudentMenu(output));
		menuAcciones.add(estudiantesItem);

		JMenuItem comunidadesItem = new JMenuItem("Comunidades...");
		comunidadesItem.addActionListener(e -> showCommunityMenu(output));
		menuAcciones.add(comunidadesItem);

		JMenuItem conexionesItem = new JMenuItem("Conexiones...");
		conexionesItem.addActionListener(e -> showConnectionMenu(output));
		menuAcciones.add(conexionesItem);

		JMenuItem estadisticasItem = new JMenuItem("Estadísticas...");
		estadisticasItem.addActionListener(e -> showStatsMenu(output));
		menuAcciones.add(estadisticasItem);

		menuBar.add(menuAcciones);
		setJMenuBar(menuBar);

		add(panel);
		setBusy(false, "Listo.");
	}

	private void setCenter(Component comp) {
		SwingUtilities.invokeLater(() -> {
			mainPanel.remove(centerScroll);
			if (comp instanceof JScrollPane jsp) {
				centerScroll = jsp;
			} else {
				centerScroll = new JScrollPane(comp);
			}
			mainPanel.add(centerScroll, BorderLayout.CENTER);
			mainPanel.revalidate();
			mainPanel.repaint();
		});
	}

	private void showStudentsTable() {
		LinkedList<Student> students = controller.listStudents();
		String[] cols = new String[]{"ID", "Nombre", "#Prácticas", "#Intereses"};
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
		setCenter(new JScrollPane(table));
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
				JButton b = addActionButton(lbl, e -> {
					try {
						act.run();
					} catch (Exception ex) {
						showError(ex.getMessage());
					}
				});
				buttonPanel.add(b);
			}
			buttonPanel.revalidate();
			buttonPanel.repaint();
		});
	}

	private void showMainMenu(JTextArea output) {
		String[] labels = new String[]{
			"1) Modo interactivo (CRUD)",
			"2) Análisis de comunidades",
			"3) Análisis de conexiones",
			"4) Análisis de estadísticas",
			"5) Ejecutar benchmarks",
			"6) Recargar desde archivo",
			"7) Guardar a archivo",
			"Salir"
		};
		Runnable[] actions = new Runnable[]{
			() -> showCrudMenu(output),
			() -> showCommunityButtons(output),
			() -> showConnectionButtons(output),
			() -> showStatsButtons(output),
			() -> showBenchmarkButtons(output),
			() -> { try { controller.load(); appendOutput(output, "Datos recargados. Total estudiantes: " + controller.totalStudents() + "\n"); } catch (Exception ex) { showError(ex.getMessage()); } },
			() -> { try { controller.save(); appendOutput(output, "Datos guardados.\n"); } catch (Exception ex) { showError(ex.getMessage()); } },
			() -> System.exit(0)
		};
		setButtons(labels, actions);
	}

	private void showCrudMenu(JTextArea output) {
		String[] labels = new String[]{"1) Registrar estudiante", "2) Consultar por ID", "3) Actualizar deportes", "4) Eliminar estudiante", "5) Listar estudiantes", "0) Volver", "Terminar operación", "Salir"};
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
		String[] labels = new String[]{"1) Listar todas las comunidades", "2) Ver comunidad de un estudiante", "0) Volver", "Terminar operación", "Salir"};
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
		String[] labels = new String[]{"1) Conexión a deporte", "2) Practicantes en comunidad", "3) Deportes en comunidad", "0) Volver", "Terminar operación", "Salir"};
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
		String[] labels = new String[]{"1) Ranking deportes", "2) Deporte más/menos practicado", "3) Contar practicantes", "4) Estadísticas generales", "0) Volver", "Terminar operación", "Salir"};
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
		String[] labels = new String[]{"1) Ejecutar todos (rápido)", "2) Ejecutar todos (corto)", "3) Por estructura", "4) Por operación", "0) Volver", "Terminar operación", "Salir"};
		Runnable[] actions = new Runnable[]{
			() -> runBackgroundTask("Benchmarks (rápido)", output, () -> { try { benchmarkRunner.runAll(BenchmarkConfig.quickConfig(), new co.unal.deportesunal.benchmark.factories.IndexFactory[]{ new co.unal.deportesunal.benchmark.factories.ListIndexFactory(), new co.unal.deportesunal.benchmark.factories.BstIndexFactory(), new co.unal.deportesunal.benchmark.factories.AvlIndexFactory(), new co.unal.deportesunal.benchmark.factories.HashIndexFactory() }); appendOutput(output, "Benchmarks (rápido) completados.\n"); } catch (Exception ex) { throw new RuntimeException(ex); } }),
			() -> runBackgroundTask("Benchmarks (corto)", output, () -> { try { benchmarkRunner.runAll(smallConfig(), new co.unal.deportesunal.benchmark.factories.IndexFactory[]{ new co.unal.deportesunal.benchmark.factories.ListIndexFactory(), new co.unal.deportesunal.benchmark.factories.BstIndexFactory(), new co.unal.deportesunal.benchmark.factories.AvlIndexFactory(), new co.unal.deportesunal.benchmark.factories.HashIndexFactory() }); appendOutput(output, "Benchmarks (corto) completados.\n"); } catch (Exception ex) { throw new RuntimeException(ex); } }),
			() -> { String[] structs = new String[]{"LIST", "BST", "AVL", "HASH", "Cancelar"}; int s = JOptionPane.showOptionDialog(this, "Selecciona estructura", "Estructuras", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, structs, structs[0]); if (s < 0 || s == 4) return; co.unal.deportesunal.benchmark.factories.IndexFactory factory = switch (s) { case 0 -> new co.unal.deportesunal.benchmark.factories.ListIndexFactory(); case 1 -> new co.unal.deportesunal.benchmark.factories.BstIndexFactory(); case 2 -> new co.unal.deportesunal.benchmark.factories.AvlIndexFactory(); case 3 -> new co.unal.deportesunal.benchmark.factories.HashIndexFactory(); default -> null; }; if (factory == null) return; runBackgroundTask("Benchmark por estructura", output, () -> { try { benchmarkRunner.runOperations(BenchmarkConfig.quickConfig(), new co.unal.deportesunal.benchmark.factories.IndexFactory[]{factory}, new BenchmarkOperation[]{BenchmarkOperation.PUT, BenchmarkOperation.GET, BenchmarkOperation.REMOVE}, co.unal.deportesunal.persistence.FileConstant.indexBenchmarkResult("gui_quick"), false); appendOutput(output, "Benchmark por estructura completado.\n"); } catch (Exception ex) { throw new RuntimeException(ex); } }); },
			() -> { String[] ops = new String[]{"PUT", "GET", "REMOVE", "Todos", "Cancelar"}; int o = JOptionPane.showOptionDialog(this, "Selecciona operación", "Operaciones", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, ops, ops[0]); if (o < 0 || o == 4) return; BenchmarkOperation[] selectedOps = switch (o) { case 0 -> new BenchmarkOperation[]{BenchmarkOperation.PUT}; case 1 -> new BenchmarkOperation[]{BenchmarkOperation.GET}; case 2 -> new BenchmarkOperation[]{BenchmarkOperation.REMOVE}; default -> new BenchmarkOperation[]{BenchmarkOperation.PUT, BenchmarkOperation.GET, BenchmarkOperation.REMOVE}; }; runBackgroundTask("Benchmark por operación", output, () -> { try { benchmarkRunner.runOperations(BenchmarkConfig.quickConfig(), new co.unal.deportesunal.benchmark.factories.IndexFactory[]{ new co.unal.deportesunal.benchmark.factories.ListIndexFactory(), new co.unal.deportesunal.benchmark.factories.BstIndexFactory(), new co.unal.deportesunal.benchmark.factories.AvlIndexFactory(), new co.unal.deportesunal.benchmark.factories.HashIndexFactory() }, selectedOps, co.unal.deportesunal.persistence.FileConstant.indexBenchmarkResult("gui_quick"), false); appendOutput(output, "Benchmark por operación completado.\n"); } catch (Exception ex) { throw new RuntimeException(ex); } }); },
			() -> showMainMenu(output),
			() -> cancelCurrentTask(output),
			() -> System.exit(0)
		};
		setButtons(labels, actions);
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

	private JButton addActionButton(String label, java.awt.event.ActionListener listener) {
		JButton button = new JButton(label);
		button.addActionListener(listener);
		actionButtons.add(button);
		return button;
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
				boolean keepEnabled = "Cancelar operación".equals(text) || "Salir".equals(text);
				button.setEnabled(busy ? keepEnabled : true);
			}
		});
	}

	private void appendOutput(JTextArea output, String message) {
		SwingUtilities.invokeLater(() -> output.append(message));
	}

	private void registerStudent(JTextArea output) {
		Integer id = readInt("ID del estudiante: ");
		if (id == null) {
			return;
		}

		String name = promptText("Nombre del estudiante:");
		if (name == null || name.trim().isEmpty()) {
			showInfo("Nombre vacío o cancelado.");
			return;
		}

		try {
			controller.registerStudent(id, name.trim());
			output.append("Estudiante registrado: " + id + " - " + name.trim() + "\n");
		} catch (Exception ex) {
			showError(ex.getMessage());
		}
	}

	private void findStudent(JTextArea output) {
		Integer id = readInt("ID a consultar:");
		if (id == null) {
			return;
		}
		try {
			Student student = controller.findStudent(id);
			output.append(formatStudent(student) + "\n");
		} catch (Exception ex) {
			showError(ex.getMessage());
		}
	}

	private void deleteStudent(JTextArea output) {
		Integer id = readInt("ID a eliminar:");
		if (id == null) {
			return;
		}
		boolean removed = controller.deleteStudent(id);
		output.append(removed ? "Eliminado: " + id + "\n" : "No existía: " + id + "\n");
	}

	private void updateSports(JTextArea output) {
		Integer id = readInt("ID del estudiante:");
		if (id == null) {
			return;
		}

		try {
			controller.findStudent(id);
		} catch (Exception ex) {
			showError(ex.getMessage());
			return;
		}

		String[] options = new String[]{"Agregar práctica", "Remover práctica", "Agregar interés", "Remover interés", "Cancelar"};
		int option = JOptionPane.showOptionDialog(this, "Elige acción", "Actualizar deportes", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
		if (option < 0 || option == 4) {
			return;
		}

		SportEnum sport = readSport();
		if (sport == null) {
			return;
		}

		try {
			boolean result = switch (option) {
				case 0 -> controller.addPracticeSport(id, sport);
				case 1 -> controller.removePracticeSport(id, sport);
				case 2 -> controller.addInterestSport(id, sport);
				case 3 -> controller.removeInterestSport(id, sport);
				default -> false;
			};
			output.append((result ? "Operación exitosa" : "Sin cambios") + " para " + sport.displayName() + "\n");
		} catch (Exception ex) {
			showError(ex.getMessage());
		}
	}

	private void listAllCommunities(JTextArea output) {
		try {
			LinkedList<LinkedList<Student>> communities = controller.getCommunities();
			output.append("Total comunidades: " + controller.getTotalCommunities() + "\n");
			final int[] counter = {1};
			communities.traverse(new ListVisitor<LinkedList<Student>>() {
				@Override
				public void visit(LinkedList<Student> community) {
					if (community == null) return;
					output.append("\nComunidad " + counter[0] + " (" + community.size() + ")\n");
					community.traverse(new ListVisitor<Student>() {
						@Override
						public void visit(Student student) {
							if (student != null) {
								output.append("  - " + formatStudent(student) + "\n");
							}
						}
					});
					counter[0]++;
				}
			});
		} catch (Exception ex) {
			showError(ex.getMessage());
		}
	}

	private void viewStudentCommunity(JTextArea output) {
		Integer id = readInt("ID del estudiante:");
		if (id == null) {
			return;
		}

		try {
			Student student = controller.findStudent(id);
			LinkedList<Student> community = controller.getStudentCommunity(id);
			output.append("\nComunidad de " + student.getName() + " (" + community.size() + ")\n");
			community.traverse(new ListVisitor<Student>() {
				@Override
				public void visit(Student current) {
					if (current != null) {
						output.append("  - " + formatStudent(current) + (current.getId() == id ? " <- tú" : "") + "\n");
					}
				}
			});
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
					? "Sí hay conexión hacia " + sport.displayName() + "\n"
					: "No hay conexión hacia " + sport.displayName() + "\n");
			if (hasConnection) {
				LinkedList<Student> practitioners = controller.getPractitionersInCommunity(id, sport);
				output.append("Practicantes: " + practitioners.size() + "\n");
				practitioners.traverse(new ListVisitor<Student>() {
					@Override
					public void visit(Student student) {
						if (student != null) {
							output.append("  - " + formatStudent(student) + "\n");
						}
					}
				});
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
			int count = controller.countPractitionersInCommunity(id, sport);
			LinkedList<Student> practitioners = controller.getPractitionersInCommunity(id, sport);
			output.append("Practicantes de " + sport.displayName() + " en tu comunidad: " + count + "\n");
			practitioners.traverse(new ListVisitor<Student>() {
				@Override
				public void visit(Student student) {
					if (student != null) {
						output.append("  - " + formatStudent(student) + "\n");
					}
				}
			});
		} catch (Exception ex) {
			showError(ex.getMessage());
		}
	}

	private void viewAllSportsInCommunity(JTextArea output) {
		Integer id = readInt("Tu ID:");
		if (id == null) return;

		try {
			LinkedList<SportEnum> sports = controller.getSportsInCommunity(id);
			output.append("Deportes en tu comunidad: " + sports.size() + "\n");
			sports.traverse(new ListVisitor<SportEnum>() {
				@Override
				public void visit(SportEnum sport) {
					if (sport != null) {
						int practitioners = controller.countPractitionersInCommunity(id, sport);
						output.append("  - " + sport.displayName() + " (" + practitioners + ")\n");
					}
				}
			});
		} catch (Exception ex) {
			showError(ex.getMessage());
		}
	}

	private void showRanking(JTextArea output) {
		try {
			LinkedList<SportCount> ranking = controller.getRankingSports();
			output.append("\n=== Ranking deportes ===\n");
			ranking.traverse(new ListVisitor<SportCount>() {
				@Override
				public void visit(SportCount sc) {
					if (sc != null) {
						output.append(sc.getSport().displayName() + ": " + sc.getCount() + "\n");
					}
				}
			});
		} catch (Exception ex) {
			showError(ex.getMessage());
		}
	}

	private void showTopAndLeast(JTextArea output) {
		try {
			SportCount top = controller.getMostPracticedSport();
			SportCount least = controller.getLeastPracticedSport();
			output.append("\n=== Estadística rápida ===\n");
			output.append(top == null ? "No hay deporte más practicado\n" : "Más practicado: " + top.getSport().displayName() + " (" + top.getCount() + ")\n");
			output.append(least == null ? "No hay deporte menos practicado\n" : "Menos practicado: " + least.getSport().displayName() + " (" + least.getCount() + ")\n");
		} catch (Exception ex) {
			showError(ex.getMessage());
		}
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
			output.append("\n" + controller.getGeneralStats() + "\n");
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
		appendOutput("Cancelación solicitada.\n");
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
		if (raw == null) {
			return null;
		}
		try {
			return Integer.valueOf(raw.trim());
		} catch (NumberFormatException ex) {
			showError("Número inválido: " + raw);
			return null;
		}
	}

	private String promptText(String prompt) {
		return JOptionPane.showInputDialog(this, prompt);
	}

	private SportEnum readSport() {
		SportEnum[] values = SportEnum.values();
		String[] names = new String[values.length];
		for (int index = 0; index < values.length; index++) {
			names[index] = values[index].displayName() + " (" + values[index].name() + ")";
		}
		int selected = JOptionPane.showOptionDialog(this, "Selecciona un deporte", "Deportes", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, names, names[0]);
		if (selected < 0 || selected >= values.length) {
			return null;
		}
		return values[selected];
	}

	private String formatStudent(Student student) {
		if (student == null) {
			return "<null>";
		}
		return student.getId() + " | " + student.getName();
	}

	private void showError(String message) {
		JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
	}

	private void showInfo(String message) {
		JOptionPane.showMessageDialog(this, message, "Información", JOptionPane.INFORMATION_MESSAGE);
	}
}
