package edu.neu.madcourse.dharammaniar.trickiestpart.time;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import edu.neu.madcourse.dharammaniar.trickiestpart.Constants;

public class TimeBasedService extends Service {

    Context context;
    SharedPreferences preferences;
    String activities;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        context = this;
        loadDefaultActivities();
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf= new SimpleDateFormat("HH:mm");
        String str = sdf.format(c.getTime());
        int currentH = Integer.valueOf(str.split(":")[0]);
        int currentM = Integer.valueOf(str.split(":")[1]);
        checkForTimeBasedActivities(currentH, currentM);
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public void loadDefaultActivities() {
        preferences = this.getSharedPreferences(Constants.APPLICATION_NAME, Context.MODE_PRIVATE);
        activities = preferences.getString("activities", "");
        if (activities == "") {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("activities", Constants.DEFAULT_ACTIVITIES);
            editor.commit();
        }
    }

    public void checkForTimeBasedActivities(int currentH, int currentM) {
        preferences = this.getSharedPreferences(Constants.APPLICATION_NAME, Context.MODE_PRIVATE);
        String startTime, endTime;
        int startH, startM, endH, endM;
        boolean match = false;
        String[] activitiesSplit = activities.split(",");
        for (String activity : activitiesSplit) {
            if (!match) {
                startTime = preferences.getString(activity+"-starttime", "");
                endTime = preferences.getString(activity+"-endtime", "");
                if (!startTime.equalsIgnoreCase("") && !endTime.equalsIgnoreCase("")) {
                    startH = Integer.valueOf(startTime.split(":")[0]);
                    startM = Integer.valueOf(startTime.split(":")[1]);
                    endH = Integer.valueOf(endTime.split(":")[0]);
                    endM = Integer.valueOf(endTime.split(":")[1]);
                    if ((currentH >= startH && currentM >= startM) && currentH < endH) {
                        match = true;
                        Toast.makeText(context, "It's Time For " + activity, Toast.LENGTH_SHORT).show();
                        break;
                    }
                }
            } else {
                break;
            }
        }
    }
}