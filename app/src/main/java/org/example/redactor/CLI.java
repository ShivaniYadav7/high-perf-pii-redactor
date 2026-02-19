package org.example.redactor;

import org.example.redactor.engine.RedactionEngine;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class CLI {

    public static void main(String[] args) {
        // Default to Live Terminal Streaming (Standard In / Standard Out)
        InputStream inStream = System.in;
        OutputStream outStream = System.out;
        boolean dryRun = false;

        try {
            // If the user provided files, switch the streams to read the hard drive instead
            if (args.length >= 2) {
                inStream = new FileInputStream(args[0]);
                outStream = new FileOutputStream(args[1]);
                dryRun = (args.length > 2 && args[2].equals("--dry-run"));
                if (dryRun) System.out.println("MODE: Dry Run (No disk writes)");
            } else {
                System.err.println("--- LIVE STREAM MODE ---");
                System.err.println("Type your text below. Press ENTER to process. (Press Ctrl+C to exit)");
            }

            long startTime = System.currentTimeMillis();

            // The exact same streaming pipe handles BOTH files and live keyboards!
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inStream, StandardCharsets.UTF_8));
                 BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outStream, StandardCharsets.UTF_8))) {

                StringBuilder word = new StringBuilder();
                int c;

                // CORE STREAMING LOOP
                while ((c = reader.read()) != -1) {
                    char character = (char) c;

                    if (isSeparator(character)) {
                        if (word.length() > 0) {
                            String processed = RedactionEngine.redactToken(word);
                            if (!dryRun) writer.write(processed);
                            word.setLength(0);
                        }
                        if (!dryRun) {
                            writer.write(character);
                            // If we hit a newline in live mode, force the text to print to the screen instantly
                            if (character == '\n') writer.flush();
                        }
                    } else {
                        word.append(character);
                    }
                }

                if (word.length() > 0 && !dryRun) {
                    writer.write(RedactionEngine.redactToken(word));
                    writer.flush();
                }
            }

            if (args.length >= 2) {
                long endTime = System.currentTimeMillis();
                System.out.println("Processing Time: " + (endTime - startTime) + "ms");
            }

        } catch (IOException e) {
            System.err.println("Error processing: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static boolean isSeparator(char c) {
        return c == ' ' || c == '\n' || c == '\r' || c == '\t' || c == ',' || c == ';' || c == '!' || c == '?' || c == '[' || c == ']' || c == '<' || c == '>' || c == ':';
    }
}