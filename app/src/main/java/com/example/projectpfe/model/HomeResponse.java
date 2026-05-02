package com.example.projectpfe.model;

import java.util.List;

public class HomeResponse {

    private String userName;
    private int progress;
    private int timeFocus;
    private int streak;
    private List<String> goals;
    private List<Integer> goalProgress;


    public String getUserName() { return userName; }
    public int getProgress() { return progress; }
    public int getTimeFocus() { return timeFocus; }
    public int getStreak() { return streak; }
    public List<String> getGoals() { return goals; }
    public List<Integer> getGoalProgress() {
        return goalProgress;
    }

}