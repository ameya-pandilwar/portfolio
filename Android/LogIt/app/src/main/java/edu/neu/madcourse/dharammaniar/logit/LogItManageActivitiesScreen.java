package edu.neu.madcourse.dharammaniar.logit;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ListView;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import au.com.bytecode.opencsv.CSVReader;
import edu.neu.madcourse.dharammaniar.R;

/**
 * Created by Ameya on 26-11-2014.
 */
public class LogItManageActivitiesScreen extends Activity {

    Context context;

    private DatePicker datePicker;
    private ListView entriesListView;

    List<LogItEntry> listOfLogItEntry;
    String date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_it_manage_activities_screen);

        context = this;

        datePicker = (DatePicker) findViewById(R.id.manageActivitiesDatePicker);
        entriesListView = (ListView) findViewById(R.id.manageActivitiesListView);

        int year = datePicker.getYear();
        int monthOfYear = datePicker.getMonth();
        int dayOfMonth = datePicker.getDayOfMonth();

        displayEntriesForDate(dayOfMonth, monthOfYear, year);

        datePicker.init(year, monthOfYear, dayOfMonth, new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                displayEntriesForDate(dayOfMonth, monthOfYear, year);
            }
        });
    }

    public void displayEntriesForDate(int dayOfMonth, int monthOfYear, int year) {
        String day, month;
        if (dayOfMonth < 10) {
            day = "0" + String.valueOf(dayOfMonth);
        } else {
            day = String.valueOf(dayOfMonth);
        }
        if ((monthOfYear + 1) < 10) {
            month = "0" + String.valueOf(monthOfYear + 1);
        } else {
            month = String.valueOf(monthOfYear + 1);
        }
        date = day + "-" + month + "-" + String.valueOf(year);
        viewEntries(date);
    }

    public void viewEntries(String date) {
        listOfLogItEntry = fetchEntries(date);

        entriesListView.setVisibility(View.VISIBLE);
        entriesListView.setAdapter(new LogItEntryListAdapter(context, R.layout.log_it_entry, listOfLogItEntry));
    }

    public void onClickBack(View view) {
        finish();
    }

    private List<LogItEntry> fetchEntries(String date) {
        List<LogItEntry> entries = new ArrayList<LogItEntry>();
        String next[] = {};
        File activityDirectory = new File(Environment.getExternalStorageDirectory() + "/.logit/activities/");
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
                    entry.setStartTime(parseMillisToDate(Long.valueOf(next[2])));
                    entry.setEndTime(parseMillisToDate(Long.valueOf(next[3])));
                    entry.setLatitude(Double.valueOf(next[4]));
                    entry.setLongitude(Double.valueOf(next[5]));
                    entry.setDate(date);
                    entry.setCategoryString(next[0]);
                    entry.setTimeSpent(calculateTimeSpent(next[2], next[3]));
                    entries.add(entry);
                } else {
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return entries;
    }

    private String calculateTimeSpent(String startTime, String endTime) {
        long differenceInTime = Long.valueOf(endTime).longValue() - Long.valueOf(startTime).longValue();
        DecimalFormat df = new DecimalFormat("#.##");
        String timeSpent = df.format(differenceInTime / 60000);
        return timeSpent + " minutes";
    }

    private String parseMillisToDate(Long timeInMillis) {
        Date date = new Date(timeInMillis);
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.US);
        return sdf.format(date);
    }

    public void onClickAddNewEntry(View view) {
        Intent intent = new Intent(context, LogItRecordManualEntryScreen.class);
        intent.putExtra("date", date);
        startActivity(intent);
    }

    private Drawable chooseCategoryImage(String category) {
        Drawable categoryImage;
        if (category.equalsIgnoreCase("Family and friends")) {
            categoryImage = getResources().getDrawable(R.drawable.cat_family_and_friends);
        } else if (category.equalsIgnoreCase("Household Chores")) {
            categoryImage = getResources().getDrawable(R.drawable.cat_household_chores);
        } else if (category.equalsIgnoreCase("Leisure")) {
            categoryImage = getResources().getDrawable(R.drawable.cat_leisure);
        } else if (category.equalsIgnoreCase("Miscellaneous")) {
            categoryImage = getResources().getDrawable(R.drawable.cat_miscellaneous);
        } else if (category.equalsIgnoreCase("Necessities")) {
            categoryImage = getResources().getDrawable(R.drawable.cat_necessities);
        } else if (category.equalsIgnoreCase("Physical Exercise")) {
            categoryImage = getResources().getDrawable(R.drawable.cat_physical_exercise);
        } else if (category.equalsIgnoreCase("Religious")) {
            categoryImage = getResources().getDrawable(R.drawable.cat_religious);
        } else if (category.equalsIgnoreCase("Sleep")) {
            categoryImage = getResources().getDrawable(R.drawable.cat_sleep);
        } else if (category.equalsIgnoreCase("Study")) {
            categoryImage = getResources().getDrawable(R.drawable.cat_study);
        } else if (category.equalsIgnoreCase("Travel")) {
            categoryImage = getResources().getDrawable(R.drawable.cat_travel);
        } else if (category.equalsIgnoreCase("Work")) {
            categoryImage = getResources().getDrawable(R.drawable.cat_work);
        } else {
            categoryImage = getResources().getDrawable(R.drawable.cat_unknown);
        }
        return categoryImage;
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        viewEntries(date);
    }
}
