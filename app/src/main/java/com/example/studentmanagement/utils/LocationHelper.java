package com.example.studentmanagement.utils;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Looper;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class LocationHelper {
    private static final String TAG = "LocationHelper";
    private final Activity activity;
    private final FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    private LocationHelperCallback callback;
    
    public interface LocationHelperCallback {
        void onLocationReceived(double latitude, double longitude, String address);
        void onLocationError(String error);
    }
    
    public LocationHelper(Activity activity) {
        this.activity = activity;
        this.fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity);
    }
    
    public void getCurrentLocation(LocationHelperCallback callback) {
        this.callback = callback;
        
        // Check permission
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity,
                    new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    },
                    100);
            return;
        }
        
        // Try to get last location first
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(activity, location -> {
                    if (location != null) {
                        Log.d(TAG, "Got last location: " + location.getLatitude() + ", " + location.getLongitude());
                        String address = getAddressFromLocation(location.getLatitude(), location.getLongitude());
                        callback.onLocationReceived(location.getLatitude(), location.getLongitude(), address);
                    } else {
                        Log.d(TAG, "Last location is null, requesting current location");
                        requestCurrentLocation();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to get last location: " + e.getMessage());
                    requestCurrentLocation();
                });
    }
    
    private void requestCurrentLocation() {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            callback.onLocationError("Location permission not granted");
            return;
        }
        
        LocationRequest locationRequest = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 10000)
                .setMinUpdateIntervalMillis(5000)
                .setMaxUpdateDelayMillis(15000)
                .build();
        
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    callback.onLocationError("No location result");
                    return;
                }
                
                Location location = locationResult.getLastLocation();
                if (location != null) {
                    Log.d(TAG, "Got current location: " + location.getLatitude() + ", " + location.getLongitude());
                    String address = getAddressFromLocation(location.getLatitude(), location.getLongitude());
                    callback.onLocationReceived(location.getLatitude(), location.getLongitude(), address);
                    stopLocationUpdates();
                } else {
                    callback.onLocationError("Unable to get location");
                }
            }
        };
        
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
    }
    
    private void stopLocationUpdates() {
        if (locationCallback != null) {
            fusedLocationClient.removeLocationUpdates(locationCallback);
            locationCallback = null;
        }
    }
    
    private String getAddressFromLocation(double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(activity, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                StringBuilder sb = new StringBuilder();
                
                // Build full address
                for (int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
                    String line = address.getAddressLine(i);
                    if (line != null && !line.isEmpty()) {
                        sb.append(line);
                        if (i < address.getMaxAddressLineIndex()) {
                            sb.append(", ");
                        }
                    }
                }
                
                String fullAddress = sb.toString();
                Log.d(TAG, "Address: " + fullAddress);
                return fullAddress;
            } else {
                // Return coordinates if address not found
                return String.format(Locale.US, "Lat: %.6f, Lng: %.6f", latitude, longitude);
            }
        } catch (IOException e) {
            Log.e(TAG, "Geocoder error: " + e.getMessage());
            // Return coordinates if geocoder fails
            return String.format(Locale.US, "Lat: %.6f, Lng: %.6f", latitude, longitude);
        }
    }
    
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 100) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "Location permission granted");
                getCurrentLocation(callback);
            } else {
                Log.d(TAG, "Location permission denied");
                if (callback != null) {
                    callback.onLocationError("Location permission denied");
                }
            }
        }
    }
    
    public void cleanup() {
        stopLocationUpdates();
        callback = null;
    }
}