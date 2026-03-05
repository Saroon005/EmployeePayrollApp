package com.employeepayroll.exceptions;

/*
 * ------------------ Custom Exception ------------------
 *
 * This class represents a validation-related problem.
 *
 * Why this exists:
 * - Instead of stopping the program abruptly,
 *   we clearly communicate what went wrong.
 *
 * For now, think of this as:
 * "A special error we throw when input is invalid"
 */
public class ValidationException extends Exception {
    public ValidationException(String message) {
        super(message);
    }
}
