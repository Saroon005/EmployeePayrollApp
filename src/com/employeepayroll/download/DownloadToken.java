package com.employeepayroll.download;

// ================= Download Token =================

/*
 * DownloadToken represents time-bound access to a download.
 *
 * Purpose:
 * - Prevent unlimited access to generated files
 * - Simulate real-world expiry logic
 *
 * This introduces the idea of time-based validation.
 */
public class DownloadToken {

    private long createdTime;
    private long expiryMillis;

    public DownloadToken() {
        createdTime = System.currentTimeMillis();
        expiryMillis = 60 * 1000; // 1 minute validity
    }

    /*
     * Checks whether the token is still valid.
     */
    public boolean isExpired() {

        long now = System.currentTimeMillis();
        return (now - createdTime) > expiryMillis;
    }
}
