package com.employeepayroll.app;

/*
 * ==========================================================
 * EMPLOYEE PAYROLL APP (UC1 + UC2 IN ONE RUNNER)
 * ==========================================================
 *
 * Goal of this Use Case:
 * - Demonstrate a simple authentication flow
 * - Verify credentials using stored (hashed) password data
 *
 * This use case builds directly on UC1.
 * @author Developer
 * @version 2.0
 */


import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Scanner;

import com.employeepayroll.exceptions.ValidationException;
import com.employeepayroll.model.Employee;
import com.employeepayroll.model.UserAccount;
import com.employeepayroll.util.Validator;

public class EmployeePayrollApp {

    private static final int MAX_LOGIN_ATTEMPTS = 3;

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        System.out.println("=== EMPLOYEE PAYROLL APP ===\n");
        System.out.println("1. Register");
        System.out.println("2. Login\n");

        System.out.print("Enter choice: ");
        String choice = sc.nextLine().trim();

        switch (choice) {
            case "1" -> register(sc);
            case "2" -> login(sc);
            default -> System.out.println("Invalid choice");
        }

        sc.close();
    }

    private static void register(Scanner sc) {

        System.out.println("\n=== USE CASE 1: EMPLOYEE REGISTRATION ===\n");

        try {
            System.out.print("Enter Employee ID (EMP-XXXX): ");
            String empId = sc.nextLine();
            Validator.validateEmpId(empId);

            System.out.print("Enter Name: ");
            String name = sc.nextLine();

            System.out.print("Enter Email: ");
            String email = sc.nextLine();
            Validator.validateEmail(email);

            System.out.print("Enter Phone (10 digits starting 6-9): ");
            String phone = sc.nextLine();
            Validator.validatePhone(phone);

            System.out.print("\nCreate Username: ");
            String username = sc.nextLine();

            System.out.print("\nCreate Password: ");
            String password = sc.nextLine();

            // Create objects (Composition: Employee HAS a UserAccount)
            UserAccount account = new UserAccount(username, password);
            Employee employee = new Employee(empId.trim(), name.trim(), email.trim(), phone.trim(), account);

            // Persist
            employee.persist();

            // Confirm
            System.out.println("\n------------------------------------\n");
            System.out.println("Employee Registered Successfully:\n");
            System.out.println(employee);
            System.out.println("\nData persisted in file: employee_data.txt");
        } catch (ValidationException e) {
            System.out.println("\nValidation Failed: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("\nError saving employee data!");
        }
    }

    private static void login(Scanner sc) {

        System.out.println("\n=== USE CASE 2: EMPLOYEE AUTHENTICATION & LOGIN ===\n");

        Path filePath = Path.of("employee_data.txt");
        if (!Files.exists(filePath)) {
            System.out.println("No employee data found. Please register first.");
            return;
        }

        for (int attempt = 1; attempt <= MAX_LOGIN_ATTEMPTS; attempt++) {

            System.out.print("Enter Username: ");
            String username = sc.nextLine().trim();

            System.out.print("Enter Password: ");
            String password = sc.nextLine();

            UserAccount account = loadAccountFromFile(username);

            if (account != null && account.authenticate(password)) {
                System.out.println("\nLogin Successful!");
                System.out.println("Role: EMPLOYEE\n");

                System.out.println("====== DASHBOARD ======");
                System.out.println("Employee Dashboard");
                System.out.println("View Payslip | Update Profile\n");

                Session session = new Session(username);
                System.out.println(session);

                if (session.isExpired()) {
                    System.out.println("Session expired.");
                } else {
                    System.out.println("Session active and valid.");
                }

                return;
            }

            int remaining = MAX_LOGIN_ATTEMPTS - attempt;
            System.out.println("\nLogin Failed!");

            if (remaining > 0) {
                System.out.println("Attempts left: " + remaining + "\n");
            } else {
                System.out.println("Too many failed attempts.");
                System.out.println("Failed login notification sent.");
            }
        }
    }

    private static UserAccount loadAccountFromFile(String username) {
        try {
            List<String> lines = Files.readAllLines(Path.of("employee_data.txt"), StandardCharsets.UTF_8);

            String foundHash = null;
            String foundSalt = null;

            for (String line : lines) {
                if (line == null) {
                    continue;
                }

                String trimmed = line.trim();
                if (trimmed.isEmpty()) {
                    continue;
                }

                // File format from Employee.persist():
                // time|internalId|empId|name|email|phone|username|passwordHash|salt
                String[] parts = trimmed.split("\\|", -1);
                if (parts.length < 9) {
                    continue;
                }

                String fileUsername = parts[6];
                if (username.equals(fileUsername)) {
                    foundHash = parts[7];
                    foundSalt = parts[8];
                }
            }

            if (foundHash == null || foundSalt == null) {
                return null;
            }

            return new UserAccount(username, foundHash, foundSalt, true);
        } catch (IOException e) {
            return null;
        }
    }

    /*
     * Session represents a logged-in user state.
     *
     * Why this class exists:
     * - Login is not permanent
     * - Session has a lifetime
     *
     * This introduces the idea of time-based state.
     */
    private static class Session {

        private String username;
        private long loginTime;
        private long timeoutMillis;

        public Session(String username) {
            this(username, 60_000);
        }

        public Session(String username, long timeoutMillis) {
            this.username = username;
            this.loginTime = System.currentTimeMillis();
            this.timeoutMillis = timeoutMillis;
        }

        /*
         * Checks whether the session is still valid.
         */
        public boolean isExpired() {
            long now = System.currentTimeMillis();
            return now - loginTime > timeoutMillis;
        }

        public String toString() {
            return "Session active for user: " + username;
        }
    }
}
