package com.employeepayroll.payroll;

import com.employeepayroll.model.Employee;

// ================= Payslip =================

/*
 * Payslip represents a monthly salary statement.
 *
 * It combines:
 * - Employee details (aggregation)
 * - Salary details (composition)
 *
 * Payslip acts as a READ-ONLY view once created.
 */
public class Payslip {

    private final Employee employee;            // Aggregation
    private final SalaryComponents components;  // Composition
    private final String month;

    public Payslip(Employee employee, SalaryComponents components, String month) {
        this.employee = employee;
        this.components = components;
        this.month = month;
    }

    public Employee getEmployee() {
        return employee;
    }

    public SalaryComponents getComponents() {
        return components;
    }

    public String getMonth() {
        return month;
    }

    /*
     * Formats payslip information into a readable output.
     *
     * This avoids printing logic in main() or service classes.
     */
    @Override
    public String toString() {

        return "\n========== PAYSLIP ==========\n\n"
                + "Month        : " + month + "\n"
                + "Employee ID  : " + employee.getEmpId() + "\n"
                + "Employee Name: " + employee.getName() + "\n\n"

                + "---- Earnings ----\n"
                + "Basic Salary : " + components.basicSalary + "\n"
                + "HRA          : " + components.hra + "\n"
                + "DA           : " + components.da + "\n"
                + "Allowances   : " + components.allowances + "\n\n"

                + "---- Deductions ----\n"
                + "PF           : " + components.pf + "\n"
                + "Tax          : " + components.tax + "\n\n"

                + "Net Pay      : " + components.netPay + "\n"
                + "============================\n";
    }
}
