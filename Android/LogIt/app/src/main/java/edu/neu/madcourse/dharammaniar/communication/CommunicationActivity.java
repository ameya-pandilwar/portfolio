package edu.neu.madcourse.dharammaniar.communication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.neu.madcourse.dharammaniar.R;

/**
 * Created by Dharam on 10/21/2014.
 */
public class CommunicationActivity extends Activity implements View.OnClickListener {

    static final String TAG = "GCM_Communication";
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    GoogleCloudMessaging gcm;
    SharedPreferences prefs;
    Context context;
    String regid;

    private TextView friendname;
    private Button notification;
    private Button enterHs;
    private Button viewHs;
    private Button ack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_communication);
        initializeMembers();
    }

    private void initializeMembers() {
        gcm = GoogleCloudMessaging.getInstance(this);
        context = getApplicationContext();

        friendname = (TextView) findViewById(R.id.activity_communication_tv_friendname);
        friendname
                .setText(getGCMPreferences(getApplicationContext()).getString("friend", "friend"));
        notification = (Button) findViewById(R.id.activity_communication_bt_notif);
        notification.setOnClickListener(this);
        enterHs = (Button) findViewById(R.id.activity_communication_bt_enter_hs);
        enterHs.setOnClickListener(this);
        viewHs = (Button) findViewById(R.id.activity_communication_bt_view_hs);
        viewHs.setOnClickListener(this);
        regid = getGCMPreferences(getApplicationContext()).getString("registration_id", "");
        ack = (Button) findViewById(R.id.activity_communication_bt_ack);
        ack.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == notification.getId()) {
            if (isInternetAvailable()) {
                sendMessage("Notif");
            } else {
                Toast.makeText(getApplicationContext(),
                               "No Internet Connection. Please connect and try again.",
                               Toast.LENGTH_SHORT).show();
            }
        }
        if (v.getId() == enterHs.getId()) {
            startActivity(new Intent(getApplicationContext(), EnterScoreActivity.class));
        }
        if (v.getId() == viewHs.getId()) {
            startActivity(new Intent(getApplicationContext(), DisplayScores.class));
        }
        if (v.getId() == ack.getId()) {
            startActivity(new Intent(getApplicationContext(),Acknowledgement.class));
        }
    }

    private SharedPreferences getGCMPreferences(Context context) {
        return getSharedPreferences("GCMPreferences",
                                    Context.MODE_PRIVATE);
    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                                                      PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

    private void sendMessage(final String message) {
        if (regid == null || regid.equals("")) {
            Toast.makeText(this, "You must register first", Toast.LENGTH_LONG)
                 .show();
            return;
        }
        if (message.isEmpty()) {
            Toast.makeText(this, "Empty Message", Toast.LENGTH_LONG).show();
            return;
        }

        if (!checkPlayServices()) {
            Toast.makeText(this, "Device not supported", Toast.LENGTH_LONG).show();
            return;
        }

        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg = "";

                List<String> regIds = new ArrayList<String>();
                String reg_device;
                int nIcon = R.drawable.ic_launcher;
                int nType = CommunicationConstants.SIMPLE_NOTIFICATION;
                Map<String, String> msgParams;
                msgParams = new HashMap<String, String>();
                msgParams.put("data.username",
                              getGCMPreferences(getApplicationContext()).getString("username", ""));
                msgParams.put("data.gcmid", regid);
                msgParams.put("data.notifType", message);
                GcmNotification gcmNotification = new GcmNotification();
                regIds.clear();
                reg_device = getGCMPreferences(getApplicationContext())
                        .getString("friend_id", "friend_id");
                regIds.add(reg_device);
                gcmNotification.sendNotification(msgParams, regIds, getApplicationContext());
                msg = "sending information...";
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
            }
        }.execute(null, null, null);
    }

    private boolean isInternetAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni == null) {
            // There are no active networks.
            return false;
        } else
            return true;
    }
}
