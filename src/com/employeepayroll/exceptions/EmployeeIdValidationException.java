package com.employeepayroll.exceptions;

// ================= Child Exception =================

/*
 * EmployeeIdValidationException represents an employee-id format validation failure.
 */
public class EmployeeIdValidationException extends ValidationException {

    public EmployeeIdValidationException(String message) {
        super(message);
    }
}
