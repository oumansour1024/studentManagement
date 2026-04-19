package com.example.studentmanagement.utils;

import android.text.TextUtils;
import android.util.Patterns;

public class ValidationUtils {
    
    public static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
    
    public static boolean isValidPhone(String phone) {
        if (TextUtils.isEmpty(phone)) {
            return false;
        }
        // Remove spaces and special characters
        String cleanedPhone = phone.replaceAll("[\\s\\-()]", "");
        // Check if it matches phone pattern (8-15 digits, can start with +)
        return cleanedPhone.matches("^[+]?[0-9]{8,15}$");
    }
    
    public static boolean isValidName(String name) {
        return !TextUtils.isEmpty(name) && name.length() >= 2 && name.matches("[a-zA-Z\\s\\-']+");
    }
    
    public static boolean isValidGrade(double grade) {
        return grade >= 0 && grade <= 20;
    }
    
    public static boolean isValidCoefficient(double coefficient) {
        return coefficient > 0 && coefficient <= 10;
    }
    
    public static String formatPhoneNumber(String phone) {
        if (TextUtils.isEmpty(phone)) {
            return "";
        }
        // Remove all non-digit characters except +
        String cleaned = phone.replaceAll("[^0-9+]", "");
        
        // Format for display (example: +212 6XX XXX XXX)
        if (cleaned.length() >= 10) {
            if (cleaned.startsWith("+")) {
                return cleaned.substring(0, 4) + " " + 
                       cleaned.substring(4, 7) + " " + 
                       cleaned.substring(7, 10) + " " + 
                       cleaned.substring(10);
            } else {
                return cleaned.substring(0, 3) + " " + 
                       cleaned.substring(3, 6) + " " + 
                       cleaned.substring(6, 9) + " " + 
                       cleaned.substring(9);
            }
        }
        return cleaned;
    }
}