package controller;

import model.Applicant;
import model.ApplicantBST;
import model.Company;
import model.Job;
import model.DatabaseConnection;
import view.JobPortalView;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class JobPortalController {
    private JobPortalView view;
    private ApplicantBST applicantTree = new ApplicantBST();
    private ArrayList<Company> companies = new ArrayList<>();
    private ArrayList<Job> jobs = new ArrayList<>();

    public JobPortalController(JobPortalView view) {
        this.view = view;
    }

    public void start() {
        while (true) {
            view.displayRoleSelection();
            int roleChoice = view.getUserChoice();
            if (roleChoice == 1) {
                employerMenu();
            } else if (roleChoice == 2) {
                jobSeekerMenu();
            } else {
                view.displayMessage("❌ Invalid choice!");
            }
        }
    }

    private void employerMenu() {
        while (true) {
            view.displayEmployerMenu();
            int choice = view.getUserChoice();
            switch (choice) {
                case 1:
                    postJob();
                    break;
                case 2:
                    viewApplicants();
                    break;
                case 3:
                    changeApplicationStatus();
                    break;
                case 4:
                    return; // Exit to main menu
                default:
                    view.displayMessage("❌ Invalid choice!");
            }
        }
    }

    private void jobSeekerMenu() {
        while (true) {
            view.displayJobSeekerMenu();
            int choice = view.getUserChoice();
            switch (choice) {
                case 1:
                    registerApplicant();
                    break;
                case 2:
                    viewJobs();
                    break;
                case 3:
                    applyForJob();
                    break;
                case 4:
                    return; // Exit to main menu
                default:
                    view.displayMessage("❌ Invalid choice!");
            }
        }
    }

    private void registerApplicant() {
        String name = view.getInput("Enter name: ");
        String email = view.getInput("Enter email: ");
        String skills = view.getInput("Enter skills (comma separated): ");
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "INSERT INTO applicants (name, email, skills) VALUES (?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, name);
            stmt.setString(2, email);
            stmt.setString(3, skills);
            stmt.executeUpdate();
            view.displayMessage("✅ Applicant registered!");
        } catch (SQLException e) {
            view.displayMessage("❌ Error registering applicant: " + e.getMessage());
        }
    }

    private void postJob() {
        String cname = view.getInput("Enter company name: ");
        String industry = view.getInput("Enter industry: ");
        String title = view.getInput("Enter job title: ");
        String desc = view.getInput("Enter job description: ");
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "INSERT INTO companies (name, industry) VALUES (?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, cname);
            stmt.setString(2, industry);
            stmt.executeUpdate();

            // Get the company ID
            sql = "SELECT id FROM companies WHERE name = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, cname);
            ResultSet rs = stmt.executeQuery();
            int companyId = 0;
            if (rs.next()) {
                companyId = rs.getInt("id");
            }

            // Post the job
            sql = "INSERT INTO jobs (title, description, company_id) VALUES (?, ?, ?)";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, title);
            stmt.setString(2, desc);
            stmt.setInt(3, companyId);
            stmt.executeUpdate();
            view.displayMessage("✅ Job posted!");
        } catch (SQLException e) {
            view.displayMessage("❌ Error posting job: " + e.getMessage());
        }
    }

    private void viewApplicants() {
        // Logic to view applicants for a specific job
        String jobTitle = view.getInput("Enter job title to view applicants: ");
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT a.name, a.email, a.skills, ap.status FROM applicants a " +
                         "JOIN applications ap ON a.id = ap.applicant_id " +
                         "JOIN jobs j ON ap.job_id = j.id WHERE j.title = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, jobTitle);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String applicantName = rs.getString("name");
                String applicantEmail = rs.getString("email");
                String applicantSkills = rs.getString("skills");
                String status = rs.getString("status");
                view.displayMessage("Applicant: " + applicantName + ", Email: " + applicantEmail + 
                                    ", Skills: " + applicantSkills + ", Status: " + status);
            }
        } catch (SQLException e) {
            view.displayMessage("❌ Error retrieving applicants: " + e.getMessage());
        }
    }

    private void changeApplicationStatus() {
        String applicantEmail = view.getInput("Enter applicant email: ");
        String jobTitle = view.getInput("Enter job title: ");
        String newStatus = view.getInput("Enter new status (accepted/rejected): ");
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "UPDATE applications ap JOIN applicants a ON ap.applicant_id = a.id " +
                         "JOIN jobs j ON ap.job_id = j.id SET ap.status = ? " +
                         "WHERE a.email = ? AND j.title = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, newStatus);
            stmt.setString(2, applicantEmail);
            stmt.setString(3, jobTitle);
            stmt.executeUpdate();
            view.displayMessage("✅ Application status updated!");
        } catch (SQLException e) {
            view.displayMessage("❌ Error updating application status: " + e.getMessage());
        }
    }

    private void viewJobs() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT title, description FROM jobs";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String title = rs.getString("title");
                String description = rs.getString("description");
                view.displayMessage("Job Title: " + title + ", Description: " + description);
            }
        } catch (SQLException e) {
            view.displayMessage("❌ Error retrieving jobs: " + e.getMessage());
        }
    }

    private void applyForJob() {
        String email = view.getInput("Enter your email: ");
        String jobTitle = view.getInput("Enter job title to apply: ");
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Get applicant ID
            String sql = "SELECT id FROM applicants WHERE email = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            int applicantId = 0;
            if (rs.next()) {
                applicantId = rs.getInt("id");
            }

            // Get job ID
            sql = "SELECT id FROM jobs WHERE title = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, jobTitle);
            rs = stmt.executeQuery();
            int jobId = 0;
            if (rs.next()) {
                jobId = rs.getInt("id");
            }

            // Insert application
            sql = "INSERT INTO applications (applicant_id, job_id, status) VALUES (?, ?, 'pending')";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, applicantId);
            stmt.setInt(2, jobId);
            stmt.executeUpdate();
            view.displayMessage("✅ Applied for job successfully!");
        } catch (SQLException e) {
            view.displayMessage("❌ Error applying for job: " + e.getMessage());
        }
    }
}