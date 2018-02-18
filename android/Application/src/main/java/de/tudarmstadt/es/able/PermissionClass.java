package de.tudarmstadt.es.able;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

/**
 * Created by user on 14.02.18.
 */

public class PermissionClass {


    public static boolean isLocationEnabled(Context context) {
        int locationMode = 0;
        String locationProviders;
        int permissionCheckFine = 1;
        int permissionCheckCoarse = 1;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            try {
                locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);
                permissionCheckFine = ContextCompat.checkSelfPermission(context , Manifest.permission.ACCESS_FINE_LOCATION);
                permissionCheckCoarse = ContextCompat.checkSelfPermission(context , Manifest.permission.ACCESS_COARSE_LOCATION);
            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
                return false;
            }

            boolean result = (locationMode != Settings.Secure.LOCATION_MODE_OFF)&&
                    (0 == permissionCheckFine) && (0 == permissionCheckCoarse);
            return result;

        }else{
            locationProviders = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            return !TextUtils.isEmpty(locationProviders);
        }


    }
}
