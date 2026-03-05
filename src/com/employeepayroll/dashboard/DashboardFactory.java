package com.employeepayroll.dashboard;

// ================= Dashboard Factory =================

/*
 * DashboardFactory is responsible for creating dashboards.
 *
 * Key idea introduced:
 * - Factory pattern
 *
 * Why this is needed:
 * - Object creation logic is centralized
 * - main() does not need to know concrete classes
 */
public class DashboardFactory {

    // ---- Abstract Factory style ----
    public interface Factory {
        Dashboard create();
    }

    private static class EmployeeDashboardFactory implements Factory {
        public Dashboard create() {
            return new EmployeeDashboard();
        }
    }

    private static class ManagerDashboardFactory implements Factory {
        public Dashboard create() {
            return new ManagerDashboard();
        }
    }

    public static Factory getFactory(String role) {

        if (role == null) {
            return null;
        }

        if ("EMPLOYEE".equalsIgnoreCase(role)) {
            return new EmployeeDashboardFactory();
        }
        else if ("MANAGER".equalsIgnoreCase(role)) {
            return new ManagerDashboardFactory();
        }

        return null;
    }
}
