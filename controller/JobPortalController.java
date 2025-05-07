package controller;

import model.ApplicantBST;
import model.Company;
import model.Job;
import model.DatabaseConnection;
import view.JobPortalView;

import java.math.BigDecimal;
import java.sql.*;
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
            switch (roleChoice) {
                case 1 -> employerMenu();
                case 2 -> jobSeekerMenu();
                case 3 -> {
                    view.displayMessage("👋 Exiting... Goodbye!");
                    return;
                }
                default -> view.displayMessage("❌ Invalid choice! Please enter 1, 2 or 3.");
            }
        }
    }

    // ---------------------------- EMPLOYER MENU ----------------------------
    private void employerMenu() {
        while (true) {
            view.displayEmployerMenu();
            int choice = view.getUserChoice();
            switch (choice) {
                case 1 -> registerEmployer();
                case 2 -> postJob();
                case 3 -> viewApplicants();
                case 4 -> changeApplicationStatus();
                case 5 -> {
                    view.displayMessage("🔙 Returning to main menu...");
                    return;
                }
                default -> view.displayMessage("❌ Invalid choice! Please enter a number from 1 to 5.");
            }
        }
    }

    private void registerEmployer() {
        String name = view.getInput("Enter name: ");
        String email = view.getInput("Enter email: ");
        String company = view.getInput("Enter company name: ");

        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "INSERT INTO employers (name, email, company_name) VALUES (?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, name);
            stmt.setString(2, email);
            stmt.setString(3, company);
            stmt.executeUpdate();
            view.displayMessage("✅ Employer registered!");
        } catch (SQLException e) {
            view.displayMessage("❌ Error registering employer: " + e.getMessage());
        }
    }

    private void postJob() {
        String email = view.getInput("Enter your registered employer email: ");
        int employerId = getEmployerIdByEmail(email);

        if (employerId == -1) {
            view.displayMessage("❌ Email not registered as employer. Please register first.");
            return;
        }

        String cname = null;

        try (Connection conn = DatabaseConnection.getConnection()) {
            // Fetch company name from employers table
            String sql = "SELECT company_name FROM employers WHERE email = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                cname = rs.getString("company_name");
            } else {
                view.displayMessage("❌ Could not find company name for the provided email.");
                return;
            }

            String industry = view.getInput("Enter industry: ");
            String title = view.getInput("Enter job title: ");
            String desc = view.getInput("Enter job description: ");
            BigDecimal salary = null;

            while (salary == null) {
                try {
                    String input = view.getInput("Enter job salary (e.g., 50000.00): ");
                    salary = new BigDecimal(input);
                } catch (NumberFormatException e) {
                    view.displayMessage("❌ Invalid salary. Please enter a valid number.");
                }
            }

            // Insert or update company
            sql = "INSERT INTO companies (name, industry) VALUES (?, ?) ON DUPLICATE KEY UPDATE name=name";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, cname);
            stmt.setString(2, industry);
            stmt.executeUpdate();

            // Get company ID
            sql = "SELECT id FROM companies WHERE name = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, cname);
            rs = stmt.executeQuery();
            int companyId = 0;
            if (rs.next()) {
                companyId = rs.getInt("id");
            }

            // Insert job
            sql = "INSERT INTO jobs (title, description, company_id, salary, employer_id) VALUES (?, ?, ?, ?, ?)";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, title);
            stmt.setString(2, desc);
            stmt.setInt(3, companyId);
            stmt.setBigDecimal(4, salary);
            stmt.setInt(5, employerId);
            stmt.executeUpdate();

            view.displayMessage("✅ Job posted successfully!");

        } catch (SQLException e) {
            view.displayMessage("❌ Error posting job: " + e.getMessage());
        }
    }


    private void viewApplicants() {
        String email = view.getInput("Enter your registered employer email: ");
        int employerId = getEmployerIdByEmail(email);

        if (employerId == -1) {
            view.displayMessage("❌ Email not registered as employer. Please register first.");
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = """
                SELECT a.name, a.email, a.skills, ap.status, j.title
                FROM applications ap
                JOIN applicants a ON ap.applicant_id = a.id
                JOIN jobs j ON ap.job_id = j.id
                WHERE j.employer_id = ?
            """;
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, employerId);
            ResultSet rs = stmt.executeQuery();

            boolean found = false;
            while (rs.next()) {
                found = true;
                String name = rs.getString("name");
                String applicantEmail = rs.getString("email");
                String skills = rs.getString("skills");
                String status = rs.getString("status");
                String jobTitle = rs.getString("title");

                view.displayMessage("👤 " + name + " | 📧 " + applicantEmail + " | 💼 " + jobTitle +
                        " | 🛠️ " + skills + " | 📌 Status: " + status);
            }

            if (!found) {
                view.displayMessage("ℹ️ No applicants found for your posted jobs.");
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
            String sql = """
                UPDATE applications ap
                JOIN applicants a ON ap.applicant_id = a.id
                JOIN jobs j ON ap.job_id = j.id
                SET ap.status = ?
                WHERE a.email = ? AND j.title = ?
            """;
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, newStatus);
            stmt.setString(2, applicantEmail);
            stmt.setString(3, jobTitle);
            int rows = stmt.executeUpdate();

            if (rows > 0) {
                view.displayMessage("✅ Application status updated!");
            } else {
                view.displayMessage("❌ No matching application found.");
            }
        } catch (SQLException e) {
            view.displayMessage("❌ Error updating status: " + e.getMessage());
        }
    }

    private int getEmployerIdByEmail(String email) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT id FROM employers WHERE email = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getInt("id");
        } catch (SQLException e) {
            view.displayMessage("❌ Error fetching employer ID: " + e.getMessage());
        }
        return -1;
    }

    // -------------------------- JOB SEEKER MENU --------------------------
    private void jobSeekerMenu() {
        while (true) {
            view.displayJobSeekerMenu();
            int choice = view.getUserChoice();
            switch (choice) {
                case 1 -> registerApplicant();
                case 2 -> viewJobs();
                case 3 -> applyForJob();
                case 4 -> {
                    view.displayMessage("🔙 Returning to main menu...");
                    return;
                }
                default -> view.displayMessage("❌ Invalid choice! Please enter a number from 1 to 4.");
            }
        }
    }

    private void registerApplicant() {
        String name = view.getInput("Enter name: ");
        String email = view.getInput("Enter email: ");
        String skills = view.getInput("Enter skills (comma-separated): ");

        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "INSERT INTO applicants (name, email, skills) VALUES (?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, name);
            stmt.setString(2, email);
            stmt.setString(3, skills);
            stmt.executeUpdate();
            view.displayMessage("✅ Applicant registered!");
        } catch (SQLException e) {
            view.displayMessage("❌ Error registerin ng applicant: " + e.getMessage());
        }
    }

    private void viewJobs() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT j.id, j.title, c.name AS company_name, j.salary " +
                    "FROM jobs j JOIN companies c ON j.company_id = c.id";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            System.out.println("\n📋 Available Job Listings:");
            System.out.println("-------------------------------------------------------------");
            System.out.printf("%-5s %-20s %-20s %-10s\n", "ID", "Title", "Company", "Salary (₹)");
            System.out.println("-------------------------------------------------------------");

            while (rs.next()) {
                int id = rs.getInt("id");
                String title = rs.getString("title");
                String company = rs.getString("company_name");
                BigDecimal salary = rs.getBigDecimal("salary");

                System.out.printf("%-5d %-20s %-20s ₹%-10.2f\n", id, title, company, salary);
            }
            System.out.println("-------------------------------------------------------------");

        } catch (SQLException e) {
            view.displayMessage("❌ Error fetching jobs: " + e.getMessage());
        }
    }

    private void applyForJob() {
        String email = view.getInput("Enter your registered email: ");
        String jobTitle = view.getInput("Enter job title to apply: ");

        try (Connection conn = DatabaseConnection.getConnection()) {
            // Step 1: Get applicant ID
            String sql = "SELECT id FROM applicants WHERE email = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            int applicantId = -1;
            if (rs.next()) {
                applicantId = rs.getInt("id");
            } else {
                view.displayMessage("❌ Email not found. Please register first.");
                return;
            }

            // Step 2: List all matching jobs with that title
            sql = """
            SELECT j.id, j.title, c.name AS company_name, j.salary
            FROM jobs j
            JOIN companies c ON j.company_id = c.id
            WHERE j.title = ?
        """;
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, jobTitle);
            rs = stmt.executeQuery();

            ArrayList<Integer> jobIds = new ArrayList<>();
            System.out.println("\n🔍 Matching Jobs:");
            while (rs.next()) {
                int id = rs.getInt("id");
                String company = rs.getString("company_name");
                BigDecimal salary = rs.getBigDecimal("salary");
                System.out.println("ID: " + id + " | Title: " + jobTitle + " | Company: " + company + " | Salary: " + salary);
                jobIds.add(id);
            }

            if (jobIds.isEmpty()) {
                view.displayMessage("❌ No jobs found with the title: " + jobTitle);
                return;
            }

            // Step 3: Ask user to select job ID to apply
            int selectedJobId = Integer.parseInt(view.getInput("Enter the Job ID you want to apply for: "));
            if (!jobIds.contains(selectedJobId)) {
                view.displayMessage("❌ Invalid Job ID selected.");
                return;
            }

            // Step 4: Apply
            sql = "INSERT INTO applications (applicant_id, job_id, status) VALUES (?, ?, 'pending')";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, applicantId);
            stmt.setInt(2, selectedJobId);
            stmt.executeUpdate();
            view.displayMessage("✅ Application submitted successfully!");
        } catch (SQLException e) {
            view.displayMessage("❌ Error applying: " + e.getMessage());
        }
    }

}
