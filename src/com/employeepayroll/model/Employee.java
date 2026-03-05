package com.employeepayroll.model;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.Instant;
import java.util.UUID;

/*
 * ---------------- Employee Class ----------------
 *
 * This class represents an Employee entity.
 *
 * Core OOP concept introduced here:
 * - Encapsulation
 *
 * Data is kept private and controlled through the class.
 */
public class Employee {

    private final String internalId; // generated unique identifier

    private String empId;
    private String name;
    private String email;
    private String phone;

    // Employee HAS a UserAccount
    private UserAccount account;

    /*
     * Constructor is used to create a fully initialized Employee object.
     *
     * Important idea:
     * - Object creation happens only after validation succeeds
     */
    public Employee(String empId, String name, String email, String phone, UserAccount account) {
        this(UUID.randomUUID().toString(), empId, name, email, phone, account);
    }

    // Constructor Overloading (allows passing an existing generated unique identifier)
    public Employee(String internalId, String empId, String name, String email, String phone, UserAccount account) {
        this.internalId = requireNonBlank(internalId, "Internal ID");
        this.empId = requireNonBlank(empId, "Employee ID");
        this.name = requireNonBlank(name, "Name");
        this.email = requireNonBlank(email, "Email");
        this.phone = requireNonBlank(phone, "Phone");
        if (account == null) {
            throw new IllegalArgumentException("UserAccount cannot be null");
        }
        this.account = account;
    }

    public String getInternalId() {
        return internalId;
    }

    public String getEmpId() {
        return empId;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public UserAccount getAccount() {
        return account;
    }

    private static String requireNonBlank(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " cannot be empty");
        }
        return value.trim();
    }

    /*
     * Converts Employee data into a readable format.
     *
     * This avoids printing details manually in main().
     */
    @Override
    public String toString() {
        return "Employee ID : " + empId + "\n"
                + "Name        : " + name + "\n"
                + "Email       : " + email + "\n"
                + "Phone       : " + phone + "\n"
                + "Username    : " + account.getUsername();
    }

    /*
     * Saves employee data into a file.
     *
     * Purpose:
     * - Simulates persistence
     * - Shows that objects can manage their own data
     */
    public void persist() throws IOException {
        Path filePath = Path.of("employee_data.txt");

        // Persist a single line per employee to keep it simple.
        // Password is stored encrypted (hash + salt), never plain.
        String record = String.join("|",
                Instant.now().toString(),
                internalId,
                empId,
                name,
                email,
                phone,
                account.getUsername(),
                account.getPasswordHash(),
                account.getSalt()
        ) + System.lineSeparator();

        Files.writeString(
                filePath,
                record,
                StandardCharsets.UTF_8,
                StandardOpenOption.CREATE,
                StandardOpenOption.APPEND
        );
    }
}
