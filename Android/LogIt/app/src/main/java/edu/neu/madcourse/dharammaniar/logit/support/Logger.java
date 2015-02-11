package edu.neu.madcourse.dharammaniar.logit.support;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.BatteryManager;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import au.com.bytecode.opencsv.CSVWriter;

/**
 * Created by Dharam on 11/25/2014.
 */
public class Logger {

    private static String versionNumber;

    public static void log(String TAG, String message, Context mContext) {
        Log.i(TAG, message);
        String currentDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        try {
            versionNumber = mContext.getPackageManager()
                                    .getPackageInfo(mContext.getPackageName(), 0).versionName;
            File folder = new File(
                    Environment.getExternalStorageDirectory() + "/.logit/logs/" + currentDate);
            if (!folder.exists()) {
                folder.mkdirs();
            }
            CSVWriter logFile = new CSVWriter(new FileWriter(
                    Environment.getExternalStorageDirectory() + "/.logit/logs/" + currentDate +
                    "/" + TAG + ".csv",
                    true));

            String log[] = new String[5];
            log[0] = versionNumber;
            log[1] = TAG;
            log[2] = new Date().toString();
            log[3] = getBatteryLevel(mContext) + "";
            log[4] = message;

            logFile.writeNext(log);
            logFile.close();

        } catch (IOException e) {
            Log.d(TAG, "IOException in logging error.");
            e.printStackTrace();
        } catch (PackageManager.NameNotFoundException e) {
            Log.d(TAG, "PackageManager.NameNotFoundException in logging error.");
            e.printStackTrace();
        }
    }

    public static float getBatteryLevel(Context context) {
        Intent batteryIntent = context.registerReceiver(null,
                                                        new IntentFilter(
                                                                Intent.ACTION_BATTERY_CHANGED));
        if (batteryIntent != null) {
            int level = batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            int scale = batteryIntent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

            // Error checking that probably isn't needed but I added just in case.
            if (level == -1 || scale == -1) {
                return 50.0f;
            }
            return ((float) level / (float) scale) * 100.0f;
        }
        return -1;
    }
}
