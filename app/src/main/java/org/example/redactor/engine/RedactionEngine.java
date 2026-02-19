package org.example.redactor.engine;

import org.example.redactor.validators.*;
import java.util.List;
import java.util.Arrays;

public class RedactionEngine {
    private static final List<PIIValidator> validators = Arrays.asList(
        new EmailValidator(),
        new PhoneValidator(),
        new PanValidator()
    );

    public static String redactToken(StringBuilder token) {
        String s = token.toString();
        String suffix = "";
        String prefix = "";

        while(s.length() > 0 && (s.startsWith("(") || s.startsWith("<") || s.startsWith("["))) {
            prefix += s.charAt(0);
            s = s.substring(1);
        }

        while (s.length() > 0 && (s.endsWith(".") || s.endsWith(")") || s.endsWith(">") || s.endsWith("]"))) {
            suffix = s.charAt(s.length() - 1) + suffix;
            s = s.substring(0, s.length() - 1);
        }

        if(s.isEmpty()) return prefix + suffix;

        for(PIIValidator validator : validators) {
            if(validator.isValid(s)) {
                return prefix + validator.getReplacementTag() + suffix;
            }
        }

        return token.toString();
    }

    // NEW: A user-friendly API for developers to pass whole sentences!
    public static String redactLine(String line) {
        StringBuilder output = new StringBuilder();
        StringBuilder word = new StringBuilder();
        
        // A quick list of our separators
        String separators = " \n\r\t,;!?[]<>:";

        for (char c : line.toCharArray()) {
            if (separators.indexOf(c) != -1) {
                if (word.length() > 0) {
                    output.append(redactToken(word)); // Call our original logic
                    word.setLength(0);
                }
                output.append(c);
            } else {
                word.append(c);
            }
        }
        // Catch the last word
        if (word.length() > 0) {
            output.append(redactToken(word));
        }

        return output.toString();
    }
}
