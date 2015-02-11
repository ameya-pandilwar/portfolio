package edu.neu.madcourse.dharammaniar.trickiestpart.time;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import edu.neu.madcourse.dharammaniar.R;
import edu.neu.madcourse.dharammaniar.trickiestpart.Constants;

public class ConfigureTimeBasedActivity extends Activity {

    SharedPreferences preferences;
    Context context;

    int startHour, startMinute, endHour, endMinute;
    TimePicker startTimePicker, endTimePicker;
    TextView activityHeading;
    String activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.configure_time_based_activities);
        context = this;

        startTimePicker = (TimePicker) findViewById(R.id.startTimePicker);
        endTimePicker = (TimePicker) findViewById(R.id.endTimePicker);

        activityHeading = (TextView) findViewById(R.id.activityHeading);

        Intent intent = getIntent();
        activity = intent.getStringExtra("activity");

        loadKeywords();
    }

    public void onClickSaveActivityTimes(View view) {
        startHour = startTimePicker.getCurrentHour();
        startMinute = startTimePicker.getCurrentMinute();

        endHour = endTimePicker.getCurrentHour();
        endMinute = endTimePicker.getCurrentMinute();

        String startTime = String.valueOf(startHour) + ":" + String.valueOf(startMinute);
        String endTime = String.valueOf(endHour) + ":" + String.valueOf(endMinute);

        Toast.makeText(context, startTime + " - " + endTime, Toast.LENGTH_SHORT).show();

        saveTimes(startTime, endTime);
    }

    private void saveTimes(String startTime, String endTime) {
        preferences = this.getSharedPreferences(Constants.APPLICATION_NAME, Context.MODE_PRIVATE);
        Editor editor = preferences.edit();
        editor.putString(activity + "-starttime", startTime);
        editor.putString(activity + "-endtime", endTime);
        editor.commit();
        Toast.makeText(context, "Time Saved For " + activity, Toast.LENGTH_SHORT).show();
        finish();
    }

    private void loadKeywords() {
        activityHeading.setText(activity);
        preferences = this.getSharedPreferences(Constants.APPLICATION_NAME, Context.MODE_PRIVATE);
        String startTime = preferences.getString(activity + "-starttime", "");
        String endTime = preferences.getString(activity + "-endtime", "");

        if (!startTime.equalsIgnoreCase("") && !endTime.equalsIgnoreCase("")) {
            int startH = Integer.valueOf(startTime.split(":")[0]);
            int startM = Integer.valueOf(startTime.split(":")[1]);
            int endH = Integer.valueOf(endTime.split(":")[0]);
            int endM = Integer.valueOf(endTime.split(":")[1]);

            startTimePicker.setCurrentHour(startH);
            startTimePicker.setCurrentMinute(startM);
            endTimePicker.setCurrentHour(endH);
            endTimePicker.setCurrentMinute(endM);
        }

    }

}