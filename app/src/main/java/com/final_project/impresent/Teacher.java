package com.final_project.impresent;

public class Teacher {
    private String name,id,pass,email;


    public Teacher() {
    }

    public Teacher(String name, String id, String email , String pass) {
        this.name = name;
        this.id = id;
        this.pass = pass;
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


    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
