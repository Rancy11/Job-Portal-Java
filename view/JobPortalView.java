package view;

import java.util.Scanner;

public class JobPortalView {
    private Scanner scanner;

    public JobPortalView() {
        scanner = new Scanner(System.in);
    }

    // Role Selection
    public void displayRoleSelection() {
        System.out.println("\n=== Welcome to the Job Portal ===");
        System.out.println("Are you an employer or a job seeker?");
        System.out.println("1. Employer");
        System.out.println("2. Job Seeker");
        System.out.println("3. Exit");
        System.out.print("Choose an option: ");
    }

    // Employer Menu
    public void displayEmployerMenu() {
        System.out.println("\n--- Employer Menu ---");
        System.out.println("1. Register Employer");
        System.out.println("2. Post Job");
        System.out.println("3. View Applicants");
        System.out.println("4. Change Application Status");
        System.out.println("5. Exit");
        System.out.print("Choose an option: ");
    }

    // Job Seeker Menu
    public void displayJobSeekerMenu() {
        System.out.println("\n--- Job Seeker Menu ---");
        System.out.println("1. Register");
        System.out.println("2. View Jobs");
        System.out.println("3. Apply for Job");
        System.out.println("4. Exit");
        System.out.print("Choose an option: ");
    }

    // Safe user choice with validation
    public int getUserChoice() {
        try {
            int choice = Integer.parseInt(scanner.nextLine().trim());
            return choice;
        } catch (NumberFormatException e) {
            return -1; // Invalid input
        }
    }

    // Prompt for user input
    public String getInput(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    // General message output
    public void displayMessage(String message) {
        System.out.println(message);
    }

    // Optional: Close scanner when exiting program
    public void closeScanner() {
        if (scanner != null) {
            scanner.close();
        }
    }
}
