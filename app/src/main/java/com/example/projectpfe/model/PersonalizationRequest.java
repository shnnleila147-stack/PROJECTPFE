package com.example.projectpfe.model;
public class PersonalizationRequest {
    private int userId;
    private int q1, q2, q3, q4, q5;

    // إضافة Setters
    public void setUserId(int userId) { this.userId = userId; }
    public void setQ1(int q1) { this.q1 = q1; }
    public void setQ2(int q2) { this.q2 = q2; }
    public void setQ3(int q3) { this.q3 = q3; }
    public void setQ4(int q4) { this.q4 = q4; }
    public void setQ5(int q5) { this.q5 = q5; }

    // إضافة Getters إذا احتجت لاحقاً
    public int getUserId() { return userId; }
    public int getQ1() { return q1; }
    public int getQ2() { return q2; }
    public int getQ3() { return q3; }
    public int getQ4() { return q4; }
    public int getQ5() { return q5; }
}