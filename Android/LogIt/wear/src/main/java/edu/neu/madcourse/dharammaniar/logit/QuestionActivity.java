package edu.neu.madcourse.dharammaniar.logit;

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
public class QuestionActivity extends Activity implements View.OnClickListener,
        MessageApi.MessageListener, NodeApi.NodeListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    public static final long[] VIBRATE_INTENSE = {1000, 200, 1000, 200, 500, 250, 500, 250};
    private static final String START_ACTIVITY_PATH = "/logit";
    private static String questionType;
    private static String activityString;
    private String TAG = "QuestionActivity";
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
        this.mWakeLock = pm
                .newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.FULL_WAKE_LOCK,
                             "logit");
        this.mWakeLock.acquire();

        super.onCreate(savedInstanceState);
        mSharedPreferences = getSharedPreferences("logit", MODE_PRIVATE);
        mEditor = mSharedPreferences.edit();
        mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        mVibrator.vibrate(VIBRATE_INTENSE, -1);
        questionType = getIntent().getExtras().getString("QuestionType");
        activityString = getIntent().getExtras().getString("Activity");
        if (questionType.equals("OnGoingActivity") || questionType.equals("UnknownActivity") ||
            questionType.equals("ActivityStart")) {
            setContentView(R.layout.question_style3);

            final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
            stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
                @Override
                public void onLayoutInflated(WatchViewStub stub) {
                    question = (TextView) stub.findViewById(R.id.question);
                    answer1 = (Button) stub.findViewById(R.id.answer1);
                    answer2 = (Button) stub.findViewById(R.id.answer2);

                    if (questionType.equals("OnGoingActivity")) {
                        if (activityString.contains("ing")) {
                            question.setText("Are you still " +
                                             activityString + "?");
                        } else {
                            question.setText(
                                    "Is your current activity still - " + activityString + "?");
                        }
                    }
                    if (questionType.equals("UnknownActivity")) {
                            question.setText("Did you mean - " + activityString + "?");
                    }
                    if (questionType.equals("ActivityStart")) {
                        if (activityString.contains("ing")) {
                            question.setText("Are you " + activityString + "?");
                        } else {
                            question.setText("Is your current activity - " + activityString + "?");
                        }
                    }
                    answer1.setText("Yes");
                    answer1.setOnClickListener(QuestionActivity.this);
                    answer2.setText("No");
                    answer2.setOnClickListener(QuestionActivity.this);
                }
            });

        } else {
            setContentView(R.layout.question_style1);
            final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
            stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
                @Override
                public void onLayoutInflated(WatchViewStub stub) {
                    question = (TextView) stub.findViewById(R.id.question);
                    answer1 = (Button) stub.findViewById(R.id.answer1);
                    answer2 = (Button) stub.findViewById(R.id.answer2);
                    answer3 = (Button) stub.findViewById(R.id.answer3);

                    if (questionType.equals("TwoActivities")) {
                        question.setText("What are you doing?");
                        answer1.setText(activityString.split("`")[0]);
                        answer2.setText(activityString.split("`")[1]);
                        answer3.setText("Neither");
                    }
                    if (questionType.equals("ThreeActivities")) {
                        question.setText("What are you doing?");
                        answer1.setText(activityString.split("`")[0]);
                        answer2.setText(activityString.split("`")[1]);
                        answer3.setText(activityString.split("`")[2]);
                    }
                    answer1.setOnClickListener(QuestionActivity.this);
                    answer2.setOnClickListener(QuestionActivity.this);
                    answer3.setOnClickListener(QuestionActivity.this);
                }
            });
        }
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

        if (questionType.equals("OnGoingActivity")) {
            if (v.getId() == answer1.getId()) {
                new StartWearableActivityTask().execute("OnGoingActivity:Yes");
            }
            if (v.getId() == answer2.getId()) {
                new StartWearableActivityTask().execute("OnGoingActivity:No");
            }
        }
        if (questionType.equals("UnknownActivity")) {
            if (v.getId() == answer1.getId()) {
                new StartWearableActivityTask().execute("UnknownActivity:Yes");
            }
            if (v.getId() == answer2.getId()) {
                new StartWearableActivityTask().execute("UnknownActivity:No");
            }
        }
        if (questionType.equals("TwoActivities")) {
            if (v.getId() == answer1.getId()) {
                new StartWearableActivityTask().execute("TwoActivities:" + answer1.getText());
            }
            if (v.getId() == answer2.getId()) {
                new StartWearableActivityTask().execute("TwoActivities:" + answer2.getText());
            }
            if (v.getId() == answer3.getId()) {
                new StartWearableActivityTask().execute("TwoActivities:" + answer3.getText());
            }
        }
        if (questionType.equals("ThreeActivities")) {
            if (v.getId() == answer1.getId()) {
                new StartWearableActivityTask().execute("ThreeActivities:" + answer1.getText());
            }
            if (v.getId() == answer2.getId()) {
                new StartWearableActivityTask().execute("ThreeActivities:" + answer2.getText());
            }
            if (v.getId() == answer3.getId()) {
                new StartWearableActivityTask().execute("ThreeActivities:" + answer3.getText());
            }
        }
        if (questionType.equals("ActivityStart")) {
            if (v.getId() == answer1.getId()) {
                new StartWearableActivityTask().execute("ActivityStart:Yes");
            }
            if (v.getId() == answer2.getId()) {
                new StartWearableActivityTask().execute("ActivityStart:No");
            }
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
