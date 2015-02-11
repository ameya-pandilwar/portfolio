package edu.neu.madcourse.dharammaniar.trickiestpart;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.os.Vibrator;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

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

import edu.neu.madcourse.dharammaniar.R;

/**
 * Created by Dharam Maniar.
 * Activity is used to display the questions to the user and save answers to send back to the phone.
 */
public class TestQuestionActivity extends Activity implements View.OnClickListener,
        MessageApi.MessageListener, NodeApi.NodeListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    public static final long[] VIBRATE_INTENSE = {1000, 200, 1000, 200, 500, 250, 500, 250};
    private static final String START_ACTIVITY_PATH = "/logit";
    private String TAG = "TestQuestionActivity";
    private TextView question;
    private Button answer1;
    private Button answer2;
    private Button answer3;
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
        this.mWakeLock = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP|PowerManager.FULL_WAKE_LOCK, "logit");
        this.mWakeLock.acquire();

        super.onCreate(savedInstanceState);
        mSharedPreferences = getSharedPreferences("logit", MODE_PRIVATE);
        mEditor = mSharedPreferences.edit();
        mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        mVibrator.vibrate(VIBRATE_INTENSE, -1);
        setContentView(R.layout.question_style1);
        activity = this;

        Handler mHandler = new Handler();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mWakeLock.isHeld()) {
                    mWakeLock.release();
                    activity.finish();
                }
            }
        }, 120 * 1000);

        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                question = (TextView) stub.findViewById(R.id.question);
                answer1 = (Button) stub.findViewById(R.id.answer1);
                answer2 = (Button) stub.findViewById(R.id.answer2);
                answer3 = (Button) stub.findViewById(R.id.answer3);

                question.setText("What are you currently doing?");
                answer1.setText("Having lunch");
                answer1.setOnClickListener(TestQuestionActivity.this);
                answer2.setText("Doing work");
                answer2.setOnClickListener(TestQuestionActivity.this);
                answer3.setText("Neither");
                answer3.setOnClickListener(TestQuestionActivity.this);
            }
        });

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


    @Override
    public void onClick(View v) {

        mEditor.apply();
        if (v.getId() == answer1.getId()) {
            new StartWearableActivityTask().execute("test-answer:1");
        }
        if (v.getId() == answer2.getId()) {
            new StartWearableActivityTask().execute("test-answer:2");
        }
        if (v.getId() == answer3.getId()) {
            new StartWearableActivityTask().execute("test-answer:3");
        }
        finish();
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
