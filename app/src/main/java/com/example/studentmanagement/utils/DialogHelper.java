package com.example.studentmanagement.utils;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.example.studentmanagement.R;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class DialogHelper {
    
    public static void showSuccessDialog(Context context, String title, String message) {
        new MaterialAlertDialogBuilder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", null)
                .setIcon(R.drawable.ic_success)
                .show();
    }
    
    public static void showErrorDialog(Context context, String title, String message) {
        new MaterialAlertDialogBuilder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", null)
                .setIcon(R.drawable.ic_error)
                .show();
    }
    
    public static void showConfirmDialog(Context context, String title, String message, 
                                         DialogCallback callback) {
        new MaterialAlertDialogBuilder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("Yes", (dialog, which) -> callback.onConfirm())
                .setNegativeButton("No", (dialog, which) -> callback.onCancel())
                .show();
    }
    
    public static void showLoadingDialog(Activity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_loading, null);
        builder.setView(dialogView);
        builder.setCancelable(false);
        AlertDialog dialog = builder.create();
        dialog.show();
    }
    
    public static void showInfoDialog(Context context, String title, String message) {
        new MaterialAlertDialogBuilder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", null)
                .setIcon(R.drawable.ic_info)
                .show();
    }
    
    public interface DialogCallback {
        void onConfirm();
        void onCancel();
    }
}