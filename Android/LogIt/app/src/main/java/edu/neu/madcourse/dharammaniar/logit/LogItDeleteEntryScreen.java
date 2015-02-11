package edu.neu.madcourse.dharammaniar.logit;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;
import edu.neu.madcourse.dharammaniar.R;

/**
 * Created by Ameya on 06-12-2014.
 */
public class LogItDeleteEntryScreen extends Activity {

    Context context;

    List<LogItEntry> entries;

    String category, activity, startTime, endTime, date;
    TextView categoryTextView, activityTextView, startTimeTextView, endTimeTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_it_delete_entry_screen);

        context = this;

        Intent intent = getIntent();
        category = intent.getStringExtra("entry-category");
        activity = intent.getStringExtra("entry-activity");
        startTime = intent.getStringExtra("entry-starttime");
        endTime = intent.getStringExtra("entry-endtime");
        date = intent.getStringExtra("entry-date");

        categoryTextView = (TextView) findViewById(R.id.deleteEntryCategory);
        activityTextView = (TextView) findViewById(R.id.deleteEntryActivity);
        startTimeTextView = (TextView) findViewById(R.id.deleteEntryStartTime);
        endTimeTextView = (TextView) findViewById(R.id.deleteEntryEndTime);

        categoryTextView.setText(category);
        activityTextView.setText(activity);
        startTimeTextView.setText(startTime);
        endTimeTextView.setText(endTime);

        entries = loadEntriesForDate(date);
    }

    private List<LogItEntry> loadEntriesForDate(String date) {
        List<LogItEntry> currentEntries = new ArrayList<LogItEntry>();
        String next[] = {};
        File activityDirectory = new File(Environment.getExternalStorageDirectory() + "/.logit/");
        if (!activityDirectory.exists()) {
            activityDirectory.mkdirs();
        }
        File activityFile = new File(Environment.getExternalStorageDirectory() + "/.logit/activities/" + date + ".csv");
        try {
            CSVReader mActivityReader = new CSVReader(new FileReader(activityFile));
            while (true) {
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

    private Drawable chooseCategoryImage(String category) {
        Drawable categoryImage = getResources().getDrawable(R.drawable.ic_launcher_logit);
        return categoryImage;
    }

    public void onClickNo(View view) {
        finish();
    }

    public void onClickYes(View view) {
        List<LogItEntry> entries = loadEntriesForDate(date);
        int entriesCount = entries.size();
        LogItEntry entry = null;
        int i = 0;
        for (i = 0; i < entriesCount; i++) {
            entry = entries.get(i);
            if (entry.getCategoryString().equalsIgnoreCase(category)) {
                if (entry.getActivity().equalsIgnoreCase(activity)) {
                    if (entry.getStartTime().equalsIgnoreCase(parseTimeInMillisFormat(startTime))
                            && entry.getEndTime().equalsIgnoreCase(parseTimeInMillisFormat(endTime))) {
                        break;
                    }
                }
            }
        }

        if (i < entriesCount) {
            entries.remove(i);
            writeEntryIntoFile(entries, entry.getDate());
            Toast.makeText(context, "Entry Deleted Successfully!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "No Matching Entries Found!", Toast.LENGTH_SHORT).show();
        }
        finish();
    }

    private void writeEntryIntoFile(List<LogItEntry> entries, String date) {
        File entriesDirectory = new File(Environment.getExternalStorageDirectory() + "/.logit/");
        if (!entriesDirectory.exists()) {
            entriesDirectory.mkdirs();
        }
        CSVWriter mEntryWriter = null;

        File entryFile = new File(
                Environment.getExternalStorageDirectory() + "/.logit/activities/" + date + ".csv");
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
}