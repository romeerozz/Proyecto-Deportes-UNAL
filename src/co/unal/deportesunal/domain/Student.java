package co.unal.deportesunal.domain;

import co.unal.deportesunal.structure.listadt.LinkedList;

public class Student {
    private final int ID;
    private  String name;
    private LinkedList<SportEnum> practice;
    private LinkedList<SportEnum> interest;

    public Student(int id, String name){
        this.ID = id;
        this.name = name;
        this.practice = new LinkedList<>();
        this.interest = new LinkedList<>();
    }

    public int getId() {
        return id;
    }

    public int getID() {
        return ID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LinkedList<SportsEnum> getPractice() {
        return practice;
    }

    public void setPractice(LinkedList<SportsEnum> practice) {
        this.practice = practice;
    }

    public LinkedList<SportsEnum> getInterest() {
        return interest;
    }

    public void setInterest(LinkedList<SportsEnum> interest) {
        this.interest = interest;
    }

    public boolean removePractice(SportEnum s) {
        if (s == null) return false;
        // TODO: cuando LinkedList tenga remove(value) o wrapper find+erase:
        return false;
    }
}

    public boolean removeInterest(SportEnum s) {
        if (s == null) return false;
        // TODO: cuando LinkedList tenga remove(value) o wrapper find+erase:
        // return interest.remove(s);
        return false;
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