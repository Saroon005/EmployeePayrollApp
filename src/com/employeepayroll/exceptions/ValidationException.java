package com.employeepayroll.exceptions;

/*
 * ================= Base Exception =================
 *
 * ValidationException is the base class for all validation errors.
 *
 * Why this class exists:
 * - ALL validation-related problems belong to one category
 * - Allows a single catch block to handle all validation failures
 *
 * This introduces the idea of an exception hierarchy.
 */
public class ValidationException extends Exception {
    public ValidationException(String message) {
        super(message);
    }
}
