package co.unal.deportesunal.domain;

import co.unal.deportesunal.structure.listadt.LinkedList;

public class Student {
    private final int ID;
    private  String name;
    private LinkedList<SportsEnum> practice;
    private LinkedList<SportsEnum> interest;

    public Student(int id, String name){
        this.ID = id;
        this.name = name;
        this.practice = new LinkedList<>();
        this.interest = new LinkedList<>();
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

    public void addPractice(SportsEnum s){
        if (s == null) {
            return;
        }

        if (!practice.contains(s)) {
            practice.pushBack(s);
        }
    }

    public void addInterest(SportsEnum s) {
        if (s == null) {
            return;
        }

        if (!interest.contains(s)) {
            interest.pushBack(s);
        }
    }
}
