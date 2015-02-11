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
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;

import edu.neu.madcourse.dharammaniar.R;

/**
 * Created by Dharam on 10/21/2014.
 */
public class RegistrationActivity extends Activity {

    public static final String PROPERTY_REG_ID = "registration_id";
    static final String TAG = "GCM_Communication";
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    GoogleCloudMessaging gcm;
    SharedPreferences prefs;
    Context context;
    String regid;
    private EditText username;
    private Button register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_communication_registration);
        initializeMembers();
    }

    private void initializeMembers() {
        username = (EditText) findViewById(R.id.activity_communication_et_username);
        register = (Button) findViewById(R.id.activity_communication_bt_register);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isInternetAvailable()) {
                    if (checkPlayServices()) {
                        if (username.getText().toString().equals("")) {
                            Toast.makeText(getApplicationContext(), "Please enter username",
                                           Toast.LENGTH_SHORT).show();
                        } else {
                            if (username.getText().toString().contains(" ")) {
                                Toast.makeText(getApplicationContext(),
                                               "Username cannot contain spaces",
                                               Toast.LENGTH_SHORT).show();
                            } else {
                                registerInBackground();
                            }
                        }
                    } else {
                        Toast.makeText(getApplicationContext(),
                                       "Device not supported",
                                       Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(),
                                   "Internet Not Available. Please connect and try again.",
                                   Toast.LENGTH_SHORT).show();
                }
            }
        });
        gcm = GoogleCloudMessaging.getInstance(this);
        context = getApplicationContext();
    }

    private void registerInBackground() {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                Log.i(TAG, "Inside registerInBackground");
                String msg = "";
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(context);
                    }
                    regid = gcm.register(CommunicationConstants.GCM_SENDER_ID);

                    HttpGet httpGet = new HttpGet(
                            "http://www.dharammaniar.com/numad/user.php?tag=register&username=" +
                            username.getText().toString() + "&gcmid=" + regid);
                    HttpParams httpParameters = new BasicHttpParams();
                    int timeoutConnection = 3000;
                    HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
                    int timeoutSocket = 5000;
                    HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
                    DefaultHttpClient httpClient = new DefaultHttpClient(httpParameters);
                    HttpResponse httpResponse = httpClient.execute(httpGet);
                    HttpEntity httpEntity = httpResponse.getEntity();
                    InputStream is = httpEntity.getContent();
                    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(is, "iso-8859-1"), 200);
                    String response = reader.readLine();
                    System.out.println("JSON Response = " + response);
                    msg = response;
                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                }
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
                if (msg.equals("true")) {
                    storeRegistrationId(getApplicationContext(), regid,
                                        username.getText().toString());
                    startActivity(new Intent(getApplicationContext(), FindUserActivity.class));
                } else {
                    Toast.makeText(getApplicationContext(),
                                   "Registration failed. Please try using different username",
                                   Toast.LENGTH_SHORT).show();
                }

            }
        }.execute(null, null, null);
    }

    private void storeRegistrationId(Context context, String regId, String username) {
        final SharedPreferences prefs = getGCMPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PROPERTY_REG_ID, regId);
        editor.putString("username", username);
        editor.commit();
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
