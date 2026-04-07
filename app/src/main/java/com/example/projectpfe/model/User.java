package com.example.projectpfe.model;

import com.google.gson.annotations.SerializedName;

public class User {

    private int id; // ✅ مهم جداً
    private String email;
    private String password;
    @SerializedName("personalized")
    private boolean isPersonalized;

    public User(String email, String password) {
        this.email = email;
        this.password = password;
    }

    // ✅ getter id
    public int getId() {
        return id;
    }

    // ✅ setter id (مهم للـ Retrofit)
    public void setId(int id) {
        this.id = id;
    }

    public boolean isPersonalized() {
        return isPersonalized;
    }

    public void setPersonalized(boolean personalized) {
        isPersonalized = personalized;
    }

    public String getEmail() { return email; }
    public String getPassword() { return password; }
}