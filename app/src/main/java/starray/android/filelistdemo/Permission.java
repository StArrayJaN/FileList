package starray.android.filelistdemo;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class Permission {
    public static final int REQUEST_CODE = 5;
    private static final String[] permission = new String[]{
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
    };

    public static boolean isPermissionGranted(Activity activity){
        for (String s : permission) {
            int checkPermission = ContextCompat.checkSelfPermission(activity, s);
            if (checkPermission != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    public static void checkPermission(Activity activity){
        if (!isPermissionGranted(activity)) {
            ActivityCompat.requestPermissions(activity,permission,REQUEST_CODE);
        }
    }
}



