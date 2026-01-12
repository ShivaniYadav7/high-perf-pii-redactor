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
                    String processed = checkAndRedact(word);
                    writer.write(processed);
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
        return c == ' ' || c == '\n' || c == '\t' || c == ',' || c == ';' || c == '!' || c == '?' || c == '\r';
    }

    private static String checkAndRedact(StringBuilder token){
        String s = token.toString();

        // Edge Case: Word ends with a dot (e.g. "9876543210")
        String punctuation = "";

        if(s.endsWith(".")){
            punctuation = ".";
            s = s.substring(0, s.length() - 1);
        }

        if (isEmail(s)) return "[EMAIL]" + punctuation;
        if (isPhoneNumber(s)) return "[PHONE]" + punctuation;
        if (isPAN(s)) return "[PAN]" + punctuation;

        // If not PII, return original string
        return token.toString();
    }

    // Email Checker: Look for '@' and '.'
    private static boolean isEmail(String s) {
        if(s.length() < 5) return false;

        int atIndex = -1;
        int dotIndex = -1;

        for(int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if(c == '@') atIndex = i;
            else if(c == '.') dotIndex = i;
        }

        return atIndex > 0 && dotIndex > atIndex;
    }

    // Phone Checker: 10 digits
    private static boolean isPhoneNumber(String s) {
        if (s.length() != 10) return false;

        for(int i = 0;i < 10;i++){
            char c = s.charAt(i);
            if(c < '0' || c > '9')return false;
        }
        return true;
    }

    // PAN Card Checker: 5 Letters, 4 Digits, 1 Letter
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