package edu.neu.madcourse.dharammaniar.logit;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;
import edu.neu.madcourse.dharammaniar.R;

/**
 * Created by Ameya on 05-12-2014.
 */
public class LogItRecordManualEntryScreen extends Activity {

    Context context;

    private Spinner categoriesSpinner, activitiesSpinner;
    private TimePicker startTimePicker, endTimePicker;
    List<String> categoriesList;
    List<String> activitiesList;

    Map<String, List<String>> map;
    String date;

    String latitude = "0.0", longitude = "0.0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_it_record_manual_entry_screen);

        context = this;

        Intent intent = getIntent();
        date = intent.getStringExtra("date");

        categoriesSpinner = (Spinner) findViewById(R.id.recordManualEntryCategorySpinner);
        activitiesSpinner = (Spinner) findViewById(R.id.recordManualEntryActivitySpinner);

        startTimePicker = (TimePicker) findViewById(R.id.recordManualEntryStartTimePicker);
        endTimePicker = (TimePicker) findViewById(R.id.recordManualEntryEndTimePicker);

        map = new HashMap<String, List<String>>();
        categoriesList = retrieveCategories();

        ArrayAdapter<String> categoriesAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categoriesList);
        categoriesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categoriesSpinner.setAdapter(categoriesAdapter);

        setActivitiesSpinner();

        categoriesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                setActivitiesSpinner();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void setActivitiesSpinner() {
        activitiesList = retrieveActivitiesForCurrentCategory();

        ArrayAdapter<String> activitiesAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, activitiesList);
        activitiesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        activitiesSpinner.setAdapter(activitiesAdapter);
    }

    private List<String> retrieveActivitiesForCurrentCategory() {
        String category = String.valueOf(categoriesSpinner.getSelectedItem());
        return map.get(category);
    }

    public void onClickAddEntry(View view) {
        String category = String.valueOf(categoriesSpinner.getSelectedItem());
        String activity = String.valueOf(activitiesSpinner.getSelectedItem());
        String startTime = getTimeInMillis(startTimePicker.getCurrentHour(), startTimePicker.getCurrentMinute());
        String endTime = getTimeInMillis(endTimePicker.getCurrentHour(), endTimePicker.getCurrentMinute());

        long startTimeInMillis = Long.valueOf(startTime).longValue();
        long endTimeInMillis = Long.valueOf(endTime).longValue();

        if ((endTimeInMillis - startTimeInMillis) > 0) {
            initializeLocationProperties(startTimeInMillis, endTimeInMillis);
            writeEntryIntoFile(category, activity, startTime, endTime);
            finish();
        } else {
            Toast toast = Toast.makeText(context, "Start Time Cannot Be Greater Than\nEnd Time", Toast.LENGTH_SHORT);
            TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
            if( v != null) v.setGravity(Gravity.CENTER);
            toast.show();
        }
    }

    private void initializeLocationProperties(long startTime, long endTime) {
        String next[] = {};
        long timestamp;

        File locationsDirectory = new File(Environment.getExternalStorageDirectory() + "/.logit/");
        if (!locationsDirectory.exists()) {
            locationsDirectory.mkdirs();
        }
        File locationFile = new File(Environment.getExternalStorageDirectory() + "/.logit/locations/" + date + ".csv");
        try {
            CSVReader mActivityReader = new CSVReader(new FileReader(locationFile));
            while (true) {
                next = mActivityReader.readNext();
                if (next != null) {
                    timestamp = convertIntoMillisFormat(next[0].split(" ")[1]);
                    if (timestamp >= startTime && timestamp <= endTime) {
                        latitude = next[1];
                        longitude = next[2];
                    }
                } else {
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private long convertIntoMillisFormat(String time) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        Date date = null;
        try {
            date = sdf.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date.getTime();
    }

    private void writeEntryIntoFile(String category, String activity, String startTime, String endTime) {
        File entriesDirectory = new File(Environment.getExternalStorageDirectory() + "/.logit/");
        if (!entriesDirectory.exists()) {
            entriesDirectory.mkdirs();
        }
        CSVWriter mEntryWriter = null;

        File entryFile = new File(Environment.getExternalStorageDirectory() + "/.logit/activities/" + date + ".csv");
        try {
            mEntryWriter = new CSVWriter(new FileWriter(entryFile, true));

            String entry[] = new String[6];

            entry[0] = category;
            entry[1] = activity;
            entry[2] = startTime;
            entry[3] = endTime;
            entry[4] = latitude;
            entry[5] = longitude;
            mEntryWriter.writeNext(entry);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (mEntryWriter != null) {
                try {
                    mEntryWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        Toast.makeText(context, "Entry added successfully!", Toast.LENGTH_SHORT).show();
    }

    private String getTimeInMillis(Integer currentHour, Integer currentMinute) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        String time = currentHour + ":" + currentMinute;
        String timeInMillis = "";

        try {
            Date date = sdf.parse(time);
            timeInMillis = String.valueOf(date.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return timeInMillis;
    }

    private List<String> retrieveCategories() {
        List<String> categories = new ArrayList<String>();
        List<String> activities = new ArrayList<String>();
        String next[] = {};
        String category = "";
        String activity = "";
        File activityDirectory = new File(Environment.getExternalStorageDirectory() + "/.logit/");
        if (!activityDirectory.exists()) {
            activityDirectory.mkdirs();
        }
        File activityFile = new File(Environment.getExternalStorageDirectory() + "/.logit/defaultActivities.csv");
        try {
            CSVReader mActivityReader = new CSVReader(new FileReader(activityFile));
            while (true) {
                next = mActivityReader.readNext();
                if (next != null) {
                    category = next[0];
                    activity = parseStringAsActivity(next);
                    createMappingInHashMap(category, activity);
                    if (!categories.contains(category.trim())) {
                        categories.add(category);
                    }
                } else {
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return categories;
    }

    private String parseStringAsActivity(String[] next) {
        String activity = "";
        activity = activity + next[1];
        if (next[2] != "") {
            activity = activity + " " + next[2];
        }
        if (next[3] != "") {
            activity = activity + " " + next[3];
        }
        return activity;
    }

    private void createMappingInHashMap(String category, String activity) {
        List<String> activitiesFromMap = map.get(category);
        if (activitiesFromMap == null) {
            List<String> activitiesList = new ArrayList<String>();
            activitiesList.add(activity);
            map.put(category, activitiesList);
        } else {
            if (!activitiesFromMap.contains(activity)) {
                activitiesFromMap.add(activity);
                map.put(category, activitiesFromMap);
            }
        }
    }

    public void onClickBack(View view) {
        finish();
    }
}
