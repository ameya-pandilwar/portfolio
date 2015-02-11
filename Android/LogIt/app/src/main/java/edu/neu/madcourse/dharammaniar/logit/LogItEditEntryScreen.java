package edu.neu.madcourse.dharammaniar.logit;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
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
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;
import edu.neu.madcourse.dharammaniar.R;

/**
 * Created by Ameya on 06-12-2014.
 */
public class LogItEditEntryScreen extends Activity {

    Context context;

    private Spinner categoriesSpinner;
    private EditText activityEditText;
    private TimePicker startTimePicker, endTimePicker;
    List<String> categoriesList;

    List<LogItEntry> entries;

    String currentCategoryString, currentActivity, currentStartTime, currentEndTime, date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_it_edit_entry_screen);

        context = this;

        Intent intent = getIntent();
        currentCategoryString = intent.getStringExtra("entry-category");
        currentActivity = intent.getStringExtra("entry-activity");
        currentStartTime = intent.getStringExtra("entry-starttime");
        currentEndTime = intent.getStringExtra("entry-endtime");
        date = intent.getStringExtra("entry-date");

        categoriesSpinner = (Spinner) findViewById(R.id.editEntryCategory);
        activityEditText = (EditText) findViewById(R.id.editEntryActivity);
        startTimePicker = (TimePicker) findViewById(R.id.editEntryStartTime);
        endTimePicker = (TimePicker) findViewById(R.id.editEntryEndTime);

        categoriesList = retrieveCategories();

        entries = loadEntriesForDate(date);

        activityEditText.setText(currentActivity);
        setStartEndTimes(currentStartTime, currentEndTime);

        ArrayAdapter<String> categoriesAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categoriesList);
        categoriesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categoriesSpinner.setAdapter(categoriesAdapter);
    }

    private List<LogItEntry> loadEntriesForDate(String date) {
        List<LogItEntry> currentEntries = new ArrayList<LogItEntry>();
        String next[] = {};
        File activityDirectory = new File(Environment.getExternalStorageDirectory() + "/.logit/");
        if (!activityDirectory.exists()) {
            activityDirectory.mkdirs();
        }
        File activityFile = new File(Environment.getExternalStorageDirectory() + "/.logit/activities/"+date+".csv");
        try {
            CSVReader mActivityReader = new CSVReader(new FileReader(activityFile));
            while(true) {
                next = mActivityReader.readNext();
                if (next != null) {
                    LogItEntry entry = new LogItEntry();
                    entry.setCategory(chooseCategoryImage(next[0]));
                    entry.setActivity(next[1]);
                    entry.setStartTime(next[2]);
                    entry.setEndTime(next[3]);
                    entry.setLatitude(Double.valueOf(next[4]));
                    entry.setLongitude(Double.valueOf(next[5]));
                    entry.setDate(date);
                    entry.setCategoryString(next[0]);
                    currentEntries.add(entry);
                } else {
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return currentEntries;
    }

    private String parseMillisToDate(Long timeInMillis) {
        Date date = new Date(timeInMillis);
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.US);
        return sdf.format(date);
    }

    private Drawable chooseCategoryImage(String category) {
        Drawable categoryImage = getResources().getDrawable(R.drawable.ic_launcher_logit);
        return categoryImage;
    }

    private void setStartEndTimes(String startTime, String endTime) {
        startTimePicker.setCurrentHour(Integer.valueOf(startTime.split(":")[0]));
        startTimePicker.setCurrentMinute(Integer.valueOf(startTime.split(":")[1]));

        endTimePicker.setCurrentHour(Integer.valueOf(endTime.split(":")[0]));
        endTimePicker.setCurrentMinute(Integer.valueOf(endTime.split(":")[1]));
    }

    public void onClickSave(View view) {
        String updatedCategoryString = String.valueOf(categoriesSpinner.getSelectedItem());
        String updatedActivity = activityEditText.getText().toString();
        String updatedStartTime = getTimeInMillis(startTimePicker.getCurrentHour(), startTimePicker.getCurrentMinute());
        String updatedEndTime = getTimeInMillis(endTimePicker.getCurrentHour(), endTimePicker.getCurrentMinute());

        long startTimeInMillis = Long.valueOf(updatedStartTime).longValue();
        long endTimeInMillis = Long.valueOf(updatedEndTime).longValue();

        if ((endTimeInMillis - startTimeInMillis) > 0) {
            int entriesCount = entries.size();
            LogItEntry entry = null;
            int i = 0;
            for (i = 0; i < entriesCount; i++) {
                entry = entries.get(i);
                if (entry.getCategoryString().equalsIgnoreCase(currentCategoryString)) {
                    if (entry.getActivity().equalsIgnoreCase(currentActivity)) {
                        if (entry.getStartTime().equalsIgnoreCase(parseTimeInMillisFormat(currentStartTime))
                                && entry.getEndTime().equalsIgnoreCase(parseTimeInMillisFormat(currentEndTime))) {
                            break;
                        }
                    }
                }
            }

            LogItEntry updatedEntry = new LogItEntry();
            updatedEntry.setCategory(entry.getCategory());
            updatedEntry.setActivity(updatedActivity);
            updatedEntry.setStartTime(updatedStartTime);
            updatedEntry.setEndTime(updatedEndTime);
            updatedEntry.setLatitude(entry.getLatitude());
            updatedEntry.setLongitude(entry.getLongitude());
            updatedEntry.setCategoryString(updatedCategoryString);
            updatedEntry.setDate(entry.getDate());

            if (i < entriesCount) {
                entries.remove(i);
                entries.add(i, updatedEntry);

                writeEntryIntoFile(entries, entry.getDate());
                Toast.makeText(context, "Entry Updated Successfully!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "No Matching Entries Found!", Toast.LENGTH_SHORT).show();
            }
            finish();
        } else {
            Toast toast = Toast.makeText(context, "Start Time Cannot Be Greater Than\nEnd Time", Toast.LENGTH_SHORT);
            TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
            if( v != null) v.setGravity(Gravity.CENTER);
            toast.show();
        }
    }

    private String parseTimeInMillisFormat(String time) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.US);
        Date dateForMillisFormat = null;
        try {
            dateForMillisFormat = sdf.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return String.valueOf(dateForMillisFormat.getTime());
    }

    private void writeEntryIntoFile(List<LogItEntry> entries, String date) {
        File entriesDirectory = new File(Environment.getExternalStorageDirectory() + "/.logit/");
        if (!entriesDirectory.exists()) {
            entriesDirectory.mkdirs();
        }
        CSVWriter mEntryWriter = null;

        File entryFile = new File(Environment.getExternalStorageDirectory() + "/.logit/activities/"+date+".csv");
        try {
            mEntryWriter = new CSVWriter(new FileWriter(entryFile, false));
            String[] entryString = new String[6];

            for (LogItEntry entry : entries) {
                entryString[0] = entry.getCategoryString();
                entryString[1] = entry.getActivity();
                entryString[2] = entry.getStartTime();
                entryString[3] = entry.getEndTime();
                entryString[4] = String.valueOf(entry.getLatitude());
                entryString[5] = String.valueOf(entry.getLongitude());
                mEntryWriter.writeNext(entryString);
            }

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
            while(true) {
                next = mActivityReader.readNext();
                if (next != null) {
                    category = next[0];
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

    public void onClickBack(View view) {
        finish();
    }
}