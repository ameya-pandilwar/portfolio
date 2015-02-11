package edu.neu.madcourse.dharammaniar.logit;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import java.util.Collection;
import java.util.HashSet;

import edu.neu.madcourse.dharammaniar.R;

/**
 * Created by Ameya on 25-11-2014.
 */
public class LogItWearConnectivityTutorialScreen extends Activity
        implements DataApi.DataListener, MessageApi.MessageListener,
        NodeApi.NodeListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    final static String APPLICATION_NAME = "LogIt";
    private Boolean isDeviceConnected = false;
    private Boolean isNextActivityStarted = false;
    private int deviceConnectedCount = 0;
    SharedPreferences preferences;
    private TextView wearConnectivityTutorialScreenGreeting, wearConnectivityTutorialScreenMessage;
    private ImageView wearConnectivityTutorialScreenNext;
    private Button wearConnectivityTutorialScreenSkip;
    private GoogleApiClient mGoogleApiClient;
    private boolean mResolvingError = false;
    private CountDownTimer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_it_wear_connectivity_tutorial_screen);

        wearConnectivityTutorialScreenGreeting = (TextView) findViewById(
                R.id.wearConnectivityTutorialScreenGreeting);
        wearConnectivityTutorialScreenMessage = (TextView) findViewById(
                R.id.wearConnectivityTutorialScreenMessage);
        wearConnectivityTutorialScreenNext = (ImageView) findViewById(
                R.id.wearConnectivityTutorialScreenNext);
        wearConnectivityTutorialScreenSkip = (Button) findViewById(
                R.id.wearConnectivityTutorialScreenSkip);

        wearConnectivityTutorialScreenNext.setVisibility(View.INVISIBLE);
//        wearConnectivityTutorialScreenSkip.setVisibility(View.INVISIBLE);

        preferences = this.getSharedPreferences(APPLICATION_NAME, Context.MODE_PRIVATE);
        String name = preferences.getString("Name", "");

        wearConnectivityTutorialScreenGreeting
                .setText("Welcome " + name + "!\nLet's Get Started...");
        wearConnectivityTutorialScreenMessage.setText("Please Connect Your Android Wear Device");

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API).addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).build();
        if (!mResolvingError) {
            mGoogleApiClient.connect();
        }

        timer = new CountDownTimer(5 * 60 * 1000, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {
                new StartWearConnectivityTask().execute();

                if (isDeviceConnected) {
                    if (deviceConnectedCount == 2) {
                        if (!isNextActivityStarted) {
                            Intent intent = new Intent(LogItWearConnectivityTutorialScreen.this,
                                                       LogItWearQuestionTutorialScreen.class);
                            startActivity(intent);
                            this.cancel();
                            isNextActivityStarted = true;
                        }
                    } else {
                        deviceConnectedCount++;
                    }
                }

            }

            @Override
            public void onFinish() {

            }
        };
        timer.start();

    }

    public void onClickNext(View view) {
        Intent intent = new Intent(LogItWearConnectivityTutorialScreen.this,
                                   LogItWearQuestionTutorialScreen.class);
        startActivity(intent);
    }

    public void onClickSkip(View view) {
        Intent intent = new Intent(LogItWearConnectivityTutorialScreen.this, LogItMainScreen.class);
        startActivity(intent);
    }

    @Override
    public void onConnected(Bundle bundle) {

        mResolvingError = false;
        Wearable.NodeApi.addListener(mGoogleApiClient, this);

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {

    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {

    }

    @Override
    public void onPeerConnected(Node node) {

    }

    @Override
    public void onPeerDisconnected(Node node) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

        mResolvingError = false;
        Wearable.NodeApi.removeListener(mGoogleApiClient, this);

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

    private class StartWearConnectivityTask extends AsyncTask<Void, Void, Integer> {

        @Override
        protected Integer doInBackground(Void... params) {
            Collection<String> nodes = getNodes();
            return getNodes().size();
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            if (integer == 0) {
                isDeviceConnected = false;
            } else {
                isDeviceConnected = true;
                wearConnectivityTutorialScreenMessage.setText("Your wear device is connected.");
                wearConnectivityTutorialScreenNext.setVisibility(View.VISIBLE);
            }
        }
    }
}
