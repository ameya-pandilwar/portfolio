package edu.neu.madcourse.dharammaniar.trickiestpart;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.os.Vibrator;
import android.speech.RecognizerIntent;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import edu.neu.madcourse.dharammaniar.R;

/**
 * Created by Dharam Maniar.
 * Activity is used to display the questions to the user and save answers to send back to the phone.
 */
public class TestVoiceActivity extends Activity
        implements MessageApi.MessageListener, NodeApi.NodeListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    public static final long[] VIBRATE_INTENSE = {1000, 200, 1000, 200, 500, 250, 500, 250};
    private static final String START_ACTIVITY_PATH = "/logit";
    private static final int SPEECH_REQUEST_CODE = 0;
    private String TAG = "TestAudioActivity";
    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;
    private PowerManager.WakeLock mWakeLock;
    private Activity activity;
    private Vibrator mVibrator;
    private GoogleApiClient mGoogleApiClient;
    private boolean mResolvingError = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        final PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        //noinspection deprecation
        this.mWakeLock = pm
                .newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.FULL_WAKE_LOCK,
                             "logit");
        this.mWakeLock.acquire();

        super.onCreate(savedInstanceState);
        mSharedPreferences = getSharedPreferences("logit", MODE_PRIVATE);
        mEditor = mSharedPreferences.edit();
        mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        mVibrator.vibrate(VIBRATE_INTENSE, -1);
        setContentView(R.layout.audio);
        activity = this;

        Handler mHandler1 = new Handler();
        mHandler1.postDelayed(new Runnable() {
            @Override
            public void run() {
                displaySpeechRecognizer();
            }
        }, 5 * 1000);

        Handler mHandler2 = new Handler();
        mHandler2.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mWakeLock.isHeld()) {
                    mWakeLock.release();
                    activity.finish();
                }
            }
        }, 30 * 1000);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        if (!mResolvingError) {
            mGoogleApiClient.connect();
        }

    }

    @Override
    protected void onDestroy() {
        mVibrator.cancel();
        if (this.mWakeLock.isHeld()) {
            this.mWakeLock.release();
        }
        super.onDestroy();
    }

    // Create an intent that can start the Speech Recognizer activity
    private void displaySpeechRecognizer() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                        RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        // Start the activity, the intent will be populated with the speech text
        startActivityForResult(intent, SPEECH_REQUEST_CODE);
    }

    // This callback is invoked when the Speech Recognizer returns.
    // This is where you process the intent and extract the speech text from the intent.
    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        if (requestCode == SPEECH_REQUEST_CODE && resultCode == RESULT_OK) {
            List<String> results = data.getStringArrayListExtra(
                    RecognizerIntent.EXTRA_RESULTS);
            String spokenText = results.get(0);
            // Do something with spokenText
            new StartWearableActivityTask().execute("test-speech:" + spokenText);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onConnected(Bundle bundle) {
        mResolvingError = false;
        Wearable.MessageApi.addListener(mGoogleApiClient, this);
        Wearable.NodeApi.addListener(mGoogleApiClient, this);
    }

    @Override
    public void onConnectionSuspended(int i) {

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
        Wearable.MessageApi.removeListener(mGoogleApiClient, this);
        Wearable.NodeApi.removeListener(mGoogleApiClient, this);
    }

    private Collection<String> getNodes() {
        HashSet<String> results = new HashSet<String>();
        NodeApi.GetConnectedNodesResult nodes =
                Wearable.NodeApi.getConnectedNodes(mGoogleApiClient).await();
        for (Node node : nodes.getNodes()) {
            results.add(node.getId());
        }

        return results;
    }

    private void sendStartActivityMessage(String node, String mAnswer) {

        Wearable.MessageApi.sendMessage(
                mGoogleApiClient, node, START_ACTIVITY_PATH, mAnswer.getBytes()).setResultCallback(
                new ResultCallback<MessageApi.SendMessageResult>() {
                    @Override
                    public void onResult(MessageApi.SendMessageResult sendMessageResult) {
                        if (!sendMessageResult.getStatus().isSuccess()) {
                            Log.e(TAG, "Failed to send message with status code: "
                                       + sendMessageResult.getStatus().getStatusCode());
                        }
                    }
                }
        );
    }

    private class StartWearableActivityTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {
            Collection<String> nodes = getNodes();
            for (String node : nodes) {
                sendStartActivityMessage(node, params[0]);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            finish();
        }
    }

}
