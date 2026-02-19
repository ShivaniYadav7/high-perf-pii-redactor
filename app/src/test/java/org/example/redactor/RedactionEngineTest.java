package org.example.redactor;

import org.example.redactor.engine.RedactionEngine;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class RedactionEngineTest {
    private String redact(String input) {
        return RedactionEngine.redactToken(new StringBuilder(input));
    }

    // Email tests
    @Test 
    public void testValidEmails() {
        assertEquals("[EMAIL]", redact("user@example.com")); 
        assertEquals("[EMAIL]", redact("admin.name@sub.domain.co.uk")); 
        assertEquals("[EMAIL]", redact("user+tag@gmail.com"));
    }

    @Test
    public void testInvalidEmails() {
        assertEquals("user@", redact("user@")); // Missing domain
        assertEquals("user..name@gmail.com", redact("user..name@gmail.com")); //Double dot 
    }

    // Phone tests
    @Test
    public void testValidPhones() {
        assertEquals("[PHONE]", redact("9876543210"));
        assertEquals("[PHONE]", redact("555-123-4567")); 
    }

    // PAN tests
    @Test
    public void testPanCards() {
        assertEquals("[PAN]", redact("ABCDE1234F")); // Valid
        assertEquals("abcde1234f", redact("abcde1234f")); // Invalid lowercase
        assertEquals("ABCDE12345", redact("ABCDE12345")); // Invalid end char
    }

    // Punctuation Edge Cases
    @Test
    public void testStickyPunctuation() {
        assertEquals("<[EMAIL]>", redact("<user@test.com>")); 
        assertEquals("([PHONE])", redact("(9876543210)"));
        assertEquals("[EMAIL].", redact("user@test.com.")); // Sentence ending
    }
}