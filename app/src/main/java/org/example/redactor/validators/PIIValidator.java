package org.example.redactor.validators;

public interface PIIValidator {
    boolean isValid(String token);
    String getReplacementTag();
}
