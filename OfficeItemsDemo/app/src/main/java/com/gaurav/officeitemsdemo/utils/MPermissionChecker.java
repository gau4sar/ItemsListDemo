package com.gaurav.officeitemsdemo.utils;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;

@TargetApi(Build.VERSION_CODES.M)
public class MPermissionChecker {

    private static String TAG = "MPermissionChecker";
    private static final String[] PERMISSION_CAMERA = {Manifest.permission.CAMERA};
    private static final String[] PERMISSIONS_CONTACT = {Manifest.permission.READ_CONTACTS};
    private static final String[] PERMISSION_GALLERY = {Manifest.permission.READ_EXTERNAL_STORAGE};

    //GALLERY PERMISSION ACCESS
    public static boolean grantGalleryAccess(Activity activity, final int requestCode) {

        if (currentAPIVersion < android.os.Build.VERSION_CODES.M) {
            return true;
        }
        // Verify that all required contact permissions have been granted.
        else if (ActivityCompat.checkSelfPermission(activity.getBaseContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Gallery permissions have not been granted.
            //Crashlytics.log(Log.DEBUG, TAG, "Gallery permission has NOT been granted. Requesting permissions.");
            requestGalleryPermission(activity, requestCode);

            return false;
        }
        return true;
    }


    /**
     * Requests the Gallery permission.
     * If the permission has been denied previously, a SnackBar will prompt the user to grant the
     * permission, otherwise it is requested directly.
     */
    private static void requestGalleryPermission(final Activity activity, final int requestCode) {

        if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                Manifest.permission.READ_EXTERNAL_STORAGE)) {

            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setTitle("Accessing Gallery");
            builder.setMessage("Galley permission is needed. Allow access ?");

            builder.setPositiveButton("Okay", (dialog, which) -> {
                ActivityCompat.requestPermissions(activity, PERMISSION_GALLERY, requestCode);
                dialog.cancel();
            });

            builder.setNegativeButton("Cancel", (dialog, which) -> {
                //No button click
                dialog.cancel();
            }).show();

            // Provide an additional rationale to the user if the permission was not granted
            // and the user would benefit from additional context for the use of the permission.
            // For example, if the request has been denied previously.
            //Crashlytics.log(Log.DEBUG, TAG, "Displaying PERMISSION_GALLERY rationale to provide additional context.");

            //TODO: Display a SnackBar with an explanation and a button to trigger the request.

        } else {
            // Contact permissions have not been granted yet. Request them directly.
            ActivityCompat.requestPermissions(activity, PERMISSION_GALLERY, requestCode);
        }
    }


    // CONTACT PERMISSION CHECKER
    public static boolean grantContact(Activity activity, final int requestCode) {

        // Verify that all required contact permissions have been granted.
        if (ActivityCompat.checkSelfPermission(activity.getBaseContext(), Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {

            // Contacts permissions have not been granted.
            //Crashlytics.log(Log.DEBUG, TAG, "Contact permissions has NOT been granted. Requesting permissions.");
            requestContactsPermissions(activity, requestCode);

            return false;
        }
        return true;
    }

    /**
     * Requests the Contacts permissions.
     * If the permission has been denied previously, a SnackBar will prompt the user to grant the
     * permission, otherwise it is requested directly.
     */
    private static void requestContactsPermissions(final Activity activity, final int requestCode) {

        if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                Manifest.permission.READ_CONTACTS)) {

            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setTitle("Accessing Contact");
            builder.setMessage("Permission needed to access contact. Allow access ?");

            builder.setPositiveButton("Okay", (dialog, which) -> {
                ActivityCompat.requestPermissions(activity, PERMISSIONS_CONTACT, requestCode);
                dialog.cancel();
            });

            builder.setNegativeButton("Cancel", (dialog, which) -> {
                //No button click
                dialog.cancel();
            }).show();

            // Provide an additional rationale to the user if the permission was not granted
            // and the user would benefit from additional context for the use of the permission.
            // For example, if the request has been denied previously.
            //Crashlytics.log(Log.DEBUG, TAG, "Displaying contacts permission rationale to provide additional context.");

            //TODO: Display a SnackBar with an explanation and a button to trigger the request.

        } else {
            // Contact permissions have not been granted yet. Request them directly.
            ActivityCompat.requestPermissions(activity, PERMISSIONS_CONTACT, requestCode);
        }

    }

    private final static int currentAPIVersion = Build.VERSION.SDK_INT;

    // CAMERA PERMISSION CHECKER
    public static boolean grantCameraAccess(Activity activity, final int requestCode) {

        if (currentAPIVersion < android.os.Build.VERSION_CODES.M) {
            return true;
        }
        // Verify that all required contact permissions have been granted.
        else if (ActivityCompat.checkSelfPermission(activity.getBaseContext(), Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            // Contacts permissions have not been granted.
            //Crashlytics.log(Log.DEBUG, TAG, "Contact permissions has NOT been granted. Requesting permissions.");
            requestCameraPermissions(activity, requestCode);

            return false;
        }

        return true;
    }

    /**
     * Requests the Contacts permissions.
     * If the permission has been denied previously, a SnackBar will prompt the user to grant the
     * permission, otherwise it is requested directly.
     */
    private static void requestCameraPermissions(final Activity activity, final int requestCode) {

        if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                Manifest.permission.CAMERA)) {

            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setTitle("Accessing Camera");
            builder.setMessage("Permission needed to access camera. Allow access ?");

            builder.setPositiveButton("Okay", (dialog, which) -> {

                ActivityCompat.requestPermissions(activity, PERMISSION_CAMERA, requestCode);
                dialog.cancel();
            });

            builder.setNegativeButton("Cancel", (dialog, which) -> {
                //No button click
                dialog.cancel();
            }).show();

            // Provide an additional rationale to the user if the permission was not granted
            // and the user would benefit from additional context for the use of the permission.
            // For example, if the request has been denied previously.
            //Crashlytics.log(Log.DEBUG, TAG, "Displaying CAMERA permission rationale to provide additional context.");

            //TODO: Display a SnackBar with an explanation and a button to trigger the request.

        } else {
            // Contact permissions have not been granted yet. Request them directly.
            ActivityCompat.requestPermissions(activity, PERMISSION_CAMERA, requestCode);
        }
    }


    /**
     * Check that all given permissions have been granted by verifying that each entry in the
     * given array is of the value {@link PackageManager#PERMISSION_GRANTED}.
     **/
    public static boolean verifyPermissions(int[] grantResults) {
        // At least one result must be checked.
        if (grantResults.length < 1) {
            return false;
        }

        // Verify that each required permission has been granted, otherwise return false.
        for (int result : grantResults) {
            if (result == PackageManager.PERMISSION_GRANTED) {
                return true;
            }
        }
        return false;
    }

}