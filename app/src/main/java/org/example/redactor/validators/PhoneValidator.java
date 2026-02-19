package org.example.redactor.validators;

public class PhoneValidator implements PIIValidator{
    @Override
    public boolean isValid(String s) {
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

    @Override
    public String getReplacementTag() {
        return "[PHONE]";
    }
}
