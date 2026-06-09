package co.unal.deportesunal.controller;

import co.unal.deportesunal.domain.SportCount;
import co.unal.deportesunal.domain.SportEnum;
import co.unal.deportesunal.domain.Student;
import co.unal.deportesunal.domain.exception.DataAccessException;
import co.unal.deportesunal.domain.exception.DuplicatedIdException;
import co.unal.deportesunal.domain.exception.NotFoundException;
import co.unal.deportesunal.service.CommunityService;
import co.unal.deportesunal.service.ConnectionService;
import co.unal.deportesunal.service.StatsService;
import co.unal.deportesunal.service.StudentService;
import co.unal.deportesunal.structure.listadt.LinkedList;

import java.io.IOException;

public class AppController {

    private final StudentService studentService;
    private final CommunityService communityService;
    private final ConnectionService connectionService;
    private final StatsService statsService;

    public AppController(StudentService studentService) {
        this.studentService = studentService;
        this.communityService = new CommunityService(studentService);
        this.connectionService = new ConnectionService(studentService, communityService);
        this.statsService = new StatsService(studentService);
    }

    public void load() throws DataAccessException, DuplicatedIdException {
        studentService.loadFromRepository();
    }

    public void save() throws DataAccessException, IOException {
        studentService.saveToRepository();
    }

    public void registerStudent(int id, String name) throws DuplicatedIdException {
        Student s = new Student(id, name);
        studentService.registerStudent(s);
    }

    public Student findStudent(int id) throws NotFoundException {
        return studentService.findStudentById(id);
    }

    public boolean deleteStudent(int id) {
        return studentService.removeStudentById(id);
    }

    public boolean exists(int id) {
        return studentService.exists(id);
    }

    public LinkedList<Student> listStudents() {
        return studentService.listStudentsOrderedById();
    }

    public int totalStudents() {
        return studentService.totalStudents();
    }

    public boolean addPracticeSport(int studentId, SportEnum sport) throws NotFoundException {
        return studentService.addPracticeSport(studentId, sport);
    }

    public boolean removePracticeSport(int studentId, SportEnum sport) throws NotFoundException {
        return studentService.removePracticeSport(studentId, sport);
    }

    public boolean addInterestSport(int studentId, SportEnum sport) throws NotFoundException {
        return studentService.addInterestSport(studentId, sport);
    }

    public boolean removeInterestSport(int studentId, SportEnum sport) throws NotFoundException {
        return studentService.removeInterestSport(studentId, sport);
    }

    public LinkedList<LinkedList<Student>> getCommunities() {
        return communityService.findCommunities();
    }

    public LinkedList<Student> getStudentCommunity(int studentId) {
        return communityService.findCommunityById(studentId);
    }

    public int getCommunitySize(int studentId) {
        return communityService.getCommunitySize(studentId);
    }

    public int getTotalCommunities() {
        return communityService.getTotalCommunities();
    }

    public boolean hasConnectionToSport(int studentId, SportEnum sport) {
        return connectionService.hasConnectionToSport(studentId, sport);
    }

    public LinkedList<Student> getPractitionersInCommunity(int studentId, SportEnum sport) {
        return connectionService.findPractitionersInCommunity(studentId, sport);
    }

    public LinkedList<SportEnum> getSportsInCommunity(int studentId) {
        return connectionService.getSportsInCommunity(studentId);
    }

    public LinkedList<Student> getContactsForSport(int studentId, SportEnum sport) {
        return connectionService.findUsefulContactsForSport(studentId, sport);
    }

    public int countPractitionersInCommunity(int studentId, SportEnum sport) {
        return connectionService.countPractitionersInCommunity(studentId, sport);
    }

    public LinkedList<SportCount> getRankingSports() {
        return statsService.getRankingSports();
    }

    public SportCount getMostPracticedSport() {
        return statsService.getMostPracticedSport();
    }

    public SportCount getLeastPracticedSport() {
        return statsService.getLeastPracticedSport();
    }

    public int getPractitionersCount(SportEnum sport) {
        return statsService.getPractitionersCount(sport);
    }

    public String getGeneralStats() {
        return statsService.getGeneralStats();
    }
}
