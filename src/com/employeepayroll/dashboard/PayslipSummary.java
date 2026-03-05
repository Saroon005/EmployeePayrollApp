package com.employeepayroll.dashboard;

// ================= Payslip Class =================

/*
 * Payslip represents a simplified salary record.
 *
 * This version contains only:
 * - Month
 * - Net Pay
 *
 * Why simplified:
 * - Dashboard does not need full payslip details
 * - Keeps focus on display logic
 */
public class PayslipSummary {

    private final String month;
    private final double netPay;

    public PayslipSummary(String month, double netPay) {
        this.month = month;
        this.netPay = netPay;
    }

    public String getMonth() {
        return month;
    }

    public double getNetPay() {
        return netPay;
    }

    @Override
    public String toString() {
        return month + " : " + netPay;
    }
}
