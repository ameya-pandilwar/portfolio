package edu.neu.madcourse.dharammaniar.communication;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
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

import edu.neu.madcourse.dharammaniar.R;

/**
 * Created by Dharam on 10/21/2014.
 */
public class DisplayScores extends Activity {

    static final String TAG = "DisplayScoreActivity";

    private ArrayList<String> scores;
    private ArrayAdapter<String> adapter;
    private String usernames[];
    private String userscore[];
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_highscore);
        listView = (ListView) findViewById(R.id.activity_find_user_listView);

        scores = new ArrayList<String>();
        adapter = new ArrayAdapter<String>(getApplicationContext(),
                                           android.R.layout.simple_list_item_1,
                                           android.R.id.text1,
                                           scores);
        if(isInternetAvailable()) {
            getHighScores();
        }
        else {
            Toast.makeText(getApplicationContext(),"Internet not available",Toast.LENGTH_SHORT).show();
        }
    }

    private void getHighScores() {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                Log.i(TAG, "Inside getAllUsers");
                String msg = "";
                try {
                    HttpGet httpGet = new HttpGet(
                            "http://www.dharammaniar.com/numad/score.php?tag=getHighScore");
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
                        userscore = jsonObject.get("score").toString().split("`");
                        for (int i = 0; i < usernames.length; i++) {
                            scores.add(usernames[i] + " - " + userscore[i]);
                        }
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
