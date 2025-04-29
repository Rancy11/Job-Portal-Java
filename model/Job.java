package model;

import java.util.ArrayList;

public class Job {
    String title;
    String description;
    String companyName;
    ArrayList<String> applicants = new ArrayList<>();

    public Job(String title, String description, String companyName) {
        this.title = title;
        this.description = description;
        this.companyName = companyName;
    }

    public void addApplicant(String applicantName) {
        applicants.add(applicantName);
    }

    // Getters and Setters
    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getCompanyName() {
        return companyName;
    }

    public ArrayList<String> getApplicants() {
        return applicants;
    }
}