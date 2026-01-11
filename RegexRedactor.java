import java.io.*;
import java.util.regex.*;

public class RegexRedactor {
    public static void main(String[] args) throws Exception {
        if(args.length < 2){
            System.out.println("USE: java RegexRedactor <input_file> <output_file>");
            return;
        }

            String inputFile = args[0];
            String outputFile = args[1];

            String regex = "([a-zA-Z0-9._-]+@[a-z]+\\.com)|(\\b\\d{10}\\b)|([A-Z]{5}[0-9]{4}[A-Z]{1})";
            
            Pattern pattern = Pattern.compile(regex);

            long startTime = System.currentTimeMillis();

            try (BufferedReader reader = new BufferedReader(new FileReader(inputFile));
            BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {
                String line;

                while((line = reader.readLine()) != null) {
                    String redacted = parseAndRedact(line, pattern);

                    writer.write(redacted);
                    writer.newLine();
                }
        } catch (IOException e) {
            e.printStackTrace();
        }

        long endTime = System.currentTimeMillis();
        System.out.println("Regex Processing Time: " + (endTime - startTime) + "ms");
    }

    private static String parseAndRedact(String line,Pattern pattern) {
        Matcher matcher = pattern.matcher(line);
        StringBuffer sb = new StringBuffer();

        while(matcher.find()) {
            if(matcher.group(1) != null){
                matcher.appendReplacement(sb, "[EMAIL]");
            }

            else if(matcher.group(2) != null) {
                matcher.appendReplacement(sb,"[PHONE");
            }

            else if(matcher.group(3) != null) {
                matcher.appendReplacement(sb, "[PAN]");
            }
        }

        matcher.appendTail(sb);
        return sb.toString();
    }
    
}
