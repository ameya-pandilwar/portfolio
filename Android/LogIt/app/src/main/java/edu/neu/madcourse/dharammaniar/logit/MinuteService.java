package edu.neu.madcourse.dharammaniar.logit;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;
import edu.neu.madcourse.dharammaniar.R;
import edu.neu.madcourse.dharammaniar.logit.support.Globals;
import edu.neu.madcourse.dharammaniar.logit.support.Logger;
import edu.neu.madcourse.dharammaniar.logit.support.SharedPrefs;

/**
 * Created by Dharam on 11/25/2014.
 */
public class MinuteService extends Service implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    public static final long[] VIBRATE_INTENSE = {1000, 200, 1000, 200, 500, 250, 500, 250, 500,
            250, 500, 250, 250, 250, 250, 250, 250, 250, 250, 250, 250, 100, 100, 100, 100, 100,
            100, 100, 1000, 2000, 1000, 200, 1000, 200, 500, 250, 500, 250, 500,
            250, 500, 250, 250, 250, 250, 250, 250, 250, 250, 250, 250, 100, 100, 100, 100, 100,
            100, 100, 1000, 2000};
    private static final String TAG = "MinuteService";
    private static final String START_ACTIVITY_PATH = "/logit";
    SimpleDateFormat time = new SimpleDateFormat("HH:mm");
    SimpleDateFormat timestamp = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
    private int notificationID = 10000;
    private int serviceNotificationID = 10001;
    private LocationClient mLocationClient;
    private Date currentTime;
    private Boolean locationEnabled;
    private GooglePlayServicesClient.ConnectionCallbacks servicesClient1 = new GooglePlayServicesClient.ConnectionCallbacks() {
        @Override
        public void onDisconnected() {
        }

        @Override
        public void onConnected(Bundle connectionHint) {
            if (locationEnabled) {
                getLocation();
            } else {
                generateDefaultActivities();
            }
        }
    };
    private GoogleApiClient mGoogleApiClient;
    private boolean mResolvingError = false;
    private GooglePlayServicesClient.OnConnectionFailedListener servicesClient2 = new GooglePlayServicesClient.OnConnectionFailedListener() {
        @Override
        public void onConnectionFailed(ConnectionResult result) {
        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        currentTime = new Date();

        super.onCreate();
        Logger.log(TAG, "MinuteService started", getApplicationContext());

        showServiceNotification();

        LocationManager manager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER) &&
            !manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            locationEnabled = false;
            Toast.makeText(getApplicationContext(),
                           "LogIt: Enable location services for accurate data",
                           Toast.LENGTH_SHORT).show();
        } else {
            mLocationClient = new LocationClient(this, servicesClient1, servicesClient2);
            mLocationClient.connect();
            locationEnabled = true;
        }

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API).addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).build();
        if (!mResolvingError) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mLocationClient.disconnect();

        Logger.log(TAG, "MinuteService stopped", getApplicationContext());
    }

    private void getLocation() {
        String currentDate = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
        File locationDirectory = new File(
                Environment.getExternalStorageDirectory() + "/.logit/locations/");
        if (!locationDirectory.exists()) {
            locationDirectory.mkdirs();
        }
        File locationFile = new File(
                Environment.getExternalStorageDirectory() + "/.logit/locations/" + currentDate +
                ".csv");
        try {
            CSVWriter mLocationWriter = new CSVWriter(new FileWriter(locationFile, true));
            CSVReader mLocationReader = new CSVReader(new FileReader(locationFile));
            List<String[]> locations = mLocationReader.readAll();
            String answer[] = new String[4];
            Location mCurrentLocation = mLocationClient.getLastLocation();
            Log.d(TAG,
                  mCurrentLocation.getLatitude() + "," + mCurrentLocation.getLongitude() + "," +
                  mCurrentLocation.getAccuracy());

            if (mCurrentLocation.getAccuracy() > 50.0f) {
                answer[0] = timestamp.format(currentTime);
                answer[1] = mCurrentLocation.getLatitude() + "";
                answer[2] = mCurrentLocation.getLongitude() + "";
                answer[3] = mCurrentLocation.getAccuracy() + "";
                mLocationWriter.writeNext(answer);
                SharedPrefs.setString(Globals.KEY_CURRENT_LATITUDE,
                                      mCurrentLocation.getLatitude() + "", getApplicationContext());
                SharedPrefs.setString(Globals.KEY_CURRENT_LONGITUDE,
                                      mCurrentLocation.getLongitude() + "",
                                      getApplicationContext());
            }

            if ((SharedPrefs.getLong(Globals.KEY_CURRENT_ACTIVITY_START_TIME,
                                     System.currentTimeMillis(), getApplicationContext()) +
                 Globals.FIVE_MIN_IN_MS) > System.currentTimeMillis()) {
                if (mCurrentLocation.getAccuracy() > 50.0f) {
                    SharedPrefs.setString(Globals.KEY_CURRENT_ACTIVITY_LATITUDE,
                                          mCurrentLocation.getLatitude() + "",
                                          getApplicationContext());
                    SharedPrefs.setString(Globals.KEY_CURRENT_ACTIVITY_LONGITUDE,
                                          mCurrentLocation.getLongitude() + "",
                                          getApplicationContext());
                } else {
                    if (!locations.isEmpty()) {
                        SharedPrefs.setString(Globals.KEY_CURRENT_ACTIVITY_LATITUDE,
                                              locations.get(locations.size() - 1)[1],
                                              getApplicationContext());
                        SharedPrefs.setString(Globals.KEY_CURRENT_ACTIVITY_LONGITUDE,
                                              locations.get(locations.size() - 1)[2],
                                              getApplicationContext());
                    } else {
                        SharedPrefs.setString(Globals.KEY_CURRENT_ACTIVITY_LATITUDE,
                                              "0",
                                              getApplicationContext());
                        SharedPrefs.setString(Globals.KEY_CURRENT_ACTIVITY_LONGITUDE,
                                              "0",
                                              getApplicationContext());

                    }
                }
            }
            mLocationWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        generateDefaultActivities();
    }

    private void promptIfRequired() {
        Logger.log(TAG, "Inside promptIfRequired", getApplicationContext());
        long lastUserPromptTime = SharedPrefs
                .getLong(Globals.KEY_LAST_USER_PROMPT, 0, getApplicationContext());
        Date promptTime = new Date();
        promptTime.setTime(lastUserPromptTime);
        Logger.log(TAG, "Previous Prompt Time = " + promptTime.toString(), getApplicationContext());
        Date currentTimeToPrint = new Date();
        currentTimeToPrint.setTime(System.currentTimeMillis());
        Logger.log(TAG, "Current Time = " + currentTimeToPrint.toString(), getApplicationContext());
        Logger.log(TAG, "Current Activity = " + SharedPrefs
                           .getString(Globals.KEY_CURRENT_ACTIVITY, "", getApplicationContext()),
                   getApplicationContext());

        if (lastUserPromptTime != 0) {
            Logger.log(TAG, "Inside lastUserPromptTime != 0", getApplicationContext());
            boolean isActivityOn = SharedPrefs
                    .getBoolean(Globals.KEY_IS_ACTIVITY_ON, false, getApplicationContext());

            if (isActivityOn) {
                Logger.log(TAG, "Inside isActivityOn", getApplicationContext());
                String activity = SharedPrefs
                        .getString(Globals.KEY_CURRENT_ACTIVITY, "", getApplicationContext());
                if (activity.split(":")[0].equals("1")) {

                    if ((lastUserPromptTime + Globals.FIFTEEN_MIN_IN_MS) <
                        System.currentTimeMillis()) {
                        Logger.log(TAG,
                                   "1 activity, 15 minutes passed",
                                   getApplicationContext());
                        String currentActivity = activity.split(":")[1];
                        new StartWearableActivityTask()
                                .execute("checkActivityOn:" + currentActivity);
                        SharedPrefs.setLong(Globals.KEY_LAST_USER_PROMPT, currentTime.getTime(),
                                            getApplicationContext());
                    }
                } else {
                    if (activity.split(":")[0].equals("2")) {
                        if ((lastUserPromptTime + Globals.FIVE_MIN_IN_MS) <
                            System.currentTimeMillis()) {
                            Logger.log(TAG, "2 activities, 5 minutes passed",
                                       getApplicationContext());
                            String currentActivity = activity.split(":")[1];
                            new StartWearableActivityTask()
                                    .execute("checkTwoActivities:" + currentActivity);
                            SharedPrefs.setLong(Globals.KEY_LAST_USER_PROMPT, currentTime.getTime(),
                                                getApplicationContext());
                        }
                    } else {
                        if (activity.split(":")[0].equals("3")) {
                            if ((lastUserPromptTime + Globals.FIVE_MIN_IN_MS) <
                                System.currentTimeMillis()) {
                                Logger.log(TAG, "3 activities, 5 minutes passed",
                                           getApplicationContext());
                                String currentActivity = activity.split(":")[1];
                                new StartWearableActivityTask()
                                        .execute("checkThreeActivities:" + currentActivity);
                                SharedPrefs.setLong(Globals.KEY_LAST_USER_PROMPT,
                                                    currentTime.getTime(), getApplicationContext());
                            }
                        } else {
                            if (activity.split(":")[0].equals("Unknown")) {
                                if ((lastUserPromptTime + Globals.FIVE_MIN_IN_MS) <
                                    System.currentTimeMillis()) {
                                    Logger.log(TAG, "Unknown activity, 5 minutes passed",
                                               getApplicationContext());
                                    String currentActivity = activity.split(":")[1];
                                    new StartWearableActivityTask()
                                            .execute("checkUnknownActivity:" + currentActivity);
                                    SharedPrefs.setLong(Globals.KEY_LAST_USER_PROMPT,
                                                        currentTime.getTime(),
                                                        getApplicationContext());
                                }
                            }
                        }
                    }
                }
            } else {
                if ((lastUserPromptTime + Globals.THIRTY_MIN_IN_MS) < System.currentTimeMillis()) {
                    Logger.log(TAG, "No Activity, 30 minutes passed", getApplicationContext());

                    String activity = getMostProbableActivity();
                    Logger.log(TAG, "Most Probable Activity = " + activity,
                               getApplicationContext());
                    if (!activity.equals("")) {
                        if (activity.split(":")[0].equals("1")) {
                            new StartWearableActivityTask()
                                    .execute("checkActivityStart:" + activity.split(":")[1]);

                        } else {
                            if (activity.split(":")[0].equals("2")) {
                                new StartWearableActivityTask()
                                        .execute("checkTwoActivities:" + activity.split(":")[1]);
                            }
                        }
                        SharedPrefs
                                .setString(Globals.KEY_CURRENT_ACTIVITY, activity,
                                           getApplicationContext());
                        SharedPrefs.setLong(Globals.KEY_CURRENT_ACTIVITY_START_TIME,
                                            System.currentTimeMillis(), getApplicationContext());
                    } else {

                        new StartWearableActivityTask().execute("checkNewActivity");
                        SharedPrefs.setLong(Globals.KEY_LAST_USER_PROMPT, currentTime.getTime(),
                                            getApplicationContext());
                    }
                }
            }
        } else {
            Logger.log(TAG, "No previous prompt", getApplicationContext());
            Date currentTime = new Date();
            new StartWearableActivityTask().execute("checkNewActivity");
            SharedPrefs.setLong(Globals.KEY_LAST_USER_PROMPT, currentTime.getTime(),
                                getApplicationContext());
        }
        this.stopSelf();
    }

    private String getMostProbableActivity() {
        String mostProbableActivity = "";

        try {
            File commonActivityFile = new File(
                    Environment.getExternalStorageDirectory() + "/.logit/activities.csv");
            CSVReader allActivitiesFile = new CSVReader(new FileReader(commonActivityFile));
            List<String[]> allActivities = allActivitiesFile.readAll();
            List<String> probableActivities = new ArrayList<String>();
            List<Integer> probableActivitiesCount = new ArrayList<Integer>();
            List<String> probableActivitiesTime = new ArrayList<String>();
            List<Integer> probableActivitiesTimeCount = new ArrayList<Integer>();
            List<String> probableActivitiesLocation = new ArrayList<String>();
            List<Integer> probableActivitiesLocationCount = new ArrayList<Integer>();

            double currentLatitude = Double.parseDouble(SharedPrefs.getString(
                    Globals.KEY_CURRENT_LATITUDE, "0", getApplicationContext()));
            double currentLongitude = Double.parseDouble(SharedPrefs.getString(
                    Globals.KEY_CURRENT_LONGITUDE, "0", getApplicationContext()));

            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            int currentHour = calendar.get(Calendar.HOUR_OF_DAY);

            for (int i = 0; i < allActivities.size(); i++) {

                Boolean isWithinHour = false;
                Boolean isWithinLocation = false;

                double activityLatitude = Double.parseDouble(allActivities.get(i)[2]);
                double activityLongitude = Double.parseDouble(allActivities.get(i)[3]);

                int activityHour = Integer.parseInt(allActivities.get(i)[1]);

                if (activityHour == currentHour) {
                    isWithinHour = true;
                }

                float results[] = new float[1];
                Location.distanceBetween(currentLatitude, currentLongitude, activityLatitude,
                                         activityLongitude, results);

                if (results[0] < 50.0f) {
                    isWithinLocation = true;
                }

                if (isWithinHour) {
                    if (probableActivitiesTime.contains(allActivities.get(i)[0])) {
                        int position = probableActivitiesTime.indexOf(allActivities.get(i)[0]);
                        probableActivitiesTimeCount
                                .set(position, probableActivitiesTimeCount.get(position) + 1);
                    } else {
                        probableActivitiesTime.add(allActivities.get(i)[0]);
                        probableActivitiesTimeCount.add(1);
                    }
                }
                if (isWithinLocation) {
                    if (probableActivitiesLocation.contains(allActivities.get(i)[0])) {
                        int position = probableActivitiesLocation.indexOf(allActivities.get(i)[0]);
                        probableActivitiesLocationCount
                                .set(position, probableActivitiesLocationCount.get(position) + 1);
                    } else {
                        probableActivitiesLocation.add(allActivities.get(i)[0]);
                        probableActivitiesLocationCount.add(1);
                    }
                }
                if (isWithinHour && isWithinLocation) {
                    if (probableActivities.contains(allActivities.get(i)[0])) {
                        int position = probableActivities.indexOf(allActivities.get(i)[0]);
                        probableActivitiesCount
                                .set(position, probableActivitiesCount.get(position) + 1);
                    } else {
                        probableActivities.add(allActivities.get(i)[0]);
                        probableActivitiesCount.add(1);
                    }
                }
            }

            if (probableActivitiesLocation.size() != 0) {
                int size = probableActivitiesLocation.size();
                int index = 0;
                for (int i = 0; i < size; i++) {
                    if (probableActivitiesLocationCount.get(index) < 3) {
                        probableActivitiesLocation.remove(index);
                        probableActivitiesLocationCount.remove(index);
                    } else {
                        index++;
                    }
                }
            }

            if (probableActivitiesTime.size() != 0) {
                int size = probableActivitiesTime.size();
                int index = 0;
                for (int i = 0; i < size; i++) {
                    if (probableActivitiesTimeCount.get(index) < 3) {
                        probableActivitiesTime.remove(index);
                        probableActivitiesTimeCount.remove(index);
                    } else {
                        index++;
                    }
                }
            }

            if (probableActivities.size() != 0) {
                // Best case scenario
                if (probableActivities.size() == 1) {
                    mostProbableActivity = "1:" + probableActivities.get(0);
                } else {
                    if (probableActivities.size() == 2) {
                        mostProbableActivity = "2:" + probableActivities.get(0) +
                                               "`" + probableActivities.get(1);
                    } else {
                        if (probableActivities.size() > 2) {
                            int maxCount = 0;
                            for (int i = 0; i < probableActivities.size(); i++) {
                                if (probableActivitiesCount.get(i) >= maxCount) {
                                    mostProbableActivity = "1:" + probableActivities.get(i);
                                    maxCount = probableActivitiesCount.get(i);
                                }
                            }
                        }
                    }
                }
            } else {
                if (probableActivitiesTime.size() != 0) {
                    // Best case scenario
                    if (probableActivitiesTime.size() == 1) {
                        mostProbableActivity = "1:" + probableActivitiesTime.get(0);
                    } else {
                        if (probableActivitiesTime.size() == 2) {
                            mostProbableActivity = "2:" + probableActivitiesTime.get(0) +
                                                   "`" + probableActivitiesTime.get(1);
                        } else {
                            if (probableActivitiesTime.size() > 2) {
                                int maxCount = 0;
                                for (int i = 0; i < probableActivitiesTime.size(); i++) {
                                    if (probableActivitiesTimeCount.get(i) >= maxCount) {
                                        mostProbableActivity = "1:" + probableActivitiesTime.get(i);
                                        maxCount = probableActivitiesTimeCount.get(i);
                                    }
                                }
                            }
                        }
                    }
                } else {
                    if (probableActivitiesLocation.size() != 0) {
                        // Best case scenario
                        if (probableActivitiesLocation.size() == 1) {
                            mostProbableActivity = "1:" + probableActivitiesLocation.get(0);
                        } else {
                            if (probableActivitiesLocation.size() == 2) {
                                mostProbableActivity = "2:" + probableActivitiesLocation.get(0) +
                                                       "`" + probableActivitiesLocation.get(1);
                            } else {
                                if (probableActivitiesLocation.size() > 2) {
                                    int maxCount = 0;
                                    for (int i = 0; i < probableActivitiesLocation.size(); i++) {
                                        if (probableActivitiesLocationCount.get(i) >= maxCount) {
                                            mostProbableActivity =
                                                    "1:" + probableActivitiesLocation.get(i);
                                            maxCount = probableActivitiesLocationCount.get(i);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            if (allActivities.get(allActivities.size() - 1).equals(mostProbableActivity)) {
                mostProbableActivity = "";
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return mostProbableActivity;
    }

    public void showServiceNotification() {

        boolean isActivityOn = SharedPrefs
                .getBoolean(Globals.KEY_IS_ACTIVITY_ON, false, getApplicationContext());
        if (isActivityOn) {
            String activity = SharedPrefs
                    .getString(Globals.KEY_CURRENT_ACTIVITY, "", getApplicationContext());
            if (activity.split(":")[0].equals("1")) {
                String currentActivity = activity.split(":")[1];
                NotificationCompat.Builder mBuilder =
                        new NotificationCompat.Builder(this)
                                .setSmallIcon(R.drawable.ic_launcher_logit)
                                .setContentTitle("LogIt")
                                .setContentText(
                                        "Your current activity - " + currentActivity)
                                .setAutoCancel(false);

                NotificationManager mNotificationManager =
                        (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                // serviceNotificationID allows you to update the notification later on.
                mNotificationManager.notify(serviceNotificationID, mBuilder.build());
            } else {
                NotificationCompat.Builder mBuilder =
                        new NotificationCompat.Builder(this)
                                .setSmallIcon(R.drawable.ic_launcher_logit)
                                .setContentTitle("LogIt")
                                .setContentText(
                                        "Trying to identify activity")
                                .setAutoCancel(false);

                NotificationManager mNotificationManager =
                        (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                // serviceNotificationID allows you to update the notification later on.
                mNotificationManager.notify(serviceNotificationID, mBuilder.build());
            }
        } else {
            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(this)
                            .setSmallIcon(R.drawable.ic_launcher_logit)
                            .setContentTitle("LogIt")
                            .setContentText(
                                    "No current activity")
                            .setAutoCancel(false);

            NotificationManager mNotificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            // serviceNotificationID allows you to update the notification later on.
            mNotificationManager.notify(serviceNotificationID, mBuilder.build());
        }


    }

    @Override
    public void onConnected(Bundle bundle) {
        mResolvingError = false;
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

        mResolvingError = false;
    }

    private void sendStartActivityMessage(String node, String mQuestion) {

        Wearable.MessageApi.sendMessage(mGoogleApiClient, node,
                                        START_ACTIVITY_PATH, mQuestion.getBytes())
                           .setResultCallback(
                                   new ResultCallback<MessageApi.SendMessageResult>() {
                                       @Override
                                       public void onResult(
                                               MessageApi.SendMessageResult sendMessageResult) {
                                           if (!sendMessageResult.getStatus().isSuccess()) {
                                               Log.e(TAG,
                                                     "Failed to send message with status code: "
                                                     + sendMessageResult.getStatus()
                                                                        .getStatusCode());
                                           }
                                       }
                                   });
    }

    private Collection<String> getNodes() {
        HashSet<String> results = new HashSet<String>();
        NodeApi.GetConnectedNodesResult nodes = Wearable.NodeApi
                .getConnectedNodes(mGoogleApiClient).await();

        for (Node node : nodes.getNodes()) {
            results.add(node.getId());
        }

        return results;
    }

    private void generateDefaultActivities() {
        Logger.log(TAG, "Inside generateDefaultActivities", getApplicationContext());
        File directory = new File(Environment.getExternalStorageDirectory() + "/.logit/");
        if (!directory.exists()) {
            directory.mkdirs();
        }
        File defaultFile = new File(
                Environment.getExternalStorageDirectory() + "/.logit/defaultActivities.csv");

        if (!defaultFile.exists()) {
            CSVReader csvFile;
            CSVWriter csvWriter;
            AssetManager am = getAssets();
            InputStream csvStream = null;
            try {
                csvStream = am.open("defaultActivities.txt");
                csvWriter = new CSVWriter(new FileWriter(defaultFile));
                InputStreamReader csvStreamReader = new InputStreamReader(csvStream);
                csvFile = new CSVReader(csvStreamReader);
                String nextLine[];
                while ((nextLine = csvFile.readNext()) != null) {
                    csvWriter.writeNext(nextLine);
                }
                csvWriter.close();

            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        promptIfRequired();

    }

    private class StartWearableActivityTask extends
            AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {
            Collection<String> nodes = getNodes();
            for (String node : nodes) {
                sendStartActivityMessage(node, params[0]);
            }
            return null;
        }
    }
}
