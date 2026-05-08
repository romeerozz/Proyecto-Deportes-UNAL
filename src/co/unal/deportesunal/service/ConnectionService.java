package co.unal.deportesunal.service;

import co.unal.deportesunal.domain.SportEnum;
import co.unal.deportesunal.domain.Student;
import co.unal.deportesunal.structure.listadt.LinkedList;
import co.unal.deportesunal.structure.listadt.ListVisitor;
import co.unal.deportesunal.structure.queue.ArrayQueue;
import co.unal.deportesunal.structure.queue.Queue;

/**
 * ConnectionService determina si existe una conexión directa o indirecta
 * entre un estudiante y otros que practican deportes de su interés.
 * Utiliza BFS para buscar en la comunidad deportiva.
 */
public class ConnectionService {

    private final StudentService studentService;
    private final CommunityService communityService;

    public ConnectionService(StudentService studentService, CommunityService communityService) {
        this.studentService = studentService;
        this.communityService = communityService;
    }

    /**
     * Verifica si existe una conexión directa o indirecta a alguien que practica un deporte de interés.
     * @param studentId ID del estudiante
     * @param sport deporte de interés
     * @return true si existe conexión, false en caso contrario
     */
    public boolean hasConnectionToSport(int studentId, SportEnum sport) {
        if (sport == null) return false;

        try {
            Student student = studentService.findStudentById(studentId);
            LinkedList<Student> community = communityService.findCommunityById(studentId);

            // Usar BFS para buscar en la comunidad
            final boolean[] found = {false};
            community.traverse(new ListVisitor<Student>() {
                @Override
                public void visit(Student communityMember) {
                    if (communityMember != null && communityMember.practices(sport)) {
                        found[0] = true;
                    }
                }
            });

            return found[0];
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Obtiene una lista de todos los estudiantes en la comunidad que practican un deporte específico.
     * @param studentId ID del estudiante
     * @param sport deporte buscado
     * @return LinkedList<Student> con estudiantes que practican el deporte
     */
    public LinkedList<Student> findPractitionersInCommunity(int studentId, SportEnum sport) {
        LinkedList<Student> practitioners = new LinkedList<>();
        
        if (sport == null) return practitioners;

        try {
            LinkedList<Student> community = communityService.findCommunityById(studentId);
            community.traverse(new ListVisitor<Student>() {
                @Override
                public void visit(Student communityMember) {
                    if (communityMember != null && communityMember.practices(sport)) {
                        practitioners.pushBack(communityMember);
                    }
                }
            });
        } catch (Exception e) {
            // retorna lista vacía
        }

        return practitioners;
    }

    /**
     * Obtiene todos los deportes practicados en la comunidad del estudiante.
     * @param studentId ID del estudiante
     * @return LinkedList<SportEnum> con deportes únicos en la comunidad
     */
    public LinkedList<SportEnum> getSportsInCommunity(int studentId) {
        LinkedList<SportEnum> sports = new LinkedList<>();

        try {
            LinkedList<Student> community = communityService.findCommunityById(studentId);
            community.traverse(new ListVisitor<Student>() {
                @Override
                public void visit(Student communityMember) {
                    if (communityMember != null) {
                        communityMember.getPractice().traverse(new ListVisitor<SportEnum>() {
                            @Override
                            public void visit(SportEnum sport) {
                                if (sport != null && !sports.contains(sport)) {
                                    sports.pushBack(sport);
                                }
                            }
                        });
                    }
                }
            });
        } catch (Exception e) {
            // retorna lista vacía
        }

        return sports;
    }

    /**
     * Obtiene estudiantes en la comunidad que podrían ayudar con un deporte de interés.
     * Retorna aquellos que: tienen el deporte practicado O están conectados a alguien que lo practica.
     * @param studentId ID del estudiante
     * @param sport deporte de interés
     * @return LinkedList<Student> con contactos útiles
     */
    public LinkedList<Student> findUsefulContactsForSport(int studentId, SportEnum sport) {
        LinkedList<Student> contacts = new LinkedList<>();

        if (sport == null) return contacts;

        try {
            Student student = studentService.findStudentById(studentId);
            LinkedList<Student> community = communityService.findCommunityById(studentId);

            // Primero: directamente conectados que practican el deporte
            community.traverse(new ListVisitor<Student>() {
                @Override
                public void visit(Student communityMember) {
                    if (communityMember != null 
                        && communityMember.getId() != studentId
                        && communityMember.practices(sport)
                        && !contacts.contains(communityMember)) {
                        contacts.pushBack(communityMember);
                    }
                }
            });

        } catch (Exception e) {
            // retorna lista vacía
        }

        return contacts;
    }

    /**
     * Cuenta cuántos estudiantes en la comunidad practican un deporte específico.
     * @param studentId ID del estudiante
     * @param sport deporte a contar
     * @return cantidad de practicantes
     */
    public int countPractitionersInCommunity(int studentId, SportEnum sport) {
        if (sport == null) return 0;

        try {
            LinkedList<Student> practitioners = findPractitionersInCommunity(studentId, sport);
            return practitioners.size();
        } catch (Exception e) {
            return 0;
        }
    }
}
