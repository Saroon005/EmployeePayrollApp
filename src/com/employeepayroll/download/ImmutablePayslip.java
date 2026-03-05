package com.employeepayroll.download;

// ================= Immutable Payslip =================

/*
 * Payslip represents a finalized salary record.
 *
 * Key idea introduced here:
 * - Immutability
 *
 * Once a payslip is generated:
 * - Its data should never change
 * - Any operation (print / download) must use a copy
 *
 * Making the class final prevents inheritance-based modification.
 */
public final class ImmutablePayslip implements Cloneable {

    private final String empId;
    private final String empName;
    private final String month;
    private final double netPay;

    public ImmutablePayslip(String empId, String empName, String month, double netPay) {
        this.empId = requireNonBlank(empId, "Employee ID");
        this.empName = requireNonBlank(empName, "Employee Name");
        this.month = requireNonBlank(month, "Month");
        this.netPay = netPay;
    }

    public String getEmpId() {
        return empId;
    }

    public String getEmpName() {
        return empName;
    }

    public String getMonth() {
        return month;
    }

    public double getNetPay() {
        return netPay;
    }

    /*
     * Creates a safe copy of the payslip.
     *
     * Why cloning is needed:
     * - Original object should remain untouched
     * - Downloaded or printed version must be independent
     */
    @Override
    public ImmutablePayslip clone() {
        return new ImmutablePayslip(empId, empName, month, netPay);
    }

    /*
     * Defines logical equality between two payslips.
     *
     * Two payslips are considered equal if:
     * - They belong to the same employee
     * - They are for the same month
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ImmutablePayslip other)) {
            return false;
        }
        return empId.equals(other.empId) && month.equals(other.month);
    }

    /*
     * hashCode() is implemented to be consistent with equals().
     *
     * This is important when objects are used in collections.
     */
    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + empId.hashCode();
        result = 31 * result + month.hashCode();
        return result;
    }

    /*
     * Converts payslip data into readable text.
     *
     * Used for:
     * - Console printing
     * - File download
     */
    @Override
    public String toString() {

        return "PAYSLIP\n"
                + "Employee ID   : " + empId + "\n"
                + "Employee Name : " + empName + "\n"
                + "Month         : " + month + "\n"
                + "Net Pay       : " + netPay + "\n";
    }

    private static String requireNonBlank(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " cannot be empty");
        }
        return value.trim();
    }
}
