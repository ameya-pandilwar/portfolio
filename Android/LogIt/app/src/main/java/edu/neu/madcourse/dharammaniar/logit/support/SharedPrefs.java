package edu.neu.madcourse.dharammaniar.logit.support;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Dharam on 11/25/2014.
 */
public class SharedPrefs {

    private static final String SharedPrefsName = "Logit";

    public static void setInt(String key, int intValue, Context mContext) {

        SharedPreferences sharedPreferences = mContext
                .getSharedPreferences(SharedPrefsName, Context.MODE_PRIVATE);
        SharedPreferences.Editor mEditor = sharedPreferences.edit();
        mEditor.putInt(key, intValue);
        mEditor.commit();
    }

    public static int getInt(String key, int defaultValue, Context mContext) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(SharedPrefsName,
                                                                            Context.MODE_PRIVATE);
        return sharedPreferences.getInt(key, defaultValue);
    }

    public static void setString(String key, String stringValue, Context mContext) {

        SharedPreferences sharedPreferences = mContext
                .getSharedPreferences(SharedPrefsName, Context.MODE_PRIVATE);
        SharedPreferences.Editor mEditor = sharedPreferences.edit();
        mEditor.putString(key, stringValue);
        mEditor.commit();
    }

    public static String getString(String key, String defaultValue, Context mContext) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(SharedPrefsName,
                                                                            Context.MODE_PRIVATE);
        return sharedPreferences.getString(key, defaultValue);
    }

    public static void setBoolean(String key, Boolean boolValue, Context mContext) {

        SharedPreferences sharedPreferences = mContext
                .getSharedPreferences(SharedPrefsName, Context.MODE_PRIVATE);
        SharedPreferences.Editor mEditor = sharedPreferences.edit();
        mEditor.putBoolean(key, boolValue);
        mEditor.commit();
    }

    public static Boolean getBoolean(String key, Boolean boolValue, Context mContext) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(SharedPrefsName,
                                                                            Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(key, boolValue);
    }

    public static void setLong(String key, long longValue, Context mContext) {

        SharedPreferences sharedPreferences = mContext
                .getSharedPreferences(SharedPrefsName, Context.MODE_PRIVATE);
        SharedPreferences.Editor mEditor = sharedPreferences.edit();
        mEditor.putLong(key, longValue);
        mEditor.commit();
    }

    public static long getLong(String key, long longValue, Context mContext) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(SharedPrefsName,
                                                                            Context.MODE_PRIVATE);
        return sharedPreferences.getLong(key, longValue);
    }
}
