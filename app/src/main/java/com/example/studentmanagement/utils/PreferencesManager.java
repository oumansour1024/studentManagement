package com.example.studentmanagement.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferencesManager {
    private static final String PREF_NAME = "StudentManagementPrefs";
    private static final String KEY_TOKEN = "auth_token";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_USER_NOM = "user_nom";
    private static final String KEY_USER_PRENOM = "user_prenom";
    private static final String KEY_LANGUAGE = "language";
    private static final String KEY_THEME = "theme";
    
    private final SharedPreferences prefs;
    private final SharedPreferences.Editor editor;
    
    public PreferencesManager(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = prefs.edit();
    }
    
    public void saveAuthToken(String token) {
        editor.putString(KEY_TOKEN, token);
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.apply();
    }
    
    public String getAuthToken() {
        return prefs.getString(KEY_TOKEN, "");
    }
    
    public void saveUsername(String username) {
        editor.putString(KEY_USERNAME, username);
        editor.apply();
    }
    
    public String getUsername() {
        return prefs.getString(KEY_USERNAME, "");
    }
    
    public boolean isLoggedIn() {
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false);
    }
    
    public void logout() {
        editor.clear();
        editor.apply();
    }
    
    public void savePreference(String key, String value) {
        editor.putString(key, value);
        editor.apply();
    }
    
    public String getPreference(String key) {
        return prefs.getString(key, "");
    }
    
    public void savePreference(String key, boolean value) {
        editor.putBoolean(key, value);
        editor.apply();
    }
    
    public boolean getPreference(String key, boolean defaultValue) {
        return prefs.getBoolean(key, defaultValue);
    }
    
    public void savePreference(String key, int value) {
        editor.putInt(key, value);
        editor.apply();
    }
    
    public int getPreference(String key, int defaultValue) {
        return prefs.getInt(key, defaultValue);
    }
    
    // Specific user info methods
    public void saveUserId(int userId) {
        editor.putInt(KEY_USER_ID, userId);
        editor.apply();
    }
    
    public int getUserId() {
        return prefs.getInt(KEY_USER_ID, -1);
    }
    
    public void saveUserNom(String nom) {
        editor.putString(KEY_USER_NOM, nom);
        editor.apply();
    }
    
    public String getUserNom() {
        return prefs.getString(KEY_USER_NOM, "");
    }
    
    public void saveUserPrenom(String prenom) {
        editor.putString(KEY_USER_PRENOM, prenom);
        editor.apply();
    }
    
    public String getUserPrenom() {
        return prefs.getString(KEY_USER_PRENOM, "");
    }
    
    public String getUserFullName() {
        return getUserPrenom() + " " + getUserNom();
    }

    // Language preference
    public void setLanguage(String language) {
        editor.putString(KEY_LANGUAGE, language);
        editor.apply();
    }

    public String getLanguage() {
        return prefs.getString(KEY_LANGUAGE, "en");
    }

    // Theme preference
    public void setTheme(String theme) {
        editor.putString(KEY_THEME, theme);
        editor.apply();
    }

    public String getTheme() {
        return prefs.getString(KEY_THEME, "system");
    }
}