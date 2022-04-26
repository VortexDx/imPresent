package com.final_project.impresent;

public class Teacher {
    private String name,id,subId,pass;
    private int sem;

    public Teacher() {
    }

    public Teacher(String name, String id, String subId, String pass, int sem) {
        this.name = name;
        this.id = id;
        this.subId = subId;
        this.pass = pass;
        this.sem = sem;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSubId() {
        return subId;
    }

    public void setSubId(String subId) {
        this.subId = subId;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public int getSem() {
        return sem;
    }

    public void setSem(int sem) {
        this.sem = sem;
    }
}
