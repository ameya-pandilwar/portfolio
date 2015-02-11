package edu.neu.madcourse.dharammaniar.communication;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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
 * Created by Dharam on 10/28/2014.
 */
public class EnterScoreActivity extends Activity {

    private EditText score;
    private Button submit;
    private Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_communication_enter_score);
        activity = this;
        score = (EditText) findViewById(R.id.activity_enter_score_et_score);
        submit = (Button) findViewById(R.id.activity_enter_score_bt_submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isInternetAvailable()) {
                    registerInBackground();
                }else {
                    Toast.makeText(getApplicationContext(),
                                   "No Internet Connection. Please connect and try again.",
                                   Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void registerInBackground() {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                try {
                    HttpGet httpGet = new HttpGet(
                            "http://www.dharammaniar.com/numad/score.php?tag=enterScore&username=" +
                            getGCMPreferences(getApplicationContext())
                                    .getString("username", "username")
                            + "&score=" + score.getText().toString());
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

                } catch (IOException ex)

                {
                    msg = "Error :" + ex.getMessage();
                }

                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
                if (msg.equals("true")) {
                    Toast.makeText(getApplicationContext(),
                                   "Successfully Saved!",
                                   Toast.LENGTH_SHORT).show();
                    activity.finish();
                } else {
                    Toast.makeText(getApplicationContext(),
                                   "Failed. Please try again",
                                   Toast.LENGTH_SHORT).show();
                }

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
