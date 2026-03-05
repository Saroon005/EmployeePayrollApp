package com.employeepayroll.dashboard;

import java.util.ArrayList;

import com.employeepayroll.model.Employee;

// ================= Dashboard Interface =================

/*
 * Dashboard defines a common contract for all dashboards.
 *
 * Key idea introduced:
 * - Interface
 *
 * Why an interface:
 * - Different dashboards exist
 * - All dashboards must provide a display() method
 * - Caller should not depend on concrete implementations
 */
public interface Dashboard {

    void display(ArrayList<PayslipSummary> payslips, Employee employee);

}
