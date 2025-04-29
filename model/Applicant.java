package model;

import java.util.ArrayList;

public class Applicant {
    String name;
    String email;
    ArrayList<String> skills = new ArrayList<>();
    ArrayList<String> appliedJobs = new ArrayList<>();

    public Applicant(String name, String email) {
        this.name = name;
        this.email = email;
    }

    public void addSkill(String skill) {
        skills.add(skill);
    }

    public void applyJob(String jobTitle) {
        appliedJobs.add(jobTitle);
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public ArrayList<String> getSkills() {
        return skills;
    }

    public ArrayList<String> getAppliedJobs() {
        return appliedJobs;
    }
}