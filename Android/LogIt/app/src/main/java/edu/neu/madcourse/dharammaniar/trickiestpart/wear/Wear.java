package edu.neu.madcourse.dharammaniar.trickiestpart.wear;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
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
 * Created by Dharam on 11/20/2014.
 */
public class Wear extends Activity
        implements View.OnClickListener, DataApi.DataListener, MessageApi.MessageListener,
        NodeApi.NodeListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "Wear";
    private static final String START_ACTIVITY_PATH = "/logit";
    private Button connectivity;
    private Button question;
    private Button voice;
    private GoogleApiClient mGoogleApiClient;
    private boolean mResolvingError = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trickiest_wear);

        connectivity = (Button) findViewById(R.id.activity_trickiest_wear_connectivity);
        connectivity.setOnClickListener(this);
        question = (Button) findViewById(R.id.activity_trickiest_wear_question);
        question.setOnClickListener(this);
        voice = (Button) findViewById(R.id.activity_trickiest_wear_voice);
        voice.setOnClickListener(this);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API).addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).build();
        if (!mResolvingError) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    public void onClick(View v) {

        if (v.getId() == connectivity.getId()) {
            new StartWearConnectivityTask().execute();
        }
        if (v.getId() == question.getId()) {
            new StartWearableActivityTask().execute("test-question");
        }
        if (v.getId() == voice.getId()) {
            new StartWearableActivityTask().execute("test-voice");
        }
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

    @Override
    public void onConnected(Bundle bundle) {

        mResolvingError = false;
        Wearable.DataApi.addListener(mGoogleApiClient, this);
        Wearable.MessageApi.addListener(mGoogleApiClient, this);
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
        Wearable.DataApi.removeListener(mGoogleApiClient, this);
        Wearable.MessageApi.removeListener(mGoogleApiClient, this);
        Wearable.NodeApi.removeListener(mGoogleApiClient, this);

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
                Toast.makeText(getApplicationContext(), "No Wear device connected.",
                               Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "Wear device connected.",
                               Toast.LENGTH_SHORT).show();
            }
        }
    }
}
