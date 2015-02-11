package edu.neu.madcourse.dharammaniar;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;

import edu.neu.madcourse.dharammaniar.logit.QuestionActivity;
import edu.neu.madcourse.dharammaniar.logit.VoiceActivity;
import edu.neu.madcourse.dharammaniar.trickiestpart.TestQuestionActivity;
import edu.neu.madcourse.dharammaniar.trickiestpart.TestVoiceActivity;

/**
 * Created by Dharam on 11/20/2014.
 */
public class ListenerService extends WearableListenerService {

    private static final String TAG = "ListenerService";

    private static final String START_ACTIVITY_PATH = "/logit";

    private GoogleApiClient mGoogleApiClient;

    @Override
    public void onCreate() {
        super.onCreate();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        String message = new String(messageEvent.getData());

        // Check to see if the message is a question from the phone
        if (messageEvent.getPath().equals(START_ACTIVITY_PATH) && (!message.equals("Error"))) {

            if (message.equals("test-question")) {
                Intent testQuestionIntent = new Intent(getApplicationContext(),
                                                       TestQuestionActivity.class);
                testQuestionIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(testQuestionIntent);
            }
            if (message.equals("test-voice")) {
                Intent testVoiceIntent = new Intent(getApplicationContext(),
                                                    TestVoiceActivity.class);
                testVoiceIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(testVoiceIntent);
            }
            if (message.equals("checkNewActivity")) {
                Intent voiceIntent = new Intent(getApplicationContext(),
                                                    VoiceActivity.class);
                voiceIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(voiceIntent);
            }
            if (message.contains("checkActivityOn")) {
                Bundle extras = new Bundle();
                Intent questionIntent = new Intent(getApplicationContext(),
                                                       QuestionActivity.class);
                questionIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                extras.putString("QuestionType", "OnGoingActivity");
                extras.putString("Activity",message.split(":")[1]);
                questionIntent.putExtras(extras);
                startActivity(questionIntent);
            }
            if(message.contains("checkTwoActivities")) {
                Bundle extras = new Bundle();
                Intent questionIntent = new Intent(getApplicationContext(),QuestionActivity.class);
                questionIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                extras.putString("QuestionType", "TwoActivities");
                extras.putString("Activity",message.split(":")[1]);
                questionIntent.putExtras(extras);
                startActivity(questionIntent);
            }
            if(message.contains("checkThreeActivities")) {
                Bundle extras = new Bundle();
                Intent questionIntent = new Intent(getApplicationContext(),QuestionActivity.class);
                questionIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                extras.putString("QuestionType","ThreeActivities");
                extras.putString("Activity",message.split(":")[1]);
                questionIntent.putExtras(extras);
                startActivity(questionIntent);
            }
            if(message.contains("checkUnknownActivity")) {
                Bundle extras = new Bundle();
                Intent questionIntent = new Intent(getApplicationContext(),QuestionActivity.class);
                questionIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                extras.putString("QuestionType","UnknownActivity");
                extras.putString("Activity",message.split(":")[1]);
                questionIntent.putExtras(extras);
                startActivity(questionIntent);
            }
            if(message.contains("checkActivityStart")) {
                Bundle extras = new Bundle();
                Intent questionIntent = new Intent(getApplicationContext(),QuestionActivity.class);
                questionIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                extras.putString("QuestionType","ActivityStart");
                extras.putString("Activity",message.split(":")[1]);
                questionIntent.putExtras(extras);
                startActivity(questionIntent);
            }
        }
    }
}

