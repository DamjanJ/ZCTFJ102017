package com.funny.bjokes.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.provider.Settings;

import com.funny.bjokes.Constants;
import com.funny.bjokes.FunnyQuoteApplication;

import java.util.Random;

/**
 * Created by CJH on 2016.01.14.
 */
public class DeviceUtils {

    private static String generateID2(Context context) {
        String android_id = Settings.Secure.getString(context.getContentResolver(), "android_id");
        if (android_id == null) {
            android_id = String.valueOf(new Random().nextLong());
        }
        return android_id;
    }

    public static String getDeviceID(final Context context) {
        final SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.PREFERENCE_KEY, Context.MODE_PRIVATE);
        final SharedPreferences.Editor edit = sharedPreferences.edit();

        String deviceID = sharedPreferences.getString(Constants.PREFERENCE_KEY__DEVICE_ID, "0");
        if (deviceID == null || deviceID.length() == 0 || deviceID.equals("0")) {
            deviceID = generateID2(context);

            FunnyQuoteApplication.sLogger.d("Android ID : " + deviceID);
            if (deviceID != null) {
                edit.putString(Constants.PREFERENCE_KEY__DEVICE_ID, deviceID);
                edit.commit();
            }
        }

        return deviceID;
    }
}
