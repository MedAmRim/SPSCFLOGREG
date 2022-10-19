package com.example.demo.Registration;

import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class EmailValidator implements Predicate<String>{

    private final Pattern EMAIL_PATTERN = Pattern.compile("[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9.-]+");

    @Override
    public boolean test(String t) {
        Matcher emailMatcher = EMAIL_PATTERN.matcher(t);
        return emailMatcher.find();
    }
    
}
