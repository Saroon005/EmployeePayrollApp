package com.employeepayroll.exceptions;

// ================= Child Exception =================

/*
 * PhoneValidationException represents a phone-number validation failure.
 */
public class PhoneValidationException extends ValidationException {

    public PhoneValidationException(String message) {
        super(message);
    }
}
