package com.example.studentmanagement;

import android.app.Application;

import com.example.studentmanagement.utils.PreferencesManager;

public class StudentManagementApplication extends Application {
    private static StudentManagementApplication instance;
    private PreferencesManager preferencesManager;
    
    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        preferencesManager = new PreferencesManager(this);
    }
    
    public static StudentManagementApplication getInstance() {
        return instance;
    }
    
    public PreferencesManager getPreferencesManager() {
        return preferencesManager;
    }
}