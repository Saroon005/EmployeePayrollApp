package com.employeepayroll.payroll;

// ================= Salary Components (Composition) =================

/*
 * SalaryComponents groups all salary-related values.
 *
 * Why this class exists:
 * - Salary details belong together
 * - Keeps Payslip clean and readable
 *
 * This introduces COMPOSITION:
 * - Payslip owns SalaryComponents
 * - SalaryComponents has no meaning without Payslip
 */
public class SalaryComponents {

    public final double basicSalary;
    public final double hra;
    public final double da;
    public final double allowances;

    public double pf;
    public double tax;
    public double netPay;

    /*
     * Constructor initializes only earnings.
     *
     * Deductions and net pay are calculated later.
     */
    public SalaryComponents(double basicSalary, double hra, double da, double allowances) {
        this.basicSalary = basicSalary;
        this.hra = hra;
        this.da = da;
        this.allowances = allowances;
    }

    // Fluent interface: lets us chain deductions in a readable way.
    public SalaryComponents withPf(double pf) {
        this.pf = pf;
        return this;
    }

    public SalaryComponents withTax(double tax) {
        this.tax = tax;
        return this;
    }

    public SalaryComponents withNetPay(double netPay) {
        this.netPay = netPay;
        return this;
    }
}
