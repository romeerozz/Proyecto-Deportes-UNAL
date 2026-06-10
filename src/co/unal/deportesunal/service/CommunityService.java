package co.unal.deportesunal.service;

import co.unal.deportesunal.domain.Student;
import co.unal.deportesunal.structure.listadt.LinkedList;
import co.unal.deportesunal.structure.listadt.ListVisitor;
import co.unal.deportesunal.structure.graphadt.AdjacencyListGraph;
import co.unal.deportesunal.structure.disjointset.UnionFind;
import co.unal.deportesunal.structure.queue.ArrayQueue;
import co.unal.deportesunal.structure.queue.Queue;

/**
 * CommunityService agrupa estudiantes que comparten al menos un deporte en común.
 * Utiliza BFS para identificar componentes conectadas en el grafo implícito
 * donde dos estudiantes están conectados si comparten al menos un deporte practicado.
 */
public class CommunityService {

    private final StudentService studentService;

    public CommunityService(StudentService studentService) {
        this.studentService = studentService;
    }

    
    /**
     * Encuentra y retorna todas las comunidades de estudiantes.
     * Dos estudiantes pertenecen a la misma comunidad si comparten
     * al menos un deporte practicado. Usa Union-Find para agruparlos.
     * @return LinkedList<LinkedList<Student>> con las comunidades encontradas
     */
    public LinkedList<LinkedList<Student>> findCommunities() {

    LinkedList<LinkedList<Student>> communities = new LinkedList<>();

    LinkedList<Student> students =
            studentService.listStudentsOrderedById();

    UnionFind uf = new UnionFind();

    students.traverse(new ListVisitor<Student>() {
        @Override
        public void visit(Student s) {
            if (s != null) {
                uf.makeSet(s.getId());
            }
        }
    });

    students.traverse(new ListVisitor<Student>() {
        @Override
        public void visit(Student s1) {

            students.traverse(new ListVisitor<Student>() {
                @Override
                public void visit(Student s2) {

                    if (s1 != null
                            && s2 != null
                            && s1.getId() < s2.getId()
                            && s1.sharesPracticeWith(s2)) {

                        uf.union(
                                s1.getId(),
                                s2.getId()
                        );
                    }
                }
            });
        }
    });

    LinkedList<Integer> processedRoots = new LinkedList<>();

    students.traverse(new ListVisitor<Student>() {
        @Override
        public void visit(Student student) {

            int root = uf.find(student.getId());

            if (!processedRoots.contains(root)) {

                processedRoots.pushBack(root);

                LinkedList<Student> community =
                        new LinkedList<>();

                students.traverse(new ListVisitor<Student>() {
                    @Override
                    public void visit(Student other) {

                        if (uf.find(other.getId()) == root) {
                            community.pushBack(other);
                        }
                    }
                });

                communities.pushBack(community);
            }
        }
    });

    return communities;
}

    /**
     * Retorna la comunidad a la que pertenece un estudiante específico.
     * @param studentId ID del estudiante
     * @return LinkedList<Student> con la comunidad, o lista vacía si no existe.
     */
    public LinkedList<Student> findCommunityById(int studentId) {
        try {
            AdjacencyListGraph<Integer> graph = buildGraphFromStudents();
            LinkedList<Integer> compIds = graph.bfsComponent(studentId);
            LinkedList<Student> community = new LinkedList<>();
            compIds.traverse(new ListVisitor<Integer>() {
                @Override
                public void visit(Integer id) {
                    try { community.pushBack(studentService.findStudentById(id)); } catch (Exception e) {}
                }
            });
            return community;
        } catch (Exception e) {
            return new LinkedList<>();
        }
    }

    /**
     * BFS para obtener la comunidad completa a partir de un estudiante.
     * @param startStudent estudiante inicial
     * @param visited lista de IDs visitados (se modifica)
     * @return LinkedList<Student> con todos los estudiantes en la comunidad
     */
    private LinkedList<Student> bfsGetCommunity(Student startStudent, LinkedList<Integer> visited) {
        LinkedList<Student> community = new LinkedList<>();
        Queue<Student> queue = new ArrayQueue<>();

        queue.enqueue(startStudent);
        visited.pushBack(startStudent.getId());
        community.pushBack(startStudent);

        while (!queue.isEmpty()) {
            Student current = queue.dequeue();

            // Recorrer todos los estudiantes
            LinkedList<Student> allStudents = studentService.listStudentsOrderedById();
            allStudents.traverse(new ListVisitor<Student>() {
                @Override
                public void visit(Student neighbor) {
                    if (neighbor != null
                        && !isVisited(visited, neighbor.getId())
                        && current.sharesPracticeWith(neighbor)) {

                        visited.pushBack(neighbor.getId());
                        community.pushBack(neighbor);
                        queue.enqueue(neighbor);
                    }
                }
            });
        }

        return community;
    }

    /**
     * Verifica si un estudiante ya fue visitado.
     * @param visited LinkedList de IDs visitados
     * @param studentId ID a verificar
     * @return true si ya fue visitado
     */
    private boolean isVisited(LinkedList<Integer> visited, int studentId) {
        final boolean[] found = {false};
        visited.traverse(new ListVisitor<Integer>() {
            @Override
            public void visit(Integer id) {
                if (id != null && id == studentId) {
                    found[0] = true;
                }
            }
        });
        return found[0];
    }

    /** Construye un grafo no dirigido donde los vértices son IDs de estudiantes y
     * hay una arista entre dos IDs si comparten al menos un deporte practicado. */
    private AdjacencyListGraph<Integer> buildGraphFromStudents() {
        AdjacencyListGraph<Integer> graph = new AdjacencyListGraph<>();
        LinkedList<Student> students = studentService.listStudentsOrderedById();

        // Añadir vértices
        students.traverse(new ListVisitor<Student>() {
            @Override
            public void visit(Student s) {
                if (s != null) graph.addVertex(s.getId());
            }
        });

        // Añadir aristas entre pares que comparten práctica
        students.traverse(new ListVisitor<Student>() {
            @Override
            public void visit(Student s1) {
                students.traverse(new ListVisitor<Student>() {
                    @Override
                    public void visit(Student s2) {
                        if (s1 != null && s2 != null && s1.getId() < s2.getId() && s1.sharesPracticeWith(s2)) {
                            graph.addEdge(s1.getId(), s2.getId());
                        }
                    }
                });
            }
        });

        return graph;
    }

    /**
     * Obtiene el tamaño de una comunidad.
     * @param studentId ID del estudiante
     * @return tamaño de la comunidad
     */
    public int getCommunitySize(int studentId) {
        return findCommunityById(studentId).size();
    }

    /**
     * Obtiene el número total de comunidades.
     * @return cantidad de comunidades
     */
    public int getTotalCommunities() {
        return findCommunities().size();
    }
}
