package org.example.redactor.validators;

public class PanValidator implements PIIValidator{
    @Override
    public boolean isValid(String s) {
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

    @Override
    public String getReplacementTag() {
        return "[PAN]";
    }
}
