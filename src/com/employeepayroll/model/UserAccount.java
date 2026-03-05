package com.employeepayroll.model;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/*
 * ---------------- UserAccount Class ----------------
 *
 * This class represents login-related information.
 *
 * Why this is a separate class:
 * - Employee details and login details are different concerns
 * - Keeps responsibilities small and clear
 *
 * This introduces the idea of COMPOSITION:
 * - An Employee HAS a UserAccount
 */
public class UserAccount {

    private final String username;

    // Store encrypted credentials (hash + salt).
    private final String passwordHash;
    private final String salt;

    // Constructor Overloading (create from plain password)
    public UserAccount(String username, String plainPassword) {
        this.username = requireNonBlank(username, "Username");
        String generatedSalt = generateSalt();
        this.salt = generatedSalt;
        this.passwordHash = hashPassword(plainPassword, generatedSalt);
    }

    // Constructor Overloading (create from already-hashed values, e.g., when loading from storage)
    public UserAccount(String username, String passwordHash, String salt, boolean alreadyHashed) {
        this.username = requireNonBlank(username, "Username");
        this.passwordHash = requireNonBlank(passwordHash, "Password hash");
        this.salt = requireNonBlank(salt, "Salt");
    }

    public String getUsername() {
        return username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public String getSalt() {
        return salt;
    }

    private static String requireNonBlank(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " cannot be empty");
        }
        return value.trim();
    }

    private static String generateSalt() {
        byte[] saltBytes = new byte[16];
        new SecureRandom().nextBytes(saltBytes);
        return toHex(saltBytes);
    }

    private static String hashPassword(String plainPassword, String saltHex) {
        if (plainPassword == null || plainPassword.isBlank()) {
            throw new IllegalArgumentException("Password cannot be empty");
        }

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            digest.update(fromHex(saltHex));
            byte[] hashed = digest.digest(plainPassword.getBytes(StandardCharsets.UTF_8));
            return toHex(hashed);
        } catch (NoSuchAlgorithmException e) {
            // SHA-256 should always exist on the JVM.
            throw new IllegalStateException("Missing SHA-256 algorithm", e);
        }
    }

    private static String toHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    private static byte[] fromHex(String hex) {
        int len = hex.length();
        if (len % 2 != 0) {
            throw new IllegalArgumentException("Invalid hex string");
        }
        byte[] out = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            out[i / 2] = (byte) Integer.parseInt(hex.substring(i, i + 2), 16);
        }
        return out;
    }
}
