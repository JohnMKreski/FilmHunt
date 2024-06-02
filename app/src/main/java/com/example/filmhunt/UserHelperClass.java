package com.example.filmhunt;

import android.content.Context;
import android.widget.Toast;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class UserHelperClass {

    private String name, username, email, uid;

    public UserHelperClass() {
    }

    public UserHelperClass(String name, String username, String email, String uid) {
        this.name = name;
        this.username = username;
        this.email = email;
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUid() {
        return uid;
    }

    // Email validation
    public static boolean validateEmail(String email, Context context) {
        String emailPattern = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}$";
        Pattern pattern = Pattern.compile(emailPattern);
        Matcher matcher = pattern.matcher(email);
        boolean isValid = matcher.matches();
        if (!isValid) {
            Toast.makeText(context, "Invalid email format", Toast.LENGTH_SHORT).show();
        }
        return isValid;
    }

    // Password validation. Removed getters/setters for password. Handled by firebase
    public static boolean validatePassword(String password, Context context) {
        if (password.length() < 12) {
            Toast.makeText(context, "Password is too short. It must be at least 12 characters long.", Toast.LENGTH_SHORT).show();
            return false;
        }

        boolean hasUppercase = false;
        boolean hasLowercase = false;
        boolean hasDigit = false;
        boolean hasSymbol = false;
        String symbols = "!@#$%^&*()_-+=<>?/[]{},.:;";

        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) {
                hasUppercase = true;
            } else if (Character.isLowerCase(c)) {
                hasLowercase = true;
            } else if (Character.isDigit(c)) {
                hasDigit = true;
            } else if (symbols.contains(String.valueOf(c))) {
                hasSymbol = true;
            }
        }

        if (!hasUppercase) {
            Toast.makeText(context, "Password is missing an uppercase letter.", Toast.LENGTH_SHORT).show();
        }
        if (!hasLowercase) {
            Toast.makeText(context, "Password is missing a lowercase letter.", Toast.LENGTH_SHORT).show();
        }
        if (!hasDigit) {
            Toast.makeText(context, "Password is missing a digit.", Toast.LENGTH_SHORT).show();
        }
        if (!hasSymbol) {
            Toast.makeText(context, "Password is missing a symbol.", Toast.LENGTH_SHORT).show();
        }

        return hasUppercase && hasLowercase && hasDigit && hasSymbol;
    }
}
