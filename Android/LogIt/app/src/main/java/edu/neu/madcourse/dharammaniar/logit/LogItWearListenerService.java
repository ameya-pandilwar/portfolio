package edu.neu.madcourse.dharammaniar.logit;

import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;

/**
 * Created by Dharam Maniar.
 * ListenerService runs whenever the phone receives an answer from the watch.
 * It decodes the answer and writes it to a CSV File.
 */
public class LogItWearListenerService extends WearableListenerService {

    private static final String TAG = "LogItWearListenerService";

    private static final String START_ACTIVITY_PATH = "/logit";

    GoogleApiClient mGoogleApiClient;


    @Override
    public void onCreate() {
        super.onCreate();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .build();
        mGoogleApiClient.connect();
    }

    /*
     * On receiving the answer from the watch, decode it and write it to the answers.csv file.
     * Location of answers.csv: /sdcard/wear/answers.csv
     */

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        String message = new String(messageEvent.getData());
        // Check to see if the message is an answer from the watch
        if (messageEvent.getPath().equals(START_ACTIVITY_PATH) && (!message.equals("Error"))) {
            if (message.contains("answer:")) {
                Toast.makeText(getApplicationContext(),
                               "Answer Received = " + message.split(":")[1], Toast.LENGTH_SHORT)
                     .show();
            }
            if (message.contains("voice:")) {
                Toast.makeText(getApplicationContext(),
                               "Voice Received = " + message.split(":")[1], Toast.LENGTH_SHORT)
                     .show();
            }
        }
    }

    @Override
    public void onPeerConnected(Node peer) {
    }

    @Override
    public void onPeerDisconnected(Node peer) {
    }

}
