package view;

import java.util.Scanner;

public class JobPortalView {
    private Scanner input;

    public JobPortalView() {
        input = new Scanner(System.in);
    }

    public void displayRoleSelection() {
        System.out.println("\n=== Job Portal ===");
        System.out.println("1. Employer");
        System.out.println("2. Job Seeker");
        System.out.println("3. Exit");
        System.out.print("Select your role: ");
    }

    public void displayEmployerMenu() {
        System.out.println("\n--- Employer Menu ---");
        System.out.println("1. Register as Employer");
        System.out.println("2. Post a Job");
        System.out.println("3. View My Applicants");
        System.out.println("4. Update Application Status");
        System.out.println("5. Exit");
        System.out.print("Choose an option: ");
    }

    public void displayJobSeekerMenu() {
        System.out.println("\n--- Job Seeker Menu ---");
        System.out.println("1. Register as Job Seeker");
        System.out.println("2. View Available Jobs");
        System.out.println("3. Apply for a Job");
        System.out.println("4. View Applied Job Status");
        System.out.println("5. Exit");
        System.out.print("Choose an option: ");
    }

    public int getUserChoice() {
        try {
            return Integer.parseInt(input.nextLine().trim());
        } catch (Exception e) {
            return -1;
        }
    }

    public String getInput(String promptText) {
        System.out.print(promptText);
        return input.nextLine().trim();
    }

    public void displayMessage(String msg) {
        System.out.println(msg);
    }

    public void displayExitMessage() {
        System.out.println("\nThank you for using the Job Portal. Goodbye!");
    }
}
