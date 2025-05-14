package controller;

import model.ApplicantBST;
import model.Company;
import model.Job;
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
            int choice = view.getUserChoice();
            switch (choice) {
                case 1 -> handleEmployer();
                case 2 -> handleJobSeeker();
                case 3 -> {
                    view.displayMessage("👋 Exiting... Goodbye!");
                    return;
                }
                default -> view.displayMessage("❌ Please select 1, 2, or 3.");
            }
        }
    }

    // ========================== EMPLOYER ==========================

    private void handleEmployer() {
        while (true) {
            view.displayEmployerMenu();
            int choice = view.getUserChoice();
            switch (choice) {
                case 1 -> registerEmployer();
                case 2 -> addNewJob();
                case 3 -> listApplicants();
                case 4 -> updateApplicationStatus();
                case 5 -> {
                    view.displayMessage("🔙 Back to main menu.");
                    return;
                }
                default -> view.displayMessage("❌ Enter a valid option (1-5).");
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

            view.displayMessage("✅ Employer registered successfully.");
        } catch (SQLException e) {
            view.displayMessage("❌ Error: " + e.getMessage());
        }
    }
    private void addNewJob() {
        String email = view.getInput("Enter your registered employer email: ");
        int employerId = getEmployerId(email);
        if (employerId == -1) {
            view.displayMessage("❌ Not registered. Please register as an employer.");
            return;
        }
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Get company name
            String getCompanySQL = "SELECT company_name FROM employers WHERE email = ?";
            PreparedStatement stmt = conn.prepareStatement(getCompanySQL);
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();

            if (!rs.next()) {
                view.displayMessage("❌ No company found.");
                return;
            }

            String companyName = rs.getString("company_name");

            // Get job details
            String industry = view.getInput("Enter industry: ");
            String title = view.getInput("Enter job title: ");
            String desc = view.getInput("Enter job description: ");
            BigDecimal salary = null;

            while (salary == null) {
                try {
                    salary = new BigDecimal(view.getInput("Enter salary: "));
                } catch (NumberFormatException e) {
                    view.displayMessage("❌ Enter valid salary.");
                }
            }

            // Insert company (if not already)
            String insertCompanySQL = "INSERT INTO companies (name, industry) VALUES (?, ?) ON DUPLICATE KEY UPDATE name=name";
            stmt = conn.prepareStatement(insertCompanySQL);
            stmt.setString(1, companyName);
            stmt.setString(2, industry);
            stmt.executeUpdate();

            // Get company ID
            String getCompanyIdSQL = "SELECT id FROM companies WHERE name = ?";
            stmt = conn.prepareStatement(getCompanyIdSQL);
            stmt.setString(1, companyName);
            rs = stmt.executeQuery();
            int companyId = rs.next() ? rs.getInt("id") : 0;

            // Insert job
            String insertJobSQL = "INSERT INTO jobs (title, description, company_id, salary, employer_id) VALUES (?, ?, ?, ?, ?)";
            stmt = conn.prepareStatement(insertJobSQL);
            stmt.setString(1, title);
            stmt.setString(2, desc);
            stmt.setInt(3, companyId);
            stmt.setBigDecimal(4, salary);
            stmt.setInt(5, employerId);
            stmt.executeUpdate();

            view.displayMessage("✅ Job added successfully.");

        } catch (SQLException e) {
            view.displayMessage("❌ Error posting job: " + e.getMessage());
        }
    }

    private void listApplicants() {
        String email = view.getInput("Enter your registered employer email: ");
        int employerId = getEmployerId(email);

        if (employerId == -1) {
            view.displayMessage("❌ Please register first.");
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

            boolean hasData = false;
            while (rs.next()) {
                hasData = true;
                view.displayMessage("👤 Name : " + rs.getString("name") +
                        " | 📧 Email : " + rs.getString("email") +
                        " | 💼 Title : " + rs.getString("title") +
                        " | 🛠️ Skills : " + rs.getString("skills") +
                        " | 📌 Status : " + rs.getString("status"));
            }

            if (!hasData) view.displayMessage("ℹ️ No applicants yet.");
        } catch (SQLException e) {
            view.displayMessage("❌ Error fetching applicants: " + e.getMessage());
        }
    }

    private void updateApplicationStatus() {
        String employerEmail = view.getInput("Enter your (employer) email: ");
        String applicantEmail = view.getInput("Enter applicant email: ");
        String jobTitle = view.getInput("Enter job title: ");
        String status = view.getInput("Enter new status (accepted/rejected): ");

        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = """
            UPDATE applications ap
            JOIN applicants a ON ap.applicant_id = a.id
            JOIN jobs j ON ap.job_id = j.id
            JOIN employers e ON j.employer_id = e.id
            SET ap.status = ?
            WHERE a.email = ? AND j.title = ? AND e.email = ?
        """;

            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, status);
            stmt.setString(2, applicantEmail);
            stmt.setString(3, jobTitle);
            stmt.setString(4, employerEmail); // Match only the correct employer

            int updated = stmt.executeUpdate();
            if (updated > 0) {
                view.displayMessage("✅ Application status updated.");
            } else {
                view.displayMessage("❌ No matching application found or unauthorized attempt.");
            }
        } catch (SQLException e) {
            view.displayMessage("❌ Error updating status: " + e.getMessage());
        }
    }


    private int getEmployerId(String email) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT id FROM employers WHERE email = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getInt("id");
        } catch (SQLException e) {
            view.displayMessage("❌ Error fetching employer: " + e.getMessage());
        }
        return -1;
    }

    // ========================== JOB SEEKER FLOW ==========================

    private void handleJobSeeker() {
        while (true) {
            view.displayJobSeekerMenu();
            int choice = view.getUserChoice();
            switch (choice) {
                case 1 -> registerApplicant();
                case 2 -> showAllJobs();
                case 3 -> applyToJob();
                case 4 -> viewAppliedJobs();
                case 5 -> {
                    view.displayMessage("🔙 Back to main menu.");
                    return;
                }
                default -> view.displayMessage("❌ Choose 1 to 4.");
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

            view.displayMessage("✅ Registered successfully.");
        } catch (SQLException e) {
            view.displayMessage("❌ Error: " + e.getMessage());
        }
    }

    private void showAllJobs() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = """
                SELECT j.id, j.title, c.name AS company_name, j.salary
                FROM jobs j
                JOIN companies c ON j.company_id = c.id
            """;
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            System.out.println("\n📋 Job Listings:");
            System.out.println("--------------------------------------------");
            System.out.printf("%-5s %-20s %-20s %-10s\n", "ID", "Title", "Company", "Salary");
            System.out.println("--------------------------------------------");

            while (rs.next()) {
                System.out.printf("%-5d %-20s %-20s ₹%-10.2f\n",
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("company_name"),
                        rs.getBigDecimal("salary"));
            }

            System.out.println("--------------------------------------------");
        } catch (SQLException e) {
            view.displayMessage("❌ Error loading jobs: " + e.getMessage());
        }
    }

    private void applyToJob() {
        String email = view.getInput("Enter your email: ");
        String jobTitle = view.getInput("Enter job title to apply: ");

        try (Connection conn = DatabaseConnection.getConnection()) {
            // Get applicant ID
            String sql = "SELECT id FROM applicants WHERE email = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();

            int applicantId = -1;
            if (rs.next()) {
                applicantId = rs.getInt("id");
            } else {
                view.displayMessage("❌ Please register first.");
                return;
            }

            // Get matching jobs
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
                jobIds.add(id);
                System.out.println("ID: " + id + " | Company: " + rs.getString("company_name") + " | Salary: ₹" + rs.getBigDecimal("salary"));
            }

            if (jobIds.isEmpty()) {
                view.displayMessage("❌ No job found.");
                return;
            }

            int jobId = Integer.parseInt(view.getInput("Enter job ID to apply: "));
            if (!jobIds.contains(jobId)) {
                view.displayMessage("❌ Invalid Job ID.");
                return;
            }

            // Check if applicant already applied to this exact job
            sql = "SELECT COUNT(*) FROM applications WHERE applicant_id = ? AND job_id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, applicantId);
            stmt.setInt(2, jobId);
            rs = stmt.executeQuery();

            if (rs.next() && rs.getInt(1) > 0) {
                view.displayMessage("❌ You've already applied to this job.");
                return;
            }

            // Apply
            sql = "INSERT INTO applications (applicant_id, job_id, status) VALUES (?, ?, 'pending')";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, applicantId);
            stmt.setInt(2, jobId);
            stmt.executeUpdate();

            view.displayMessage("✅ Application submitted.");
        } catch (SQLException e) {
            view.displayMessage("❌ Error applying: " + e.getMessage());
        }
    }

    private void viewAppliedJobs() {
        String email = view.getInput("Enter your email: ");
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = """
            SELECT j.title, c.name AS company_name, ap.status
            FROM applications ap
            JOIN jobs j ON ap.job_id = j.id
            JOIN companies c ON j.company_id = c.id
            JOIN applicants a ON ap.applicant_id = a.id
            WHERE a.email = ?
        """;
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();

            boolean hasApplications = false;
            while (rs.next()) {
                hasApplications = true;
                String title = rs.getString("title");
                String company = rs.getString("company_name");
                String status = rs.getString("status");
                view.displayMessage("💼Title " + title + " | 🏢 Company" + company + " | 📌 Status: " + status);
            }

            if (!hasApplications) {
                view.displayMessage("ℹ️ You haven't applied to any jobs yet.");
            }
        } catch (SQLException e) {
            view.displayMessage("❌ Error fetching applications: " + e.getMessage());
        }
    }

}
