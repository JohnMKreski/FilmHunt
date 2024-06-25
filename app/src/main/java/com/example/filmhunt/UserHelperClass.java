package com.example.filmhunt;

import android.content.Context;
import android.widget.Toast;

import com.example.filmhunt.Models.ImdbResponse;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UserHelperClass {

    private String name, username, email, uid, password;
    private Map<String, ImdbResponse.Movie> watchlist; //to store a map of movies



    public UserHelperClass() {
    }

    public UserHelperClass(String name, String username, String email, String uid, String password) {
        this.name = name;
        this.username = username;
        this.email = email;
        this.uid = uid;
        this.password = password;
//        this.watchlist = watchlist;
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

    public String getPassword () { return password; }

//    public Map<String, ImdbResponse.Movie> getWatchlist() {
//        return watchlist;
//    }
//
//    public void setWatchlist(Map<String, ImdbResponse.Movie> watchlist) {
//        this.watchlist = watchlist;
//    }

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
        if (password.length() < 6) {
            Toast.makeText(context, "Password must be at least 6 characters.", Toast.LENGTH_SHORT).show();
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
            Toast.makeText(context, "Password must include an uppercase letter.", Toast.LENGTH_SHORT).show();
        }
        if (!hasLowercase) {
            Toast.makeText(context, "Password must include a lowercase letter.", Toast.LENGTH_SHORT).show();
        }
        if (!hasDigit) {
            Toast.makeText(context, "Password must include a digit.", Toast.LENGTH_SHORT).show();
        }
        if (!hasSymbol) {
            Toast.makeText(context, "Password must include a symbol.", Toast.LENGTH_SHORT).show();
        }

        return hasUppercase && hasLowercase && hasDigit && hasSymbol;
    }
}
