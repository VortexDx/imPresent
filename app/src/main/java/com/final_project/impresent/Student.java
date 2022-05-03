package com.final_project.impresent;

public class Student {
    private String name, id, password, email;
    private int sem;

    public Student() {
    }

    public Student(String name, String id, String password, int sem, String email) {
        this.name = name;
        this.id = id;
        this.password = password;
        this.sem = sem;
        this.email = email;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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
}
