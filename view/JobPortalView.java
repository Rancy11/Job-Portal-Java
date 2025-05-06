package view;

import java.util.Scanner;

public class JobPortalView {
    private Scanner scanner;

    public JobPortalView() {
        scanner = new Scanner(System.in);
    }

    public void displayRoleSelection() {
        System.out.println("Are you an employer or a job seeker?");
        System.out.println("1. Employer");
        System.out.println("2. Job Seeker");
        System.out.print("Choose an option: ");
    }

    public void displayEmployerMenu() {
        System.out.println("\n--- Employer Menu ---");
        System.out.println("1. Post Job");
        System.out.println("2. View Applicants");
        System.out.println("3. Change Application Status");
        System.out.println("4. Exit");
        System.out.print("Choose an option: ");
    }

    public void displayJobSeekerMenu() {
        System.out.println("\n--- Job Seeker Menu ---");
        System.out.println("1. Register");
        System.out.println("2. View Jobs");
        System.out.println("3. Apply for Job");
        System.out.println("4. Exit");
        System.out.print("Choose an option: ");
    }

    public int getUserChoice() {
        int choice = scanner.nextInt();
        scanner.nextLine(); // Consume leftover newline
        return choice;
    }


    public String getInput(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine();
    }

    public void displayMessage(String message) {
        System.out.println(message);
    }
}