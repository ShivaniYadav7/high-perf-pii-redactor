package org.example.redactor.validators;

public class EmailValidator implements PIIValidator {
    @Override
    public boolean isValid(String s) {
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

    @Override
    public String getReplacementTag() {
        return "[EMAIL]";
    }
}
