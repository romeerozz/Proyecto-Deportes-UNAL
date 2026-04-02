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

    public LinkedList<SportsEnum> getInterest() {
        return interest;
    }

    public boolean addPractice(SportsEnum s){
        if(practice.contains(s)) return false;
        practice.pushBack(s);
        return true;
    }

    public boolean removePractice(SportsEnum s){
        //return practice.erase(s);
        return true;
    }

    public boolean addInterest(SportsEnum s){
        if(interest.contains(s)) return false;
        interest.pushBack(s);
        return true;
    }

    public boolean removeInterest(SportsEnum s){
        //return interest.erase(s)
        return true;
    }
}
