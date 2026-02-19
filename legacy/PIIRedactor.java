package legacy;
import java.io.*;

// Key Features:
// 1. O(n) Time Complexity
// 2. O(1) Space Complexity (via Character Streaming)
// 3. No Regex Backtracking

public class PIIRedactor{

    public static void main(String[] args) throws Exception {
        if(args.length < 2) {
            System.out.println("USE: java PIIRedactor <input_file> <output_file>");
            return;
        }

        String inputFile = args[0];
        String outputFile = args[1];

        long startTime = System.currentTimeMillis();

        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile));

        BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {

        // The current token buffer
        StringBuilder word = new StringBuilder();

        int c;

        // CORE STREAMING LOOP: Reads 1 char at a time from disk/buffer.
        while((c = reader.read()) != -1) {
            // The final output - redacted log

            char character = (char) c;

            // The Delimiter logic
            if(isSeparator(character)) {
                // We hit a separator. Process the token in the buffer.
                if(word.length() > 0) {
                    writer.write(checkAndRedact(word));
                    word.setLength(0);
                }
                // Appending the separator itself
                writer.write(character);
            } else{
                // Build the token
                word.append(character);
            }
        }

        // Handling "Last remained Survivor"
        if(word.length() > 0) {
            writer.write(checkAndRedact(word));
        }  
    } catch (IOException e) {
        e.printStackTrace();
    }


    long endTime = System.currentTimeMillis();
    System.out.println("Without Regex Processing Time: "+ (endTime - startTime) + "ms");

    }

    // --- HELPER FUNCTIONS ---

    // Determines if a character acts as a word delimiter.
    private static boolean isSeparator(char c) {
        return c == ' ' || c == '\n' || c == '\r' || c == '\t' || 
               c == ',' || c == ';' || c == '!' || c == '?' || c == '[' || c == ']' || c == '<' || c == '>' || c == ':';
    }

    // REDACTION ENGINE
    private static String checkAndRedact(StringBuilder token){
        String s = token.toString();
        String prefix = "";
        String suffix = "";

        // peel off prefix separators
        while(s.length() > 0 && (s.startsWith("(") || s.startsWith("<") || s.startsWith("["))) {
            prefix += s.charAt(0);
            s = s.substring(1);
        }

        // peel off suffix separators
        while (s.length() > 0 && (s.endsWith(".") || s.endsWith(")") || s.endsWith(">") || s.endsWith("]"))) {
            suffix = s.charAt(s.length()- 1) + suffix;
            s = s.substring(0, s.length()-1);
        }

        if(s.isEmpty()) return prefix + suffix;

        if (isEmail(s)) return prefix + "[EMAIL]" + suffix;
        if (isPhoneNumber(s)) return prefix + "[PHONE]" + suffix;
        if (isPAN(s)) return prefix + "[PAN]" + suffix;

        // If not PII, return original string
        return token.toString();
    }

    // No regex. Strict rules to reduce false positives.
    private static boolean isEmail(String s) {
        if (s == null) return false;
        int n = s.length();
        if (n < 6 || n > 254) return false;

        int atIndex = s.indexOf('@');
        if(atIndex <= 0 || atIndex >= n - 3) return false; //must have local + domain

        // Local part validation
        if(!isValidEmailLocalPart(s, 0, atIndex - 1)) return false;

        // domain part validation
        if(!isValidEmailDomain(s, atIndex + 1, n - 1))return false;

        return true;
    }

    private static boolean isValidEmailLocalPart(String s, int start, int end) {
        if(start > end)return false;
        // Local part can't start or end with dot
        if(s.charAt(start) == '.' || s.charAt(end) == '.') return false;

        boolean prevWasDot = false;

        for(int i = start; i <= end; i++) {
            char c = s.charAt(i);
            if(c == '.'){
                if(prevWasDot) return false;
                prevWasDot = true;
            }
            else {
                prevWasDot = false;
                if (!Character.isLetterOrDigit(c) && c != '_' && c != '-' && c != '+') return false;
            }
        }
        return true;
    }

    private static boolean isValidEmailDomain(String s, int start, int end) {
        if (start > end) return false;

        // domain must contain atleast one dot and not start/end with dot
        if(s.charAt(start) == '.' || s.charAt(end) == '.') return false;

        int dotCount = 0;
        boolean prevWasDot = false;

        for(int i = start; i <= end; i++) {
            char c = s.charAt(i);
            if(c == '.') {
                if(prevWasDot) return false;
                dotCount++;
                prevWasDot =  true;
            }
            else {
                prevWasDot = false;
            
            if (!(Character.isLetterOrDigit(c) || c == '.' || c == '-'))return false;
            }
        }

        if (dotCount == 0)return false;
    

        // basic TLD check: last part length >= 2
        int lastDot = -1;
        for(int i = end; i >= start;i--) {
            if(s.charAt(i) == '.') {
                lastDot = i;
                break;
            }
        }

        if(lastDot == -1) return false;
        if(end - lastDot < 2)return false;

        return true;
    }

    // Phone Checker
    private static boolean isPhoneNumber(String s) {
        if (s.length() < 7) return false;

        // Ignore Dates YYYY-MM-DD
        if(s.length() == 10 && s.charAt(4) == '-' && s.charAt(7) == '-')return false;

        int digitCount = 0;
        for(int i = 0;i < s.length(); i++){
            char c = s.charAt(i);
            if(Character.isDigit(c)) {
                digitCount++;
            } else if(c != '+' && c != '-' && c != '(' && c != ')')return false;
        }

        return digitCount >= 7 && digitCount <= 15;
    }

    // PAN Card Checker: 5 Letters + 4 Digits + 1 Letter
    private static boolean isPAN(String s) {
        if(s.length() != 10)return false;

        // First 5 chars must be Uppercase letters
        for(int i = 0;i < 5;i++){
            char c = s.charAt(i);
            if(c < 'A' || c > 'Z')return false;
        }

        // Next 4 chars must be Digits
        for(int i = 5;i < 9;i++){
            char c = s.charAt(i);
            if(c < '0' || c > '9')return false;
        }

        // Last char must be letter
        char last = s.charAt(9);
        return last >= 'A' && last <= 'Z';
    }

}