package edu.neu.madcourse.dharammaniar.logit;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

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
import edu.neu.madcourse.dharammaniar.logit.support.SharedPrefs;

/**
 * Created by Ameya on 25-11-2014.
 */
public class LogItWearVoiceTutorialScreen extends Activity
        implements DataApi.DataListener, MessageApi.MessageListener,
        NodeApi.NodeListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    final static String APPLICATION_NAME = "LogIt";
    private static final String TAG = "LogItWearQuestionTutorialScreen";
    private static final String START_ACTIVITY_PATH = "/logit";
    SharedPreferences preferences;
    private TextView wearVoiceTutorialScreenGreeting, wearVoiceTutorialScreenMessage;
    private ImageView wearVoiceTutorialScreenFinish;
    private Button wearVoiceTutorialScreenRetry;
    private GoogleApiClient mGoogleApiClient;
    private boolean mResolvingError = false;

    private Boolean isQuestionAnswered = false;
    private Boolean isNextActivityStarted = false;
    private int isQuestionAnsweredCount = 0;
    private CountDownTimer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_it_wear_voice_tutorial_screen);

        wearVoiceTutorialScreenGreeting = (TextView) findViewById(
                R.id.wearVoiceTutorialScreenGreeting);
        wearVoiceTutorialScreenMessage = (TextView) findViewById(
                R.id.wearVoiceTutorialScreenMessage);
        wearVoiceTutorialScreenFinish = (ImageView) findViewById(
                R.id.wearVoiceTutorialScreenFinish);
        wearVoiceTutorialScreenRetry = (Button) findViewById(R.id.wearVoiceRetry);
        preferences = this.getSharedPreferences(APPLICATION_NAME, Context.MODE_PRIVATE);
        String name = preferences.getString("Name", "");

        wearVoiceTutorialScreenGreeting
                .setText("Fantastic " + name + "!\nJust One More Thing & We're Done...");
        wearVoiceTutorialScreenMessage
                .setText("Say 'Having Lunch' On Your Wear Device When Prompted");
        wearVoiceTutorialScreenFinish.setVisibility(View.INVISIBLE);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API).addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).build();

        if (!mResolvingError) {
            mGoogleApiClient.connect();
        }

        timer = new CountDownTimer(5 * 60 * 1000, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {

                isQuestionAnswered = SharedPrefs
                        .getBoolean("TEST_VOICE_ANSWERED", false, getApplicationContext());

                if (isQuestionAnswered) {
                    if (isQuestionAnsweredCount == 2) {
                        if (!isNextActivityStarted) {
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putBoolean("FirstLaunch", false);
                            editor.commit();
                            Intent intent = new Intent(LogItWearVoiceTutorialScreen.this,
                                                       LogItMainScreen.class);
                            startActivity(intent);
                            this.cancel();
                            isNextActivityStarted = true;
                        }
                    } else {
                        wearVoiceTutorialScreenMessage.setText("You Said - " + SharedPrefs
                                .getString("TEST_VOICE_ANSWER", "", getApplicationContext()));
                        wearVoiceTutorialScreenRetry.setVisibility(View.INVISIBLE);
                        wearVoiceTutorialScreenFinish.setVisibility(View.VISIBLE);
                        isQuestionAnsweredCount++;
                    }
                }

            }

            @Override
            public void onFinish() {

            }
        };
        timer.start();


        new StartWearableActivityTask().execute("test-voice");
    }

    public void onClickRetry(View view) {
        new StartWearableActivityTask().execute("test-voice");
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

    private Collection<String> getNodes() {
        HashSet<String> results = new HashSet<String>();
        NodeApi.GetConnectedNodesResult nodes = Wearable.NodeApi
                .getConnectedNodes(mGoogleApiClient).await();

        for (Node node : nodes.getNodes()) {
            results.add(node.getId());
        }

        return results;
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
}
