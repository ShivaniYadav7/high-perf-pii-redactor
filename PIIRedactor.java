import java.io.*;

public class PIIRedactor{

    public static void main(String[] args) throws Exception {
        if(args.length < 2) {
            System.out.println("USE: Java PIIRedactor <input_file> <output_file>");
            return;
        }

        String inputFile = args[0];
        String outputFile = args[1];

        long startTime = System.currentTimeMillis();

        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile));

        BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {

        String line;
        while((line = reader.readLine()) != null) {
            String redactedLog = parseAndRedact(line);

            writer.write(redactedLog);
            writer.newLine();
        }
    } catch (IOException e) {
        e.printStackTrace();
    }

    long endTime = System.currentTimeMillis();
    System.out.println("Without Regex Processing Time: "+ (endTime - startTime) + "ms");

    }

    public static String parseAndRedact(String input) {
        // The final output - redacted log
        StringBuilder result = new StringBuilder();
        // The current token buffer
        StringBuilder word = new StringBuilder();

        int n = input.length();

        for(int i = 0; i < n; i++){
            char c = input.charAt(i);

            // The Delimiter logic
            if(isSeparator(c)) {
                // We hit a separator. Process the token in the buffer.
                if(word.length() > 0) {
                    String processed = checkAndRedact(word);
                    result.append(processed);
                    word.setLength(0);
                }
                // Appending the separator itself
                result.append(c);
            } else{
                // Build the word
                word.append(c);
            }
        }
        if(word.length() > 0) {
            result.append(checkAndRedact(word));
        }

        return result.toString();
    }

    // Core logic tokenize (No Regex)

    private static boolean isSeparator(char c) {
        return c == ' ' || c == '\n' || c == '\t' || c == ',' || c == ';' || c == '!' || c == '?';
    }

    private static String checkAndRedact(StringBuilder token){
        String s = token.toString();

        if (isEmail(s)) return "[EMAIL]";
        if (isPhoneNumber(s)) return "[PHONE]";
        if (isPAN(s)) return "[PAN]";

        return s;
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