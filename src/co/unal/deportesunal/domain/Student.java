package co.unal.deportesunal.domain;

import co.unal.deportesunal.structure.listadt.LinkedList;

public class Student {
    private final int id;
    private String name;

    private final LinkedList<SportEnum> practice;
    private final LinkedList<SportEnum> interest;

    public Student(int id, String name){
        this.id = id;
        this.name = name;
        this.practice = new LinkedList<>();
        this.interest = new LinkedList<>();
    }

    public int getId() { return id; }

    public int getID() { return id; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    /**
     * NOTA: Por ahora se exponen las listas para no bloquear el repositorio TXT.
     * TODO: cuando exista iteración segura en LinkedList (get(i)/forEach/Iterator),
     *       reemplazar por métodos de acceso controlados o snapshots.
     */
    public LinkedList<SportEnum> getPractice() { return practice; }

    public LinkedList<SportEnum> getInterest() { return interest; }

    public boolean addPractice(SportEnum s){
        if (s == null) return false;
        if (practice.contains(s)) return false;
        practice.pushBack(s);
        return true;
    }

    public boolean addInterest(SportEnum s){
        if (s == null) return false;
        if (interest.contains(s)) return false;
        interest.pushBack(s);
        return true;
    }

    public boolean removePractice(SportEnum s) {
        if (s == null) return false;
        return practice.remove(s);
    }

    public boolean removeInterest(SportEnum s) {
        if (s == null) return false;
        return interest.remove(s);
    }

    public boolean practices(SportEnum s) {
        if (s == null) return false;
        return practice.contains(s);
    }

    public boolean isInterestedIn(SportEnum s) {
        if (s == null) return false;
        return interest.contains(s);
    }

    @Override
    public String toString() {
        return "Student{id=" + id + ", name='" + name + "'}";
    }
}