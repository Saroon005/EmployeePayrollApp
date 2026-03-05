package com.employeepayroll.util;

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

    // UC6 introduced ValidationService as the centralized validation boundary.
    // Validator remains as a small wrapper to avoid breaking earlier UC code.

    /*
     * Checks whether an email follows a valid format.
     *
     * If the format is wrong:
     * - A ValidationException is thrown
     * - Program flow jumps to the catch block in main()
     */
    public static void validateEmail(String email) throws ValidationException {
        ValidationService.validateEmail(email);
    }

    /*
     * Validates Indian phone numbers.
     *
     * Rule:
     * - Must start with 6, 7, 8, or 9
     * - Must be exactly 10 digits
     */
    public static void validatePhone(String phone) throws ValidationException {
        ValidationService.validatePhone(phone);
    }

    /*
     * Validates Employee ID format.
     *
     * Rule:
     * - Must follow EMP-XXXX where X is a digit
     */
    public static void validateEmpId(String empId) throws ValidationException {
        ValidationService.validateEmployeeId(empId);
    }

    /*
     * UC6 addition: password strength rules.
     */
    public static void validatePassword(String password) throws ValidationException {
        ValidationService.validatePassword(password);
    }

    /*
     * UC6 naming alignment: validateEmployeeId().
     */
    public static void validateEmployeeId(String empId) throws ValidationException {
        ValidationService.validateEmployeeId(empId);
    }
}
