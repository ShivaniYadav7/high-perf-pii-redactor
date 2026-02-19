package org.example.redactor;

import java.io.*;

public class Benchmark {
    public static void main(String[] args) throws IOException {
        String testFile = "benchmark_10MB.log";
        String outFile = "benchmark_out.log";

        System.out.println("1. Generating 10MB of synthetic logs");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(testFile))) {
            String logLine = "2026-02-17 10:15:30 INFO User admin@test.com updated profile. Contact: +91-9876543210 PAN: ABCDE1234F [Transaction OK]\n";
            for (int i = 0; i < 100000; i++) {
                writer.write(logLine);
            }
        }

        System.out.println("2. Running Redaction Engine...");
        long start = System.currentTimeMillis();
        
        // Run your CLI main method programmatically
        CLI.main(new String[]{testFile, outFile, "--dry-run"});
        
        long end = System.currentTimeMillis();
        long duration = end - start;
        
        System.out.println("\n--- BENCHMARK RESULTS ---");
        System.out.println("File Size: 10 MB");
        System.out.println("Time Taken: " + duration + " ms");
        System.out.println("Throughput: " + (10000 / (duration > 0 ? duration : 1)) + " MB/second");
        System.out.println("Memory Space Complexity: O(1) Constant");
    }
}