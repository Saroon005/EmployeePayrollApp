package com.employeepayroll.payroll;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.Instant;
import java.util.stream.Stream;

import com.employeepayroll.model.Employee;

// ================= Payroll Service =================

/*
 * PayrollService contains salary calculation logic.
 *
 * Why this class exists:
 * - Business rules should not be inside main()
 * - Keeps calculations reusable and isolated
 *
 * This introduces the idea of a SERVICE class.
 */
public class PayrollService {

    /*
     * Generates a payslip by:
     * - Creating salary components
     * - Applying calculation rules
     * - Returning a completed Payslip object
     */
    public Payslip generatePayslip(Employee employee,
                                   String month,
                                   double basic,
                                   double hra,
                                   double da,
                                   double allowances) {

        SalaryComponents sc = new SalaryComponents(basic, hra, da, allowances);

        // ----- Gross Salary Calculation -----
        double gross = Stream.of(basic, hra, da, allowances)
                .mapToDouble(Double::doubleValue)
                .sum();

        // ----- Deductions -----
        double pf = basic * 0.12;   // Provident Fund (12%)
        double tax = gross * 0.10;  // Income Tax (10%) – demo rule

        // ----- Net Pay -----
        double net = gross - (pf + tax);

        sc.withPf(pf)
          .withTax(tax)
          .withNetPay(net);

        return new Payslip(employee, sc, month);
    }

    /*
     * Saves payslip as a text entry.
     *
     * Purpose:
     * - Keeps a simple history trail
     * - Makes it easy to view past payslips
     */
    public void persist(Payslip payslip) throws IOException {
        Path filePath = Path.of("payslip_history.txt");

        String entry = "\n[" + Instant.now() + "]\n" + payslip.toString();

        Files.writeString(
                filePath,
                entry,
                StandardCharsets.UTF_8,
                StandardOpenOption.CREATE,
                StandardOpenOption.APPEND
        );
    }
}
