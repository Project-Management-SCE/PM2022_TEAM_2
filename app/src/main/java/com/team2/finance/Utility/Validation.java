package com.team2.finance.Utility;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Validation {

    public static boolean isAlpha(String name) {
        String expression = "^/^$|\\s+/|[a-zA-Z]+$";
        CharSequence inputStr = name;
        Pattern pattern = Pattern.compile(expression);
        Matcher matcher = pattern.matcher(inputStr);
        return matcher.matches();
    }

    public static boolean isEmailValid(String email) {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    //Password must contain minimum 8 characters at least 1 Alphabet, 1 Number
    public static boolean isValidPassword(String password) {
        final String PASSWORD_PATTERN =
                "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*).{8,20}$";
        final Pattern pattern = Pattern.compile(PASSWORD_PATTERN);
        Matcher matcher = pattern.matcher(password);
        return matcher.matches();
    }

    public static boolean isValidPhoneNumber(String phone) {
        return phone.matches("^[0-9]{10}$");
    }

    public static boolean isEmpty(String password) {
        if (password.equals("")) {
            return false;
        }
        return true;
    }

}
