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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collections;

import edu.neu.madcourse.dharammaniar.R;

/**
 * Created by Dharam on 10/21/2014.
 */
public class FindUserActivity extends Activity {

    static final String TAG = "FindUserActivity";

    private ArrayList<String> friends;
    private ArrayAdapter<String> adapter;
    private String usernames[];
    private String gcmIds[];
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_user);
        listView = (ListView) findViewById(R.id.activity_find_user_listView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                for (int i = 0; i < usernames.length; i++) {
                    if (usernames[i].equals(listView.getItemAtPosition(position).toString())) {
                        SharedPreferences.Editor editor = getGCMPreferences(getApplicationContext())
                                .edit();
                        editor.putString("friend", usernames[i]);
                        editor.putString("friend_id", gcmIds[i]);
                        editor.commit();
                        startActivity(
                                new Intent(getApplicationContext(), CommunicationActivity.class));
                    }
                }
            }
        });
        friends = new ArrayList<String>();
        adapter = new ArrayAdapter<String>(getApplicationContext(),
                                           android.R.layout.simple_list_item_1,
                                           android.R.id.text1,
                                           friends);
        if (isInternetAvailable()) {
            getAllUsers();
        } else {
            Toast.makeText(getApplicationContext(),
                           "No Internet Connection. Please connect and try again",
                           Toast.LENGTH_SHORT).show();
        }
    }

    private void getAllUsers() {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                Log.i(TAG, "Inside getAllUsers");
                String msg = "";
                try {
                    HttpGet httpGet = new HttpGet(
                            "http://www.dharammaniar.com/numad/user.php?tag=allusers");
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
                    String json = reader.readLine();
                    System.out.println("JSON Response = " + json);
                    try {
                        JSONObject jsonObject = new JSONObject(json);
                        usernames = jsonObject.get("username").toString().split("`");
                        gcmIds = jsonObject.get("gcm_id").toString().split("`");
                        for (int i = 0; i < usernames.length; i++) {
                            if (!usernames[i].equals(getUsername(getApplicationContext()))) {
                                friends.add(usernames[i]);
                            }
                        }
                        Collections.sort(friends);
                    } catch (JSONException e) {
                        Log.e(TAG, "Error parsing data " + e.toString());
                    }
                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                }
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
                listView.setAdapter(adapter);
            }
        }.execute(null, null, null);
    }

    private String getUsername(Context context) {
        final SharedPreferences prefs = getGCMPreferences(context);
        return prefs.getString("username", "username");
    }

    private SharedPreferences getGCMPreferences(Context context) {
        return getSharedPreferences("GCMPreferences",
                                    Context.MODE_PRIVATE);
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
