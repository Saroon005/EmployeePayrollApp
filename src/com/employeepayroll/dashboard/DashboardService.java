package com.employeepayroll.dashboard;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.employeepayroll.model.Employee;

/*
 * ==========================================================
 * USE CASE 5: DASHBOARD DISPLAY
 * ==========================================================
 *
 * Goal of this Use Case:
 * - Display different dashboards based on user role
 * - Introduce interfaces and runtime behavior selection
 * - Show how the same data can be presented differently
 *
 * New ideas introduced in UC5:
 * - Interface
 * - Multiple implementations
 * - Factory for object creation
 *
 * This use case builds on:
 * - UC3: Payslip data
 * - UC4: Read-only views
 */
public class DashboardService {

    private static final Pattern EMP_ID_PATTERN = Pattern.compile("Employee ID\\s*:\\s*(.+)");
    private static final Pattern MONTH_PATTERN = Pattern.compile("Month\\s*:\\s*(.+)");
    private static final Pattern NET_PATTERN = Pattern.compile("Net Pay\\s*:\\s*([0-9]+(?:\\.[0-9]+)?)");

    public void displayDashboard(Employee employee, String role) {

        ArrayList<PayslipSummary> summaries;

        // Real-time refresh: always read from file when the user opens dashboard.
        if ("MANAGER".equalsIgnoreCase(role)) {
            summaries = loadAllPayslips();
        } else {
            summaries = loadPayslipsForEmployee(employee.getEmpId());
        }

        DashboardFactory.Factory factory = DashboardFactory.getFactory(role);
        if (factory == null) {
            System.out.println("Invalid role. Please use EMPLOYEE or MANAGER.");
            return;
        }

        Dashboard dashboard = factory.create();

        // getClass() runtime type checking (simple demo)
        if (dashboard.getClass() == EmployeeDashboard.class) {
            // no-op, just showing runtime decision.
        }

        dashboard.display(summaries, employee);
    }

    private ArrayList<PayslipSummary> loadPayslipsForEmployee(String empId) {

        ArrayList<PayslipSummary> result = new ArrayList<>();
        Path filePath = Path.of("payslip_history.txt");

        if (!Files.exists(filePath)) {
            return result;
        }

        List<String> lines;
        try {
            lines = Files.readAllLines(filePath, StandardCharsets.UTF_8);
        } catch (IOException e) {
            return result;
        }

        for (PayslipBlock block : parsePayslipBlocks(lines)) {
            if (empId.equals(block.empId) && block.month != null) {
                result.add(new PayslipSummary(block.month, block.netPay));
            }
        }

        return result;
    }

    private ArrayList<PayslipSummary> loadAllPayslips() {

        ArrayList<PayslipSummary> result = new ArrayList<>();
        Path filePath = Path.of("payslip_history.txt");

        if (!Files.exists(filePath)) {
            return result;
        }

        List<String> lines;
        try {
            lines = Files.readAllLines(filePath, StandardCharsets.UTF_8);
        } catch (IOException e) {
            return result;
        }

        parsePayslipBlocks(lines).stream()
                .filter(b -> b.month != null)
                .map(b -> new PayslipSummary(b.month, b.netPay))
                .forEach(result::add);

        return result;
    }

    private List<PayslipBlock> parsePayslipBlocks(List<String> lines) {

        List<PayslipBlock> blocks = new ArrayList<>();

        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            if (line == null || !line.contains("========== PAYSLIP ==========")) {
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

            String blockText = String.join(System.lineSeparator(), lines.subList(start, end + 1));
            PayslipBlock block = parseSingleBlock(blockText);
            if (block != null) {
                blocks.add(block);
            }

            i = end;
        }

        return blocks;
    }

    private PayslipBlock parseSingleBlock(String block) {

        String empId = findFirst(block, EMP_ID_PATTERN);
        String month = findFirst(block, MONTH_PATTERN);
        String netStr = findFirst(block, NET_PATTERN);

        if (empId == null || month == null || netStr == null) {
            return null;
        }

        try {
            double netPay = Double.parseDouble(netStr);
            return new PayslipBlock(empId.trim(), month.trim(), netPay);
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

    private static class PayslipBlock {

        private final String empId;
        private final String month;
        private final double netPay;

        private PayslipBlock(String empId, String month, double netPay) {
            this.empId = empId;
            this.month = month;
            this.netPay = netPay;
        }
    }
}
