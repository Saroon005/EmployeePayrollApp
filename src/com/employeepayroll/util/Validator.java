package com.employeepayroll.util;

import java.util.regex.Pattern;

import com.employeepayroll.exceptions.ValidationException;

/*
 * ---------------- Validator Class ----------------
 *
 * This class is responsible ONLY for checking input correctness.
 *
 * Why we separate validation:
 * - Keeps main() clean and readable
 * - Avoids repeating validation logic
 *
 * Important idea:
 * - Validation logic does NOT belong to Employee
 * - Validation happens BEFORE objects are created
 */
public class Validator {

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );

    private static final Pattern PHONE_PATTERN = Pattern.compile(
            "^[6-9]\\d{9}$"
    );

    private static final Pattern EMP_ID_PATTERN = Pattern.compile(
            "^EMP-\\d{4}$"
    );

    /*
     * Checks whether an email follows a valid format.
     *
     * If the format is wrong:
     * - A ValidationException is thrown
     * - Program flow jumps to the catch block in main()
     */
    public static void validateEmail(String email) throws ValidationException {
        if (email == null || email.isBlank()) {
            throw new ValidationException("Email cannot be empty");
        }
        if (!EMAIL_PATTERN.matcher(email.trim()).matches()) {
            throw new ValidationException("Invalid email format");
        }
    }

    /*
     * Validates Indian phone numbers.
     *
     * Rule:
     * - Must start with 6, 7, 8, or 9
     * - Must be exactly 10 digits
     */
    public static void validatePhone(String phone) throws ValidationException {
        if (phone == null || phone.isBlank()) {
            throw new ValidationException("Phone number cannot be empty");
        }
        if (!PHONE_PATTERN.matcher(phone.trim()).matches()) {
            throw new ValidationException("Invalid phone number format");
        }
    }

    /*
     * Validates Employee ID format.
     *
     * Rule:
     * - Must follow EMP-XXXX where X is a digit
     */
    public static void validateEmpId(String empId) throws ValidationException {
        if (empId == null || empId.isBlank()) {
            throw new ValidationException("Employee ID cannot be empty");
        }
        if (!EMP_ID_PATTERN.matcher(empId.trim()).matches()) {
            throw new ValidationException("Invalid Employee ID format. Expected EMP-XXXX");
        }
    }
}
