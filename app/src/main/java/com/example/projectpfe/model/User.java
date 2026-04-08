package com.example.projectpfe.model;

import com.google.gson.annotations.SerializedName;

public class User {

    private int id; // ✅ مهم جداً
    private String email;
    private String password;
    private String name;
    private String grade;

    @SerializedName("personalized")
    private boolean isPersonalized;

    // constructor
    public User(String email, String password) {
        this.email = email;
        this.password = password;
    }

    // getters & setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isPersonalized() {
        return isPersonalized;
    }

    public void setPersonalized(boolean personalized) {
        isPersonalized = personalized;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    // ✅ جديد: getters & setters للـ name
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // ✅ جديد: getters & setters للـ grade
    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }
}