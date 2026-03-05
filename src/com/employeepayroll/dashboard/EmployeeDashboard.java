package com.employeepayroll.dashboard;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.employeepayroll.model.Employee;

/*
 * EmployeeDashboard provides a personal view of payslip data.
 *
 * Focus:
 * - Recent payslips
 * - Year-to-date earnings
 */
public class EmployeeDashboard implements Dashboard {

    /*
     * Displays employee-specific dashboard.
     *
     * Steps performed:
     * - Sort payslips
     * - Display top entries
     * - Calculate total earnings
     */
    @Override
    public void display(ArrayList<PayslipSummary> payslips, Employee employee) {

        System.out.println("\n=== EMPLOYEE DASHBOARD ===\n");
        System.out.println("Welcome, " + employee.getName() + "\n");

        // Display runtime implementation information
        System.out.println("Dashboard Type: " + this.getClass().getName());

        if (payslips == null || payslips.isEmpty()) {
            System.out.println("No payslip history found for this employee.");
            return;
        }

        // Sort payslips in descending order of net pay
        List<PayslipSummary> sorted = payslips.stream()
                .sorted(Comparator.comparingDouble(PayslipSummary::getNetPay).reversed())
                .toList();

        // Display only top 3 payslips
        System.out.println("\nTop 3 Payslips (by Net Pay):\n");
        sorted.stream()
                .limit(3)
                .forEach(p -> System.out.println(p));

        // Calculate Year-To-Date earnings
        double total = payslips.stream()
                .mapToDouble(PayslipSummary::getNetPay)
                .sum();

        System.out.println("\nYear-To-Date Earnings: " + total);
    }
}
