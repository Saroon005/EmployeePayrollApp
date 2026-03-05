package com.employeepayroll.dashboard;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.employeepayroll.model.Employee;

// ================= Manager Dashboard =================

/*
 * ManagerDashboard provides an aggregate view.
 *
 * Focus:
 * - Overall earnings
 * - Summary-level information
 */
public class ManagerDashboard implements Dashboard {

    /*
     * Displays manager-specific dashboard.
     *
     * Only aggregation logic is applied here.
     */
    @Override
    public void display(ArrayList<PayslipSummary> payslips, Employee employee) {

        System.out.println("\n=== MANAGER DASHBOARD ===\n");
        System.out.println("Manager: " + employee.getName() + "\n");

        System.out.println("Dashboard Type: " + this.getClass().getName());

        if (payslips == null || payslips.isEmpty()) {
            System.out.println("No payslip history found in the system.");
            return;
        }

        List<PayslipSummary> sorted = payslips.stream()
            .sorted(Comparator.comparingDouble(PayslipSummary::getNetPay).reversed())
            .toList();

        System.out.println("\nTop 3 Payslips (by Net Pay):\n");
        sorted.stream()
            .limit(3)
            .forEach(p -> System.out.println(p));

        // Calculate total team earnings
        double total = payslips.stream()
                .mapToDouble(PayslipSummary::getNetPay)
                .sum();

        System.out.println("\nTeam Total YTD Earnings: " + total);
    }
}
