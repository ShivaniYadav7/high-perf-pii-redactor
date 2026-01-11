import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public class LogGenerator {
    public static void main(String[] args) {
        String fileName = "large_test.log";

        if(args.length > 0){
            fileName = args[0];
        } else {
            System.out.println("No name provided. Using default: " + fileName);
            System.out.println("Tip: Run as 'java LogGenerator my_file.log' to specify name.");
        }

        long targetSize = 100 * 1024 * 1024;

        System.out.println("Generating ~100MB log file");
        long startTime = System.currentTimeMillis();

        try(BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            long currentSize = 0;
            Random random = new Random();

            while (currentSize < targetSize){
                String line = generateLine(random);
                writer.write(line);
                writer.newLine();

                currentSize += line.length() + 1;
            }

                System.out.println("Done! file created: "+fileName);
                System.out.println("Time Taken: "+ (System.currentTimeMillis() - startTime)+" ms");
            } catch (IOException e){
                e.printStackTrace();
            }
        }

        private static String generateLine(Random random) {
            int type = random.nextInt(5);
            String timestamp = "2064-01-10 11:11" + random.nextInt(60);

            switch(type) {
                case 0: // Email
                    return timestamp + "INFO User login: " + randomString(5) +"@test.com successfully.";

                case 1: // PAN
                    return timestamp + " DEBUG PAN verification for "+ randomPan() + "started.";

                case 2: //PHONE
                    return timestamp + "WARN Contact updated to "+ (9000000000L + random.nextInt(999999999)) + ".";

                case 3: //NOISE (No PII)
                    return timestamp + "ERROR Connection timeout at node" + random.nextInt(100) + ".";
                
                default: //Tricky (Email with punctuation)
                    return timestamp + "INFO Email sent to "+ randomString(4) + "@gmail.com, please check.";
            }
        }

        private static String randomString(int length){
            StringBuilder sb = new StringBuilder();
            for(int i = 0;i < length; i++) {
                sb.append((char) ('a' + (int)(Math.random() * 26)));
            }
            return sb.toString();
        }

        private static String randomPan() {
            return "ABCDE" + (1000 + (int) (Math.random() * 9000)) + "F";
        }
}
