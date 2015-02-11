package edu.neu.madcourse.dharammaniar.trickiestpart.location;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import au.com.bytecode.opencsv.CSVReader;
import edu.neu.madcourse.dharammaniar.R;

/**
 * Created by Dharam on 11/20/2014.
 */
public class LocationActivity extends Activity
        implements View.OnClickListener {

    public static final long[] VIBRATE_INTENSE = {1000, 200, 1000, 200, 500, 250, 500, 250, 500,
            250, 500, 250, 250, 250, 250, 250, 250, 250, 250, 250, 250, 100, 100, 100, 100, 100,
            100, 100, 1000, 2000};
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private ToggleButton toggleButton;
    private TextView locationList;
    private String TAG = "Location";
    private CSVReader mLocationReader;
    private AlarmManager am;
    private PendingIntent minuteService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trickiest_location);
        toggleButton = (ToggleButton) findViewById(R.id.toggleButton);
        toggleButton.setOnClickListener(this);
        locationList = (TextView) findViewById(R.id.activity_trickiest_location_locationlist);
        updateLocationList();
        sharedPreferences = getSharedPreferences("logit", MODE_PRIVATE);
        editor = sharedPreferences.edit();
        if (sharedPreferences.getBoolean("LocationListener", false)) {
            toggleButton.setChecked(true);
        }

        am = (AlarmManager) getApplicationContext()
                .getSystemService(Context.ALARM_SERVICE);
        minuteService = PendingIntent.getService(getApplicationContext(), 12000,
                                                               new Intent(getApplicationContext(),
                                                                          MinuteService.class), 0);
    }

    private void updateLocationList() {
        String locationListString = "New Locations found at times:\n";
        try {
            File locationFile = new File(
                    Environment.getExternalStorageDirectory() + "/.logit/location.csv");
            if (locationFile.exists()) {
                mLocationReader = new CSVReader(new FileReader(locationFile));
                List<String[]> locations = mLocationReader.readAll();
                if (!locations.isEmpty()) {
                    for (int i = 0; i < locations.size(); i++) {
                        if(locations.get(i)[5].equals("New Location")) {
                            locationListString = locationListString + locations.get(i)[0] + "\n";
                        }
                    }
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        locationList.setText(locationListString);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == toggleButton.getId()) {
            if (toggleButton.isChecked()) {
                registerLocationListener();
                editor.putBoolean("LocationListener", true);
                editor.commit();
            } else {
                unregisterLocationListener();
                editor.putBoolean("LocationListener", false);
                editor.commit();
            }
        }
    }

    private void unregisterLocationListener() {
        Log.d(TAG,"Cancelling minute service");
        am.cancel(minuteService);
    }

    private void registerLocationListener() {

        Log.d(TAG,"Setting minute service");

        am.cancel(minuteService);

        long firstTime = SystemClock.elapsedRealtime();
        am.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, firstTime, 1000 * 60, minuteService);

    }

}