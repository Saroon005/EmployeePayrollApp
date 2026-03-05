package com.employeepayroll.app;

/*
 * ==========================================================
 * EMPLOYEE PAYROLL APP (UC1 + UC2 + UC3 IN ONE RUNNER)
 * ==========================================================
 *
 * This single console runner includes:
 * - UC1: Employee Registration
 * - UC2: Employee Authentication (file-based)
 * - UC3: Monthly Payslip Generation
 * - UC4: Payslip Print / Download
 * - UC5: Dashboard Display (role-based)
 * - UC6: Input Validation (exception hierarchy + fail-fast)
 *
 * @author Developer
 * @version 6.0
 */


import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.employeepayroll.exceptions.ValidationException;
import com.employeepayroll.download.DownloadToken;
import com.employeepayroll.download.FileService;
import com.employeepayroll.download.ImmutablePayslip;
import com.employeepayroll.model.Employee;
import com.employeepayroll.model.UserAccount;
import com.employeepayroll.payroll.Payslip;
import com.employeepayroll.payroll.PayrollService;
import com.employeepayroll.util.Validator;
import com.employeepayroll.dashboard.DashboardService;

public class EmployeePayrollApp {

    private static final int MAX_LOGIN_ATTEMPTS = 3;

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        System.out.println("=== EMPLOYEE PAYROLL APP ===\n");
        System.out.println("1. Register");
        System.out.println("2. Login");
        System.out.println("3. Generate Payslip");
        System.out.println("4. Print / Download Payslip");
        System.out.println("5. Dashboard");
        System.out.println("6. UC6 Input Validation\n");

        System.out.print("Enter choice: ");
        String choice = sc.nextLine().trim();

        switch (choice) {
            case "1" -> register(sc);
            case "2" -> login(sc);
            case "3" -> generatePayslip(sc);
            case "4" -> downloadPayslip(sc);
            case "5" -> dashboard(sc);
            case "6" -> useCase6InputValidation(sc);
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
            empId = empId.trim().toUpperCase();

            System.out.print("Enter Name: ");
            String name = sc.nextLine();

            System.out.print("Enter Email: ");
            String email = sc.nextLine();
            Validator.validateEmail(email);

            System.out.print("Enter Phone (10 digits starting 6-9): ");
            String phone = sc.nextLine();
            Validator.validatePhone(phone);
            phone = phone.trim().replace(" ", "").replace("-", "");

            System.out.print("\nCreate Username: ");
            String username = sc.nextLine().trim();

            System.out.print("\nCreate Password: ");
            String password = sc.nextLine();
            Validator.validatePassword(password);
            password = password.trim();

            // Create objects (Composition: Employee HAS a UserAccount)
            UserAccount account = new UserAccount(username, password);
            Employee employee = new Employee(empId, name.trim(), email.trim(), phone, account);

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

    private static void useCase6InputValidation(Scanner sc) {

        /*
         * ==========================================================
         * USE CASE 6: INPUT VALIDATION
         * ==========================================================
         *
         * Goal of this Use Case:
         * - Validate user input before it enters the system
         * - Centralize validation logic
         * - Learn how exceptions are used to handle invalid data
         *
         * New ideas introduced in UC6:
         * - Exception hierarchy
         * - Custom checked exceptions
         * - Fail-fast validation
         *
         * This use case brings together lessons from:
         * - UC1: Input handling
         * - UC2: Controlled program flow
         * - UC3-UC5: Clean separation of responsibilities
         */

        System.out.println("=== USE CASE 6: INPUT VALIDATION ===\n");

        try {
            System.out.print("Enter Employee ID (EMP-XXXX): ");
            String empId = sc.nextLine();
            Validator.validateEmployeeId(empId);

            System.out.print("Enter Email: ");
            String email = sc.nextLine();
            Validator.validateEmail(email);

            System.out.print("Enter Phone Number: ");
            String phone = sc.nextLine();
            Validator.validatePhone(phone);

            System.out.print("Create Password: ");
            String password = sc.nextLine();
            Validator.validatePassword(password);

            System.out.println("\nAll inputs are VALID. Registration/Login can proceed.");
        }
        catch (ValidationException ex) {
            // Single catch block handles all validation failures
            System.out.println("\nValidation Failed:");
            System.out.println(ex.getMessage());
        }
    }

    private static void login(Scanner sc) {

        System.out.println("\n=== USE CASE 2: EMPLOYEE AUTHENTICATION & LOGIN ===\n");

        Path filePath = Path.of("employee_data.txt");
        if (!Files.exists(filePath)) {
            System.out.println("No employee data found. Please register first.");
            return;
        }

        Employee employee = authenticateEmployee(sc);
        if (employee == null) {
            return;
        }

        System.out.println("\nLogin Successful!");
        System.out.println("Role: EMPLOYEE\n");

        System.out.println("====== DASHBOARD ======");
        System.out.println("Employee Dashboard");
        System.out.println("View Payslip | Update Profile\n");

        Session session = new Session(employee.getAccount().getUsername());
        System.out.println(session);

        if (session.isExpired()) {
            System.out.println("Session expired.");
        } else {
            System.out.println("Session active and valid.");
        }
    }

    private static void generatePayslip(Scanner sc) {

        /*
         * ==========================================================
         * USE CASE 3: PAYSLIP GENERATION
         * ==========================================================
         *
         * Goal of this Use Case:
         * - Understand how multiple objects collaborate
         * - Learn HAS-A relationships between classes
         * - Separate calculation logic from data representation
         *
         * New ideas introduced in UC3:
         * - Aggregation
         * - Composition
         * - Service class for business logic
         */

        System.out.println("\n=== USE CASE 3: PAYSLIP GENERATION ===\n");

        Employee employee = authenticateEmployee(sc);
        if (employee == null) {
            return;
        }

        Payslip payslip = generatePayslipForEmployee(sc, employee, null);
        if (payslip == null) {
            return;
        }

        System.out.println(payslip);
    }

    private static Payslip generatePayslipForEmployee(Scanner sc, Employee employee, String month) {

        String monthValue = month;
        if (monthValue == null || monthValue.isBlank()) {
            System.out.print("Enter Month (e.g., January 2026): ");
            monthValue = sc.nextLine().trim();
        }

        System.out.println("\nEnter salary amounts (enter 0 if not applicable)");
        double basic = readDouble(sc, "Enter Basic Salary: ");
        double hra = readDouble(sc, "Enter HRA: ");
        double da = readDouble(sc, "Enter DA: ");
        double allowances = readDouble(sc, "Enter Allowances: ");

        PayrollService payroll = new PayrollService();
        Payslip payslip = payroll.generatePayslip(employee, monthValue, basic, hra, da, allowances);

        try {
            payroll.persist(payslip);
        } catch (IOException e) {
            System.out.println("Error saving payslip history!");
        }

        return payslip;
    }

    private static void downloadPayslip(Scanner sc) {

        /*
         * ==========================================================
         * USE CASE 4: PAYSLIP PRINT / DOWNLOAD
         * ==========================================================
         *
         * Goal of this Use Case:
         * - Protect existing data from accidental modification
         * - Learn how objects can be safely copied
         * - Understand how object equality works
         *
         * New ideas introduced in UC4:
         * - Immutability
         * - Cloning objects
         * - equals() and hashCode()
         * - Simple file persistence
         */

        System.out.println("\n=== USE CASE 4: PAYSLIP PRINT / DOWNLOAD ===\n");

        Employee employee = authenticateEmployee(sc);
        if (employee == null) {
            return;
        }

        System.out.print("Enter Month (e.g., January 2026): ");
        String month = sc.nextLine().trim();

        // UC4 should not ask salary inputs.
        // It should use an existing saved payslip (UC3 history).
        PayslipSnapshot snapshot = findPayslipSnapshot(employee.getEmpId(), month);

        ImmutablePayslip original;
        if (snapshot != null) {
            original = new ImmutablePayslip(snapshot.empId, snapshot.empName, snapshot.month, snapshot.netPay);
        } else {
            System.out.println("Payslip not available for the selected month.");
            System.out.println("Redirecting to UC3 to generate it now...\n");

            Payslip generated = generatePayslipForEmployee(sc, employee, month);
            if (generated == null) {
                return;
            }

            System.out.println(generated);

            original = new ImmutablePayslip(
                    employee.getEmpId(),
                    employee.getName(),
                    month,
                    generated.getComponents().netPay
            );
        }

        System.out.println("Original Payslip:\n");
        System.out.println(original);

        try {
            // Clone payslip for download
            ImmutablePayslip cloned = original.clone();

            // Verify equality and identity
            if (original.equals(cloned)) {
                System.out.println("Verified: Download copy is equal to original.");
            }
            System.out.println("Original hashcode : " + original.hashCode());
            System.out.println("Cloned hashcode   : " + cloned.hashCode());

            // Check download expiry
            DownloadToken token = new DownloadToken();
            if (token.isExpired()) {
                System.out.println("Download token expired. Please try again.");
                return;
            }

            // Save payslip to files
            FileService fileService = new FileService();
            String textFile = fileService.savePayslipAsText(cloned);
            String pdfFile = fileService.savePayslipAsPdf(cloned);

            System.out.println("\nPayslip Download Successful.\n");
            System.out.println("Saved as text file: " + textFile);
            System.out.println("Saved as PDF file : " + pdfFile);

            System.out.println("\n--- Printed Payslip ---\n");
            System.out.println(cloned);
        } catch (Exception e) {
            System.out.println("Error during payslip download.");
        }
    }

    private static void dashboard(Scanner sc) {

        System.out.println("\n=== USE CASE 5: DASHBOARD DISPLAY ===\n");

        Employee employee = authenticateEmployee(sc);
        if (employee == null) {
            return;
        }

        System.out.print("Enter role (EMPLOYEE / MANAGER): ");
        String role = sc.nextLine().trim();

        DashboardService service = new DashboardService();
        service.displayDashboard(employee, role);
    }

    private static PayslipSnapshot findPayslipSnapshot(String empId, String month) {

        Path filePath = Path.of("payslip_history.txt");
        if (!Files.exists(filePath)) {
            return null;
        }

        try {
            List<String> lines = Files.readAllLines(filePath, StandardCharsets.UTF_8);
            PayslipSnapshot lastMatch = null;

            for (int i = 0; i < lines.size(); i++) {
                String line = lines.get(i);
                if (line == null) {
                    continue;
                }

                if (!line.contains("========== PAYSLIP ==========")) {
                    continue;
                }

                int start = i;
                int end = -1;
                for (int j = start; j < lines.size(); j++) {
                    String endLine = lines.get(j);
                    if (endLine != null && endLine.contains("============================")) {
                        end = j;
                        break;
                    }
                }

                if (end == -1) {
                    break;
                }

                String block = String.join(System.lineSeparator(), lines.subList(start, end + 1));
                PayslipSnapshot snapshot = parsePayslipBlock(block);

                if (snapshot != null
                        && empId.equals(snapshot.empId)
                        && month.equals(snapshot.month)) {
                    lastMatch = snapshot;
                }

                i = end;
            }

            return lastMatch;
        } catch (IOException e) {
            return null;
        }
    }

    private static PayslipSnapshot parsePayslipBlock(String block) {
        if (block == null || block.isBlank()) {
            return null;
        }

        Pattern empIdPattern = Pattern.compile("Employee ID\\s*:\\s*(.+)");
        Pattern namePattern = Pattern.compile("Employee Name\\s*:\\s*(.+)");
        Pattern monthPattern = Pattern.compile("Month\\s*:\\s*(.+)");
        Pattern netPattern = Pattern.compile("Net Pay\\s*:\\s*([0-9]+(?:\\.[0-9]+)?)");

        String empId = findFirst(block, empIdPattern);
        String empName = findFirst(block, namePattern);
        String month = findFirst(block, monthPattern);
        String netStr = findFirst(block, netPattern);

        if (empId == null || empName == null || month == null || netStr == null) {
            return null;
        }

        try {
            double netPay = Double.parseDouble(netStr);
            return new PayslipSnapshot(empId.trim(), empName.trim(), month.trim(), netPay);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private static String findFirst(String input, Pattern pattern) {
        Matcher matcher = pattern.matcher(input);
        if (!matcher.find()) {
            return null;
        }
        return matcher.group(1);
    }

    private static class PayslipSnapshot {

        private final String empId;
        private final String empName;
        private final String month;
        private final double netPay;

        private PayslipSnapshot(String empId, String empName, String month, double netPay) {
            this.empId = empId;
            this.empName = empName;
            this.month = month;
            this.netPay = netPay;
        }
    }

    private static double readDouble(Scanner sc, String prompt) {
        while (true) {
            System.out.print(prompt);
            String raw = sc.nextLine().trim();
            try {
                return Double.parseDouble(raw);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }

    private static Employee authenticateEmployee(Scanner sc) {

        Path filePath = Path.of("employee_data.txt");
        if (!Files.exists(filePath)) {
            System.out.println("No employee data found. Please register first.");
            return null;
        }

        for (int attempt = 1; attempt <= MAX_LOGIN_ATTEMPTS; attempt++) {

            System.out.print("Enter Username: ");
            String username = sc.nextLine().trim();

            System.out.print("Enter Password: ");
            String password = sc.nextLine();

            Employee employee = loadEmployeeFromFile(username);
            if (employee != null && employee.getAccount().authenticate(password)) {
                return employee;
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

        return null;
    }

    private static Employee loadEmployeeFromFile(String username) {
        try {
            List<String> lines = Files.readAllLines(Path.of("employee_data.txt"), StandardCharsets.UTF_8);

            String internalId = null;
            String empId = null;
            String name = null;
            String email = null;
            String phone = null;
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
                    internalId = parts[1];
                    empId = parts[2];
                    name = parts[3];
                    email = parts[4];
                    phone = parts[5];
                    foundHash = parts[7];
                    foundSalt = parts[8];
                }
            }

            if (internalId == null || empId == null || name == null || foundHash == null || foundSalt == null) {
                return null;
            }

            UserAccount account = new UserAccount(username, foundHash, foundSalt, true);
            return new Employee(internalId, empId, name, email, phone, account);
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
