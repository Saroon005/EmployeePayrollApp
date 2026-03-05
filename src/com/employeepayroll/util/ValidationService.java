package com.employeepayroll.util;

import java.util.regex.Pattern;

import com.employeepayroll.exceptions.EmailValidationException;
import com.employeepayroll.exceptions.EmployeeIdValidationException;
import com.employeepayroll.exceptions.PasswordValidationException;
import com.employeepayroll.exceptions.PhoneValidationException;

/*
 * ==========================================================
 * USE CASE 6: INPUT VALIDATION
 * ==========================================================
 *
 * Goal of this Use Case:
 * - Validate user input before it enters the system
 * - Centralize validation logic
 * - Learn how exceptions are used to handle invalid data
 *
 * New ideas introduced in UC6:
 * - Exception hierarchy
 * - Custom checked exceptions
 * - Fail-fast validation
 *
 * This class represents a defensive boundary
 * between user input and application logic.
 */
public final class ValidationService {

    private ValidationService() {
        // utility class
    }

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );

    // Keep it simple and consistent with earlier UC1 rules.
    private static final Pattern PHONE_PATTERN = Pattern.compile(
            "^[6-9]\\d{9}$"
    );

    private static final Pattern EMP_ID_PATTERN = Pattern.compile(
            "^EMP-\\d{4}$"
    );

    private static final Pattern HAS_UPPER = Pattern.compile(".*[A-Z].*");
    private static final Pattern HAS_LOWER = Pattern.compile(".*[a-z].*");
    private static final Pattern HAS_DIGIT = Pattern.compile(".*\\d.*");
    private static final Pattern HAS_SPECIAL = Pattern.compile(".*[^A-Za-z0-9].*");
    private static final Pattern HAS_SPACE = Pattern.compile(".*\\s+.*");

    /*
     * Sanitizes input before validation.
     *
     * Purpose:
     * - Remove accidental spaces
     * - Ensure consistent validation behavior
     */
    private static String sanitize(String input) {
        if (input == null) {
            return "";
        }
        return input.trim();
    }

    /*
     * Validates email format.
     */
    public static void validateEmail(String email) throws EmailValidationException {

        String sanitized = sanitize(email);
        if (sanitized.isBlank()) {
            throw new EmailValidationException("Email cannot be empty");
        }

        if (!EMAIL_PATTERN.matcher(sanitized).matches()) {
            throw new EmailValidationException("Invalid email format (example: name@example.com)");
        }
    }

    /*
     * Validates phone number.
     */
    public static void validatePhone(String phone) throws PhoneValidationException {

        String sanitized = sanitize(phone);

        // Basic sanitization: remove spaces and hyphens often typed by users.
        sanitized = sanitized.replace(" ", "").replace("-", "");

        if (sanitized.isBlank()) {
            throw new PhoneValidationException("Phone number cannot be empty");
        }

        if (!PHONE_PATTERN.matcher(sanitized).matches()) {
            throw new PhoneValidationException("Invalid phone number (must be 10 digits starting 6-9)");
        }
    }

    /*
     * Validates password strength.
     */
    public static void validatePassword(String password) throws PasswordValidationException {

        // Do not aggressively sanitize passwords (to avoid changing user intent),
        // but trimming common accidental outer spaces is still helpful.
        String sanitized = sanitize(password);

        if (sanitized.isBlank()) {
            throw new PasswordValidationException("Password cannot be empty");
        }

        if (sanitized.length() < 8) {
            throw new PasswordValidationException("Password must be at least 8 characters long");
        }

        if (HAS_SPACE.matcher(sanitized).matches()) {
            throw new PasswordValidationException("Password must not contain spaces");
        }

        if (!HAS_UPPER.matcher(sanitized).matches()) {
            throw new PasswordValidationException("Password must contain at least 1 uppercase letter");
        }

        if (!HAS_LOWER.matcher(sanitized).matches()) {
            throw new PasswordValidationException("Password must contain at least 1 lowercase letter");
        }

        if (!HAS_DIGIT.matcher(sanitized).matches()) {
            throw new PasswordValidationException("Password must contain at least 1 number");
        }

        if (!HAS_SPECIAL.matcher(sanitized).matches()) {
            throw new PasswordValidationException("Password must contain at least 1 special character");
        }
    }

    /*
     * Validates employee ID format.
     */
    public static void validateEmployeeId(String empId) throws EmployeeIdValidationException {

        String sanitized = sanitize(empId).toUpperCase();
        if (sanitized.isBlank()) {
            throw new EmployeeIdValidationException("Employee ID cannot be empty");
        }

        if (!EMP_ID_PATTERN.matcher(sanitized).matches()) {
            throw new EmployeeIdValidationException("Invalid Employee ID format. Expected EMP-XXXX");
        }
    }
}
