package com.funny.bjokes.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.funny.bjokes.Constants;

/**
 * Created by CJH on 2016.01.14.
 */
public class PrefsUtils {

    public static int getSelectedTab(Context paramContext) {
        return paramContext.getSharedPreferences(Constants.PREFERENCE_KEY, Context.MODE_PRIVATE).getInt(Constants.PREFERENCE_KEY__TAB_SELECTED, 0);
    }

    public static void setSelectedTab(Context paramContext, int tabIndex) {
        SharedPreferences.Editor localEditor = paramContext.getSharedPreferences(Constants.PREFERENCE_KEY, Context.MODE_PRIVATE).edit();
        localEditor.putInt(Constants.PREFERENCE_KEY__TAB_SELECTED, tabIndex);
        localEditor.commit();
    }

    public static int getNewFeed(Context paramContext) {
        return paramContext.getSharedPreferences(Constants.PREFERENCE_KEY, Context.MODE_PRIVATE).getInt(Constants.PREFERENCE_KEY__NEW_FEED, 0);
    }

    public static void setNewFeed(Context paramContext, int news) {
        SharedPreferences.Editor localEditor = paramContext.getSharedPreferences(Constants.PREFERENCE_KEY, Context.MODE_PRIVATE).edit();
        localEditor.putInt(Constants.PREFERENCE_KEY__NEW_FEED, news);
        localEditor.commit();
    }

    public static void initAdmob() {
        Constants.PAGE_VIEWED = 0;
        Constants.GOOGLE_ADS = false;
        Constants.ADCOLONY_ADS = false;
        Constants.ADS_FIRST_TIME = false;
    }

}
