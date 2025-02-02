package com.example.myapplication.controller;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A utility class for managing permissions and handling location data.
 * This class helps to request permissions, process their results, and store location data in Firebase Firestore.
 */
public class PermissionHelper {

    /**
     * The request code used when requesting permissions.
     */
    private static final int PERMISSION_REQUEST_CODE = 1001;

    /**
     * The fragment context, if applicable.
     */
    private final Fragment fragment;

    /**
     * The activity context.
     */
    private final Activity activity;

    /**
     * The client used to fetch the device's location.
     */
    private FusedLocationProviderClient fusedLocationProviderClient;

    /**
     * Constructor for creating a PermissionHelper instance with an activity context.
     *
     * @param activity The activity from which this class is instantiated.
     */
    public PermissionHelper(Activity activity) {
        this.activity = activity;
        this.fragment = null;
    }

    /**
     * Handles the results of a permission request.
     *
     * @param permissions  The permissions requested.
     * @param grantResults The grant results corresponding to the requested permissions.
     * @param callback     The callback to handle permission results.
     */
    public void handlePermissionResult(String[] permissions, int[] grantResults,
                                       PermissionResultCallback callback) {
        boolean locationGranted = false;
        boolean notificationGranted = false;
        for (int i = 0; i < permissions.length; i++) {
            String permission = permissions[i];
            if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                if (Manifest.permission.ACCESS_FINE_LOCATION.equals(permission)) {
                    locationGranted = true;
                } else if (Manifest.permission.POST_NOTIFICATIONS.equals(permission)) {
                    notificationGranted = true;
                }
            }
        }
        callback.onPermissionsResult(locationGranted, notificationGranted);
    }

    /**
     * Returns a list of required permissions that have not yet been granted.
     *
     * @return A list of ungranted permissions.
     */
    public List<String> getUngrantedPermissions() {
        List<String> requiredPermissions = new ArrayList<>();
        requiredPermissions.add(Manifest.permission.ACCESS_FINE_LOCATION);

        // Add POST_NOTIFICATIONS permission only for Android 13 and above.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requiredPermissions.add(Manifest.permission.POST_NOTIFICATIONS);
        }

        List<String> ungrantedPermissions = new ArrayList<>();
        for (String permission : requiredPermissions) {
            if (ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
                ungrantedPermissions.add(permission);
            }
        }

        Log.d("PermissionHelper", "Ungranted permissions: " + ungrantedPermissions);
        return ungrantedPermissions;
    }

    /**
     * Requests the specified permissions from the user.
     *
     * @param permissions A list of permissions to request.
     * @param db          The FirebaseFirestore instance for database operations.
     */
    public void requestPermissions(List<String> permissions, FirebaseFirestore db) {
        if (!permissions.isEmpty()) {
            Log.d("PermissionHelper", "Requesting permissions: " + permissions);

            if (activity != null) {
                ActivityCompat.requestPermissions(activity, permissions.toArray(new String[0]), PERMISSION_REQUEST_CODE);
            } else {
                Log.e("PermissionHelper", "No valid context to request permissions.");
            }
        }
    }

    /**
     * Fetches the device's last known location and stores it in the specified Firestore document.
     *
     * @param db      The FirebaseFirestore instance for database operations.
     * @param eventId The ID of the event document in Firestore where the location data will be stored.
     */
    public void fetchAndStoreLocation(FirebaseFirestore db, String eventId) {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(activity);
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.e("PermissionHelper", "Location permission not granted.");
            return;
        }

        fusedLocationProviderClient.getLastLocation()
                .addOnSuccessListener(location -> {
                    if (location != null) {
                        double latitude = location.getLatitude();
                        double longitude = location.getLongitude();
                        Map<String, Double> coordPair = new HashMap<>();
                        coordPair.put("latitude", latitude);
                        coordPair.put("longitude", longitude);

                        db.collection("events").document(eventId)
                                .update("coordinates", FieldValue.arrayUnion(coordPair))
                                .addOnSuccessListener(documentReference -> {
                                    Log.d("PermissionHelper", "Location saved successfully!");
                                })
                                .addOnFailureListener(e -> {
                                    Log.e("PermissionHelper", "Failed to save location: " + e.getMessage());
                                });
                    } else {
                        Log.e("PermissionHelper", "Failed to fetch location.");
                    }
                });
    }

    /**
     * Callback interface for handling the results of permission requests.
     */
    public interface PermissionResultCallback {
        /**
         * Called when the results of a permission request are available.
         *
         * @param locationGranted     Whether the location permission was granted.
         * @param notificationGranted Whether the notification permission was granted.
         */
        void onPermissionsResult(boolean locationGranted, boolean notificationGranted);
    }
}
