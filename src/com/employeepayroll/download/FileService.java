package com.employeepayroll.download;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

// ================= File Service =================

/*
 * FileService handles saving payslip data to files.
 *
 * Why this class exists:
 * - File operations should not be inside Payslip
 * - Separates persistence from data representation
 */
public class FileService {

    private final Path outputDir;

    public FileService() {
        this(Path.of("downloads"));
    }

    public FileService(Path outputDir) {
        this.outputDir = outputDir;
    }

    /*
     * Saves payslip as a text file.
     *
     * A unique filename is generated using timestamp
     * to avoid overwriting existing files.
     */
    public String savePayslipAsText(ImmutablePayslip payslip) throws IOException {

        Files.createDirectories(outputDir);

        String fileName = "Payslip_" + payslip.getEmpId() + "_" + System.currentTimeMillis() + ".txt";
        Path filePath = outputDir.resolve(fileName);

        Files.writeString(filePath, payslip.toString(), StandardCharsets.UTF_8);
        return filePath.toString();
    }

    /*
     * Saves payslip as a PDF file.
     *
     * Note:
     * - This is a simplified demo
     * - Content is plain text with .pdf extension
     */
    public String savePayslipAsPdf(ImmutablePayslip payslip) throws IOException {

        Files.createDirectories(outputDir);

        String fileName = "Payslip_" + payslip.getEmpId() + "_" + System.currentTimeMillis() + ".pdf";
        Path filePath = outputDir.resolve(fileName);

        Files.writeString(filePath, payslip.toString(), StandardCharsets.UTF_8);
        return filePath.toString();
    }
}
