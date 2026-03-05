package com.employeepayroll.exceptions;

// ================= Child Exception =================

/*
 * EmailValidationException represents an email-specific validation failure.
 *
 * Why separate exception types:
 * - Clear identification of error cause
 * - Type-safe exception handling
 */
public class EmailValidationException extends ValidationException {

    public EmailValidationException(String message) {
        super(message);
    }
}
