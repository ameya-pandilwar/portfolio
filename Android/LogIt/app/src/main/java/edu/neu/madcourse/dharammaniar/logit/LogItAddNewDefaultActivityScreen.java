package edu.neu.madcourse.dharammaniar.logit;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;
import edu.neu.madcourse.dharammaniar.R;

/**
 * Created by Ameya on 04-12-2014.
 */
public class LogItAddNewDefaultActivityScreen extends Activity {

    Context context;

    private Spinner activitiesSpinner;
    private EditText activitiesEditText;
    List<String> activitiesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_it_add_new_default_activity_screen);

        context = this;

        activitiesSpinner = (Spinner) findViewById(R.id.addNewDefaultActivitySpinner);
        activitiesEditText = (EditText) findViewById(R.id.addNewDefaultActivityEditText);
        activitiesList = retrieveDefaultActivities();

        if (activitiesList.size() > 0) {
            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, activitiesList);
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            activitiesSpinner.setAdapter(dataAdapter);
        } else {
            Toast.makeText(context, "No Categories Available To Choose From", Toast.LENGTH_SHORT).show();
        }
    }

    public void onClickAddNewActivity(View view) {
        if (activitiesList.size() > 0) {
            String activity = activitiesEditText.getText().toString();
            boolean easyToUnderstand = isActivityEasyToUnderstand(activity);
            if (easyToUnderstand) {
                if (!activity.equalsIgnoreCase("")) {
                    String category = String.valueOf(activitiesSpinner.getSelectedItem());
                    writeNewActivityIntoFile(category, activity);
                    Toast toast = Toast.makeText(context, "The new activity has been added\nunder the '" + category + "' category!", Toast.LENGTH_SHORT);
                    TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
                    if (v != null) v.setGravity(Gravity.CENTER);
                    toast.show();
                    finish();
                } else {
                    Toast.makeText(context, String.valueOf("The new activity cannot be blank."), Toast.LENGTH_SHORT).show();
                }
            } else {
                String msg = "Enter activity using 3-4 words.\nSee above examples for reference.";
                Toast toast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
                TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
                if (v != null) v.setGravity(Gravity.CENTER);
                toast.show();
            }
        } else {
            Toast.makeText(context, "Need To Select A Category For The Activity", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isActivityEasyToUnderstand(String activity) {
        String[] activitySplit = activity.split(" ");
        if (activitySplit.length < 5) {
            return true;
        } else {
            return false;
        }
    }

    private void writeNewActivityIntoFile(String category, String activity) {

        File activityDirectory = new File(Environment.getExternalStorageDirectory() + "/.logit/");
        if (!activityDirectory.exists()) {
            activityDirectory.mkdirs();
        }
        CSVWriter mActivityWriter = null;
        File activityFile = new File(Environment.getExternalStorageDirectory() + "/.logit/defaultActivities.csv");
        try {
            mActivityWriter = new CSVWriter(new FileWriter(activityFile, true));

            String entry[] = new String[4];

            entry[0] = category;
            entry[1] = retrieveInformationFromActivity(activity, "verb");
            entry[2] = retrieveInformationFromActivity(activity, "connector");
            entry[3] = retrieveInformationFromActivity(activity, "last");
            mActivityWriter.writeNext(entry);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (mActivityWriter != null) {
                try {
                    mActivityWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private String retrieveInformationFromActivity(String activity, String task) {
        String[] words = activity.split(" ");
        if (task.equalsIgnoreCase("verb")) {
            return words[0];
        } else if (task.equalsIgnoreCase("connector")) {
            String connector = "";
            for (int i = 1; i < (words.length - 1); i++) {
                if (i == (words.length - 1)) {
                    break;
                } else {
                    connector = connector.concat(" " + words[i]);
                }
            }
            return connector.trim();
        } else if (task.equalsIgnoreCase("last")) {
            return words[words.length - 1];
        } else {
            return "";
        }
    }

    public void onClickBack(View view) {
        finish();
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
                    activity = next[0];
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
}
