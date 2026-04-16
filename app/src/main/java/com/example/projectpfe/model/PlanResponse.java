package com.example.projectpfe.model;

import java.util.List;

public class PlanResponse {

    public Plan plan1;
    public Plan plan2;

    public static class Plan {
        public String title;
        public String description;
        public List<String> steps;
    }
}