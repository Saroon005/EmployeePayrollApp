package com.employeepayroll.app;

/*
 * ==========================================================
 * USE CASE 1: EMPLOYEE REGISTRATION
 * ==========================================================
 *
 * Goal of this Use Case:
 * - Understand how multiple classes work together
 * - Learn how objects are created and used
 * - See how a real-world problem is broken into small parts
 *
 * At this stage, focus on:
 * - What each class represents
 * - How main() coordinates the flow
 * @author Developer
 * @version 1.0
 */

import java.io.IOException;
import java.util.Scanner;

import com.employeepayroll.exceptions.ValidationException;
import com.employeepayroll.model.Employee;
import com.employeepayroll.model.UserAccount;
import com.employeepayroll.util.Validator;

/*
 * ---------------- Main Class ----------------
 *
 * Entry point of Use Case 1.
 *
 * Execution Flow:
 * 1. Take input from user
 * 2. Validate input
 * 3. Create objects
 * 4. Persist data
 * 5. Display confirmation
 */
public class EmployeeRegistrationApp {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        System.out.println("=== USE CASE 1: EMPLOYEE REGISTRATION ===\n");

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
        }
        catch (ValidationException e) {
            System.out.println("\nValidation Failed: " + e.getMessage());
        }
        catch (IOException e) {
            System.out.println("\nError saving employee data!");
        }
        finally {
            sc.close();
        }
    }
}
