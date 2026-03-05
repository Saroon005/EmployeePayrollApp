package com.employeepayroll.exceptions;

// ================= Child Exception =================

/*
 * PasswordValidationException represents a password-strength validation failure.
 */
public class PasswordValidationException extends ValidationException {

    public PasswordValidationException(String message) {
        super(message);
    }
}
