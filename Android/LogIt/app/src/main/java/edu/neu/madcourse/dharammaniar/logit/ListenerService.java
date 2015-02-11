package edu.neu.madcourse.dharammaniar.logit;


import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;
import edu.neu.madcourse.dharammaniar.logit.support.Globals;
import edu.neu.madcourse.dharammaniar.logit.support.Logger;
import edu.neu.madcourse.dharammaniar.logit.support.SharedPrefs;

/**
 * Created by Dharam on 11/20/2014.
 */
public class ListenerService extends WearableListenerService {

    private static final String TAG = "ListenerService";

    private static final String START_ACTIVITY_PATH = "/logit";

    private GoogleApiClient mGoogleApiClient;

    @Override
    public void onCreate() {
        super.onCreate();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        String message = new String(messageEvent.getData());
        Logger.log(TAG, "Message Received = " + message, getApplicationContext());

        if (messageEvent.getPath().equals(START_ACTIVITY_PATH) && (!message.equals("Error"))) {
            if (message.contains("OnGoingActivity")) {
                if (message.equals("OnGoingActivity:Yes")) {
                    SharedPrefs
                            .setBoolean(Globals.KEY_IS_ACTIVITY_ON, true, getApplicationContext());
                    Logger.log(TAG, "OnGoingActivity:Yes", getApplicationContext());
                }
                if (message.equals("OnGoingActivity:No")) {
                    SharedPrefs
                            .setBoolean(Globals.KEY_IS_ACTIVITY_ON, false, getApplicationContext());
                    SharedPrefs.setLong(Globals.KEY_LAST_USER_PROMPT, 0, getApplicationContext());
                    Logger.log(TAG, "OnGoingActivity:No", getApplicationContext());
                    // TODO Activity finished, Save to file
                    writeActivityToFile();
                }
            }
            if (message.contains("UnknownActivity")) {
                if (message.equals("UnknownActivity:Yes")) {
                    SharedPrefs
                            .setBoolean(Globals.KEY_IS_ACTIVITY_ON, true, getApplicationContext());
                    String activity = SharedPrefs
                            .getString(Globals.KEY_CURRENT_ACTIVITY, "", getApplicationContext())
                            .split(":")[1];
                    SharedPrefs.setString(Globals.KEY_CURRENT_ACTIVITY, "1:" + activity,
                                          getApplicationContext());
                    Logger.log(TAG, "UnknownActivity:Yes", getApplicationContext());
                }
                if (message.equals("UnknownActivity:No")) {
                    SharedPrefs
                            .setBoolean(Globals.KEY_IS_ACTIVITY_ON, false, getApplicationContext());
                    SharedPrefs.setLong(Globals.KEY_LAST_USER_PROMPT, 0, getApplicationContext());
                    Logger.log(TAG, "UnknownActivity:No", getApplicationContext());
                    // TODO Activity not recognized, do not save to file.
                }
            }
            if (message.contains("ActivityStart")) {
                if (message.equals("ActivityStart:Yes")) {
                    SharedPrefs.setBoolean(Globals.KEY_IS_ACTIVITY_ON, true,
                                           getApplicationContext());
                }
                if (message.equals("ActivityStart:No")) {
                    SharedPrefs
                            .setBoolean(Globals.KEY_IS_ACTIVITY_ON, false, getApplicationContext());
                    SharedPrefs.setLong(Globals.KEY_LAST_USER_PROMPT, 0, getApplicationContext());
                }
            }
            if (message.contains("TwoActivities")) {
                if (message.equals("TwoActivities:Neither")) {
                    SharedPrefs
                            .setBoolean(Globals.KEY_IS_ACTIVITY_ON, false, getApplicationContext());
                    SharedPrefs.setLong(Globals.KEY_LAST_USER_PROMPT, 0, getApplicationContext());
                    Logger.log(TAG, "TwoActivities:Neither", getApplicationContext());
                } else {
                    SharedPrefs
                            .setString(Globals.KEY_CURRENT_ACTIVITY, "1:" + message.split(":")[1],
                                       getApplicationContext());
                    SharedPrefs
                            .setBoolean(Globals.KEY_IS_ACTIVITY_ON, true, getApplicationContext());
                    Logger.log(TAG, "TwoActivities:" + message.split(":")[1],
                               getApplicationContext());
                }
            }
            if (message.contains("ThreeActivities")) {
                SharedPrefs
                        .setString(Globals.KEY_CURRENT_ACTIVITY, "1:" + message.split(":")[1],
                                   getApplicationContext());
                SharedPrefs
                        .setBoolean(Globals.KEY_IS_ACTIVITY_ON, true, getApplicationContext());
                Logger.log(TAG, "ThreeActivities:" + message.split(":")[1],
                           getApplicationContext());
            }

            if (message.contains("voice:")) {
                List<String> words = new ArrayList<String>();
                List<Float> confidence = new ArrayList<Float>();
                for (int i = 0; i < message.split(":")[1].split("`").length; i++) {
                    words.add(message.split(":")[1].split("`")[i].split(";")[0]);
                    confidence.add(Float.parseFloat(
                            message.split(":")[1].split("`")[i].split(";")[1]));
                }
                String activity = detectActivityFromVoice(words, confidence);
                if (!activity.equals("")) {
                    Logger.log(TAG, "Detected Activity = " + activity, getApplicationContext());
                    SharedPrefs.setString(Globals.KEY_CURRENT_ACTIVITY,
                                          activity,
                                          getApplicationContext());
                    SharedPrefs.setLong(Globals.KEY_CURRENT_ACTIVITY_START_TIME,
                                        (new Date()).getTime(), getApplicationContext());
                    SharedPrefs
                            .setBoolean(Globals.KEY_IS_ACTIVITY_ON, true, getApplicationContext());
                }
            }
            if (message.contains("test-answer")) {
                if (message.equals("test-answer:1")) {
                    SharedPrefs.setBoolean("TEST_QUESTION_ANSWERED", true, getApplicationContext());
                    SharedPrefs.setString("TEST_QUESTION_ANSWER", "having lunch",
                                          getApplicationContext());
                }
                if (message.equals("test-answer:2")) {
                    SharedPrefs.setBoolean("TEST_QUESTION_ANSWERED", true, getApplicationContext());
                    SharedPrefs.setString("TEST_QUESTION_ANSWER", "doing work",
                                          getApplicationContext());
                }
                if (message.equals("test-answer:3")) {
                    SharedPrefs.setBoolean("TEST_QUESTION_ANSWERED", true, getApplicationContext());
                    SharedPrefs
                            .setString("TEST_QUESTION_ANSWER", "neither", getApplicationContext());
                }
            }
            if (message.contains("test-speech")) {
                String voice = message.split(":")[1];
                if (voice.equalsIgnoreCase("having lunch")) {
                    SharedPrefs.setBoolean("TEST_VOICE_ANSWERED", true, getApplicationContext());
                    SharedPrefs
                            .setString("TEST_VOICE_ANSWER", voice, getApplicationContext());
                } else {
                    Toast.makeText(getApplicationContext(),
                                   "You Said - " + voice + ". Please retry.", Toast.LENGTH_SHORT)
                         .show();
                }
            }
        }
    }

    private void writeActivityToFile() {
        try {

            String currentDate = new SimpleDateFormat("dd-MM-yyyy").format(new Date());

            File defaultFile = new File(
                    Environment.getExternalStorageDirectory() +
                    "/.logit/defaultActivities.csv");

            File activityDirectory = new File(
                    Environment.getExternalStorageDirectory() + "/.logit/activities/");

            File activityFile = new File(
                    Environment.getExternalStorageDirectory() + "/.logit/activities/" +
                    currentDate + ".csv");

            File commonActivityFile = new File(
                    Environment.getExternalStorageDirectory() + "/.logit/activities.csv");

            if (!activityDirectory.exists()) {
                activityDirectory.mkdirs();
            }

            CSVWriter activityFileWriter = new CSVWriter(new FileWriter(activityFile, true));
            CSVWriter commonActivityFileWriter = new CSVWriter(
                    new FileWriter(commonActivityFile, true));

            CSVReader mDefaultReader = null;
            mDefaultReader = new CSVReader(new FileReader(defaultFile));
            List<String[]> defaultActivities = mDefaultReader.readAll();

            String[] activity = new String[6];
            String currentActivity = SharedPrefs
                    .getString(Globals.KEY_CURRENT_ACTIVITY, "",
                               getApplicationContext()).split(":")[1];
            String latitude = SharedPrefs
                    .getString(Globals.KEY_CURRENT_ACTIVITY_LATITUDE, "",
                               getApplicationContext());
            String longitude = SharedPrefs
                    .getString(Globals.KEY_CURRENT_ACTIVITY_LONGITUDE, "",
                               getApplicationContext());
            long startTime = SharedPrefs
                    .getLong(Globals.KEY_CURRENT_ACTIVITY_START_TIME, 0,
                             getApplicationContext());
            long endTime = System.currentTimeMillis();

            String category = "Unknown";
            for (int i = 0; i < defaultActivities.size(); i++) {
                if (currentActivity.equals(getActivityFromArray(defaultActivities.get(i)))) {
                    category = defaultActivities.get(i)[0];
                }
            }

            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(startTime);
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.US);
            Date startDate = sdf
                    .parse(cal.get(Calendar.HOUR_OF_DAY) + ":" + cal.get(Calendar.MINUTE));
            cal.setTimeInMillis(endTime);
            Date endDate = sdf
                    .parse(cal.get(Calendar.HOUR_OF_DAY) + ":" + cal.get(Calendar.MINUTE));

            activity[0] = category;
            activity[1] = currentActivity;
            activity[2] = startDate.getTime() + "";
            activity[3] = endDate.getTime() + "";
            activity[4] = latitude;
            activity[5] = longitude;

            activityFileWriter.writeNext(activity);
            activityFileWriter.close();

            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(startTime);

            String activityForDetection[] = new String[4];
            activityForDetection[0] = currentActivity;
            activityForDetection[1] = calendar.get(Calendar.HOUR_OF_DAY) + "";
            activityForDetection[2] = latitude;
            activityForDetection[3] = longitude;

            commonActivityFileWriter.writeNext(activityForDetection);
            commonActivityFileWriter.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private String detectActivityFromVoice(List<String> words, List<Float> confidence) {
        Log.i(TAG, "Inside detectActivityFromVoice");
        Logger.log(TAG, "Detecting activity from voice - " + words.get(0), getApplicationContext());
        String activity = "";
        List<String[]> probableActivities = new ArrayList<String[]>();
        List<String[]> moreProbableActivities = new ArrayList<String[]>();
        File defaultFile = new File(
                Environment.getExternalStorageDirectory() + "/.logit/defaultActivities.csv");
        if ((!words.isEmpty()) && (words.get(0).split(" ").length < 10)) {
            try {
                String voice = words.get(0).toLowerCase();

                CSVReader csvFile = new CSVReader(new FileReader(defaultFile));
                String nextLine[];
                while ((nextLine = csvFile.readNext()) != null) {
                    if (!nextLine[3].equals("")) {
                        if (voice.contains(nextLine[3])) {
                            probableActivities.add(nextLine);
                        }
                    }
                }
                csvFile.close();
                // Only one matching activity. Best case scenario.
                if (probableActivities.size() == 1) {
                    activity = "1:" + getActivityFromArray(probableActivities.get(0));
                    Logger.log(TAG, "One probable activity = " + activity, getApplicationContext());
                } else {
                    // For more than one matching specific activities
                    if (probableActivities.size() > 1) {

                        for (int j = 0; j < probableActivities.size(); j++) {
                            String action = probableActivities.get(j)[1]
                                    .substring(0, probableActivities.get(j)[1].length() - 3);
                            Log.i(TAG, "Action = " + action);
                            if (voice.contains(action)) {
                                moreProbableActivities.add(probableActivities.get(j));
                            }
                        }
                        // Only one matching probable activity. Second best case scenario
                        if (moreProbableActivities.size() == 1) {
                            activity =
                                    "1:" + getActivityFromArray(moreProbableActivities.get(0));
                            Logger.log(TAG, "One moreProbable activity = " + activity,
                                       getApplicationContext());
                        } else {
                            // More than one more probable activity. Shouldn't usually be the case
                            if (moreProbableActivities.size() > 1) {
                                if (moreProbableActivities.size() == 2) {
                                    activity = "2:" +
                                               getActivityFromArray(moreProbableActivities.get(0)) +
                                               "`" +
                                               getActivityFromArray(moreProbableActivities.get(1));
                                    Logger.log(TAG, "Two moreProbable activities = " + activity,
                                               getApplicationContext());
                                } else {
                                    if (moreProbableActivities.size() == 3) {
                                        activity = "3:" + getActivityFromArray(
                                                moreProbableActivities.get(0)) +
                                                   "`" +
                                                   getActivityFromArray(
                                                           moreProbableActivities.get(1)) +
                                                   "`" +
                                                   getActivityFromArray(
                                                           moreProbableActivities.get(2));
                                        Logger.log(TAG,
                                                   "Three moreProbable activities = " + activity,
                                                   getApplicationContext());
                                    } else {
                                        Logger.log(TAG, "More than 3 moreProbable activities",
                                                   getApplicationContext());
                                        activity = "Unknown:" + getActivityFromVoice(voice);
                                    }
                                }
                            } else {
                                if (moreProbableActivities.size() == 0) {
                                    if (probableActivities.size() == 2) {
                                        activity = "2:" +
                                                   getActivityFromArray(probableActivities.get(0)) +
                                                   "`" +
                                                   getActivityFromArray(probableActivities.get(1));
                                        Logger.log(TAG,
                                                   "Two probableActivities with zero moreProbable activities = " +
                                                   activity,
                                                   getApplicationContext());
                                    } else {
                                        if (probableActivities.size() == 3) {
                                            activity = "3:" +
                                                       getActivityFromArray(
                                                               probableActivities.get(0)) +
                                                       "`" +
                                                       getActivityFromArray(
                                                               probableActivities.get(1)) +
                                                       "`" +
                                                       getActivityFromArray(
                                                               probableActivities.get(2));
                                            Logger.log(TAG,
                                                       "Three probable activities with zero moreProbable activities = " +
                                                       activity,
                                                       getApplicationContext());
                                        } else {
                                            if (probableActivities.size() > 3) {
                                                Logger.log(TAG,
                                                           "More than 3 probable activities with zero moreProbable activities",
                                                           getApplicationContext());
                                                activity = "Unknown:" +
                                                           getActivityFromVoice(voice);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        // No probable activities on matching specific activities. Need to
                        // check on the action verb now
                        if (probableActivities.size() == 0) {

                            csvFile = new CSVReader(new FileReader(defaultFile));
                            while ((nextLine = csvFile.readNext()) != null) {
                                if (!nextLine[1].equals("")) {
                                    if ((voice.contains(nextLine[1])) &&
                                        (nextLine[3].equals(""))) {
                                        probableActivities.add(nextLine);
                                    }
                                }
                            }
                            csvFile.close();

                            // Found one activity based on verb. This is also a good case
                            if (probableActivities.size() == 1) {
                                activity =
                                        "1:" + getActivityFromArray(probableActivities.get(0));
                                Logger.log(TAG, "Verb - One probable activity - " + activity,
                                           getApplicationContext());
                            } else {
                                if (probableActivities.size() == 0) {
                                    Logger.log(TAG, "Verb - No probable activities. ",
                                               getApplicationContext());
                                    activity = "Unknown:" + getActivityFromVoice(voice);
                                } else {
                                    if (probableActivities.size() == 2) {
                                        activity = "2:" +
                                                   getActivityFromArray(probableActivities.get(0)) +
                                                   "`" +
                                                   getActivityFromArray(probableActivities.get(1));
                                        Logger.log(TAG,
                                                   "Verb - Two probable activities = " + activity,
                                                   getApplicationContext());
                                    } else {
                                        if (probableActivities.size() == 3) {
                                            activity = "3:" +
                                                       getActivityFromArray(
                                                               probableActivities.get(0)) +
                                                       "`" +
                                                       getActivityFromArray(
                                                               probableActivities.get(1)) +
                                                       "`" +
                                                       getActivityFromArray(
                                                               probableActivities.get(2));
                                            Logger.log(TAG,
                                                       "Verb - Three probable activities = " +
                                                       activity,
                                                       getApplicationContext());
                                        } else {
                                            if (probableActivities.size() > 3) {
                                                Logger.log(TAG,
                                                           "Verb - More than 3 probable activities.",
                                                           getApplicationContext());
                                                activity = "Unknown:" +
                                                           getActivityFromVoice(voice);
                                            }
                                        }
                                    }
                                }
                            }
                        }

                    }
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return activity;
    }

    private String getActivityFromVoice(String voice) {
        String activity = "";

        if (voice.contains("ing")) {
            String[] words = voice.split(" ");
            boolean foundVerb = false;
            for (int i = 0; i < words.length; i++) {
                if (words[i].contains("ing")) {
                    foundVerb = true;
                }
                if (foundVerb) {
                    activity = activity + words[i] + " ";
                }
            }
            activity = activity.trim();
        } else {
            activity = voice;
        }

        return activity;
    }

    private String getActivityFromArray(String[] strings) {
        String activity = "";

        activity = activity + strings[1];
        if (!strings[2].equals("")) {
            activity = activity + " " + strings[2];
        }
        if (!strings[3].equals("")) {
            activity = activity + " " + strings[3];
        }
        return activity;
    }
}

