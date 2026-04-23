package com.example.projectpfe.model;

import java.util.List;

public class PlanResponse {

        public String title;
        public List<String> steps;
        public List<Boolean> doneSteps;
        public int progress;

        // Constructor الصحيح
        public PlanResponse() {
        }
}