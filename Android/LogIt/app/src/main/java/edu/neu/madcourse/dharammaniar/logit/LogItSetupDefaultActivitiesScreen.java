package edu.neu.madcourse.dharammaniar.logit;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import au.com.bytecode.opencsv.CSVReader;
import edu.neu.madcourse.dharammaniar.R;

/**
 * Created by Ameya on 26-11-2014.
 */
public class LogItSetupDefaultActivitiesScreen extends Activity {

    Context context;

    private ListView setupDefaultActivitiesList;
    List<String> activitiesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_it_setup_default_activities_screen);

        context = this;

        setupDefaultActivitiesList = (ListView) findViewById(R.id.defaultActivitiesList);
        displayRetrievedActivitiesList();
    }

    private void displayRetrievedActivitiesList() {
        activitiesList = retrieveDefaultActivities();
        if (activitiesList.size() > 0) {
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, activitiesList) {
                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    View view = super.getView(position, convertView, parent);
                    TextView textView = (TextView) view.findViewById(android.R.id.text1);
                    textView.setTextColor(Color.BLACK);
                    return view;
                }
            };
            setupDefaultActivitiesList.setAdapter(adapter);
        } else {
            Toast.makeText(context, "No Default Activities Found", Toast.LENGTH_SHORT).show();
        }
    }

    public void onClickBack(View view) {
        finish();
    }

    public void onClickAddNewActivity(View view) {
        Intent intent = new Intent(context, LogItAddNewDefaultActivityScreen.class);
        startActivity(intent);
    }

    private List<String> retrieveDefaultActivities() {
        List<String> activities = new ArrayList<String>();
        String next[] = {};
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
                    activity = parseStringAsActivity(next);
                    if (!activities.contains(activity.trim())) {
                        activities.add(activity);
                    }
                } else {
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return activities;
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

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        displayRetrievedActivitiesList();
    }
}
