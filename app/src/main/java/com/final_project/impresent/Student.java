package com.final_project.impresent;

import java.io.Serializable;

public class Student implements Serializable {
    private String name, id, email, branch, androidId;
    private int sem;

    public Student() {
    }

    public Student(String name, String id, int sem, String email, String androidId) {
        this.name = name;
        this.id = id;
        this.sem = sem;
        this.email = email;
        this.branch = id.substring(5,8);
        this.androidId = androidId;
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

    public int getSem() {
        return sem;
    }

    public void setSem(int sem) {
        this.sem = sem;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch){
        this.branch = branch;
    }

    public String getAndroidId() {
        return androidId;
    }

    public void setAndroidId(String androidId) {
        this.androidId = androidId;
    }
}
