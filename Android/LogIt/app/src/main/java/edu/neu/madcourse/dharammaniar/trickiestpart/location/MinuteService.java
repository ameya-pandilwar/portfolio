package edu.neu.madcourse.dharammaniar.trickiestpart.location;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;
import edu.neu.madcourse.dharammaniar.R;

/**
 * Created by Dharam on 11/11/2014.
 */
public class MinuteService extends Service implements
        GooglePlayServicesClient.ConnectionCallbacks,
        GooglePlayServicesClient.OnConnectionFailedListener {

    public static final long[] VIBRATE_INTENSE = {1000, 200, 1000, 200, 500, 250, 500, 250, 500,
            250, 500, 250, 250, 250, 250, 250, 250, 250, 250, 250, 250, 100, 100, 100, 100, 100,
            100, 100, 1000, 2000, 1000, 200, 1000, 200, 500, 250, 500, 250, 500,
            250, 500, 250, 250, 250, 250, 250, 250, 250, 250, 250, 250, 100, 100, 100, 100, 100,
            100, 100, 1000, 2000};
    private static final int TWO_MINUTES = 1000 * 60 * 2;
    SimpleDateFormat time = new SimpleDateFormat("HH:mm");
    SimpleDateFormat timestamp = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
    private String TAG = "MinuteService";
    private int notificationID = 10000;
    private int serviceNotificationID = 10001;
    private LocationClient mLocationClient;
    private CSVWriter mLocationWriter;
    private CSVReader mLocationReader;
    private Date currentTime;
    private Vibrator mVibrator;
    private Boolean locationEnabled;
    private Location mCurrentLocation;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        currentTime = new Date();
        showServiceNotification();
        LocationManager manager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER) &&
            !manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            locationEnabled = false;
            Toast.makeText(getApplicationContext(), "Enable location services for accurate data",
                           Toast.LENGTH_SHORT).show();
        } else {
            mLocationClient = new LocationClient(this, this, this);
            mLocationClient.connect();
            locationEnabled = true;
        }
        mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
    }

    private void getLocation() {
        File locationDirectory = new File(Environment.getExternalStorageDirectory() + "/.logit/");
        if (!locationDirectory.exists()) {
            locationDirectory.mkdirs();
        }
        File locationFile = new File(
                Environment.getExternalStorageDirectory() + "/.logit/location.csv");
        try {
            mLocationWriter = new CSVWriter(new FileWriter(locationFile, true));
            mLocationReader = new CSVReader(new FileReader(locationFile));
            List<String[]> locations = mLocationReader.readAll();
            String answer[] = new String[6];
            mCurrentLocation = mLocationClient.getLastLocation();
            Log.d(TAG,
                  mCurrentLocation.getLatitude() + "," + mCurrentLocation.getLongitude() + "," +
                  mCurrentLocation.getAccuracy());

            float[] results = new float[10];
            if (!locations.isEmpty()) {
                Location.distanceBetween(mCurrentLocation.getLatitude(),
                                         mCurrentLocation.getLongitude(),
                                         Double.parseDouble(locations.get(
                                                 locations.size() - 1)[1]),
                                         Double.parseDouble(locations.get(
                                                 locations.size() - 1)[2]), results);
            } else {
                results[0] = 0.0f;
            }

            Log.d(TAG, "Distance = " + results[0]);

            if ((mCurrentLocation.getAccuracy() > 50.0f) || (locations.isEmpty())) {

                answer[0] = timestamp.format(currentTime);
                answer[1] = mCurrentLocation.getLatitude() + "";
                answer[2] = mCurrentLocation.getLongitude() + "";
                answer[3] = mCurrentLocation.getAccuracy() + "";
                answer[4] = results[0] + "";
                answer[5] = "";
                if (results[0] > 30) {
                    mVibrator.vibrate(VIBRATE_INTENSE, -1);
                    showNotification();
                    answer[5] = "New Location";
                }
                mLocationWriter.writeNext(answer);
            }

            mLocationWriter.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        this.stopSelf();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "Inside onDestroy");
    }

    public void showNotification() {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_launcher_logit)
                        .setContentTitle("LogIt")
                        .setContentText("Found New Location at - " + time.format(currentTime))
                        .setAutoCancel(false)
                        .setOngoing(true);

        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // notificationID allows you to update the notification later on.
        mNotificationManager.notify(notificationID, mBuilder.build());
    }

    public void showServiceNotification() {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_launcher_logit)
                        .setContentTitle("LogIt")
                        .setContentText(
                                "Location Service is last ran at - " + time.format(currentTime))
                        .setAutoCancel(false);

        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // notificationID allows you to update the notification later on.
        mNotificationManager.notify(serviceNotificationID, mBuilder.build());
    }

    public void removeServiceNotification() {

        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancel(serviceNotificationID);
    }

    @Override
    public void onConnected(Bundle bundle) {
        if (locationEnabled) {
            getLocation();
        }
    }

    @Override
    public void onDisconnected() {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
}
