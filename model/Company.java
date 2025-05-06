package model;
import java.util.ArrayList;
public class Company {
    String name;
    String industry;
    ArrayList<Job> postedJobs = new ArrayList<>();

    public Company(String name, String industry) {
        this.name = name;
        this.industry = industry;
    }
    public void postJob(Job job) {
        postedJobs.add(job);
    }
    // Getters and Setters
    public String getName() {
        return name;
    }
    public String getIndustry() {
        return industry;
    }
    public ArrayList<Job> getPostedJobs() {
        return postedJobs;
    }
}