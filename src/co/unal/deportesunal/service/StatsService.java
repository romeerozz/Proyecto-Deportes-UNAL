package co.unal.deportesunal.service;

import co.unal.deportesunal.domain.SportCount;
import co.unal.deportesunal.domain.SportEnum;
import co.unal.deportesunal.domain.Student;
import co.unal.deportesunal.structure.listadt.LinkedList;
import co.unal.deportesunal.structure.listadt.ListVisitor;

	/**
	 * StatsService proporciona estadísticas sobre los deportes practicados.
	 * Cuenta cuántos estudiantes practican cada deporte y genera rankings.
	 */
	public class StatsService {

		private final StudentService studentService;

		public StatsService(StudentService studentService) {
			this.studentService = studentService;
		}

		/**
		 * Cuenta cuántos estudiantes practican cada deporte.
		 * @return LinkedList<SportCount> con conteos de cada deporte practicado
		 */
		public LinkedList<SportCount> countSportsPracticed() {
			LinkedList<SportCount> counts = new LinkedList<>();

			// Inicializar contadores para todos los deportes
			for (SportEnum sport : SportEnum.values()) {
				counts.pushBack(new SportCount(sport));
			}

			// Recorrer todos los estudiantes y contar
			LinkedList<Student> students = studentService.listStudentsOrderedById();
			students.traverse(new ListVisitor<Student>() {
				@Override
				public void visit(Student student) {
					if (student != null) {
						student.getPractice().traverse(new ListVisitor<SportEnum>() {
							@Override
							public void visit(SportEnum sport) {
								if (sport != null) {
									// Buscar y incrementar el contador del deporte
									counts.traverse(new ListVisitor<SportCount>() {
										@Override
										public void visit(SportCount sc) {
											if (sc != null && sc.getSport() == sport) {
												sc.increment();
											}
										}
									});
								}
							}
						});
					}
				}
			});

			return counts;
		}

		/**
		 * Obtiene ranking de deportes por cantidad de practicantes (mayor a menor).
		 * Solo incluye deportes que tiene al menos 1 practicante.
		 * @return LinkedList<SportCount> ordenado descendente
		 */
		public LinkedList<SportCount> getRankingSports() {
			LinkedList<SportCount> allCounts = countSportsPracticed();
			LinkedList<SportCount> filtered = new LinkedList<>();

			// Recorrer todos los deportes y filtrar los que tienen practicantes
			allCounts.traverse(new ListVisitor<SportCount>() {
				@Override
				public void visit(SportCount sc) {
					if (sc != null && sc.getCount() > 0) {
						filtered.pushBack(sc);
					}
				}
			});

			// Ordenar manualmente usando burbuja (simple pero correcto)
			return bubbleSortDescending(filtered);
		}

		/**
		 * Ordena SportCount en orden descendente por cantidad de practicantes.
		 */
		private LinkedList<SportCount> bubbleSortDescending(LinkedList<SportCount> list) {
			if (list.size() <= 1) return list;

			LinkedList<SportCount> result = new LinkedList<>();

			// Copiar elementos a array para ordenar
			final SportCount[] arr = new SportCount[list.size()];
			final int[] idx = {0};

			list.traverse(new ListVisitor<SportCount>() {
				@Override
				public void visit(SportCount sc) {
					if (sc != null) {
						arr[idx[0]++] = sc;
					}
				}
			});

			// Burbuja
			for (int i = 0; i < arr.length - 1; i++) {
				for (int j = 0; j < arr.length - 1 - i; j++) {
					if (arr[j].getCount() < arr[j + 1].getCount()) {
						SportCount temp = arr[j];
						arr[j] = arr[j + 1];
						arr[j + 1] = temp;
					}
				}
			}

			// Pasar a LinkedList
			for (SportCount sc : arr) {
				if (sc != null) {
					result.pushBack(sc);
				}
			}

			return result;
		}

		/**
		 * Obtiene el deporte más practicado.
		 * @return SportCount del deporte más popular, o null si no hay practicantes
		 */
		public SportCount getMostPracticedSport() {
			LinkedList<SportCount> ranking = getRankingSports();
			if (ranking.size() == 0) return null;
            
			final SportCount[] result = {null};
			ranking.traverse(new ListVisitor<SportCount>() {
				@Override
				public void visit(SportCount sc) {
					if (sc != null && result[0] == null) {
						result[0] = sc;
					}
				}
			});
			return result[0];
		}

		/**
		 * Obtiene el deporte menos practicado (de los que tiene practicantes).
		 * @return SportCount del deporte menos popular
		 */
		public SportCount getLeastPracticedSport() {
			LinkedList<SportCount> ranking = getRankingSports();
			if (ranking.size() == 0) return null;

			final SportCount[] result = {null};
			ranking.traverse(new ListVisitor<SportCount>() {
				@Override
				public void visit(SportCount sc) {
					if (sc != null) {
						result[0] = sc; // última iteración será la menor
					}
				}
			});
			return result[0];
		}

		/**
		 * Obtiene el total de practicantes únicos de un deporte.
		 * @param sport deporte
		 * @return cantidad de estudiantes que practican ese deporte
		 */
		public int getPractitionersCount(SportEnum sport) {
			if (sport == null) return 0;

			final int[] count = {0};
			LinkedList<Student> students = studentService.listStudentsOrderedById();

			students.traverse(new ListVisitor<Student>() {
				@Override
				public void visit(Student student) {
					if (student != null && student.practices(sport)) {
						count[0]++;
					}
				}
			});

			return count[0];
		}

		/**
		 * Obtiene estadísticas generales del sistema.
		 * @return String con resumen de estadísticas
		 */
		public String getGeneralStats() {
			int totalStudents = studentService.totalStudents();
			LinkedList<SportCount> ranking = getRankingSports();
			int totalSportsWithPractitioners = ranking.size();

			StringBuilder sb = new StringBuilder();
			sb.append("=== Estadísticas Generales ===\n");
			sb.append("Total de estudiantes: ").append(totalStudents).append("\n");
			sb.append("Deportes practicados: ").append(totalSportsWithPractitioners).append("\n");

			if (totalSportsWithPractitioners > 0) {
				SportCount top = getMostPracticedSport();
				if (top != null) {
					sb.append("Deporte más practicado: ").append(top.getSport().displayName())
					  .append(" (").append(top.getCount()).append(" estudiantes)\n");
				}
			}

			return sb.toString();
		}
	}
