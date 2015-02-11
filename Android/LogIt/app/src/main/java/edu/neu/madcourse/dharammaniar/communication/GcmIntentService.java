package edu.neu.madcourse.dharammaniar.communication;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import edu.neu.madcourse.dharammaniar.R;
import edu.neu.madcourse.dharammaniar.twoplayerbananagrams.TwoPlayerBananagramsGame;
import edu.neu.madcourse.dharammaniar.twoplayerbananagrams.TwoPlayerBananagramsHelper;

/**
 * Created by Dharam on 10/21/2014.
 */
public class GcmIntentService extends IntentService {

    public static final int NOTIFICATION_ID = 1;
    static final String TAG = "GCM_Communication";
    NotificationCompat.Builder builder;
    private NotificationManager mNotificationManager;

    public GcmIntentService() {
        super(CommunicationConstants.GCM_SENDER_ID);
    }

    public GcmIntentService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        if (!extras.isEmpty()) {
            final SharedPreferences prefs = getGCMPreferences(getApplicationContext());
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("friend", extras.getString("username"));
            editor.putString("friend_id", extras.getString("gcmid"));
            editor.commit();
            if (extras.getString("notifType").equals("Notif")) {
                sendNotification("NUMAD14F-DharamManiar", "Communication Test",
                                 "Notification from " + getGCMPreferences(getApplicationContext())
                                         .getString("friend", "friend"));
            }
            if (extras.getString("notifType").equals("NewGame")) {
                TwoPlayerBananagramsHelper helper = new TwoPlayerBananagramsHelper();
                helper.getGameState(extras.getString("message"), getApplicationContext());
                getGCMPreferences(getApplicationContext()).edit().putBoolean("NewGame", false)
                                                          .commit();
                getGCMPreferences(getApplicationContext()).edit().putBoolean("Multiplayer", true)
                                                          .commit();
                getGCMPreferences(getApplicationContext()).edit().putLong("timer", 600000).commit();
                startGameNotification("NUMAD14F-DharamManiar", "Bananagrams",
                                      "Game challenge from " +
                                      getGCMPreferences(getApplicationContext())
                                              .getString("friend", "friend"));
            }
            if (extras.getString("notifType").equals("NewWord")) {
                if (!getGCMPreferences(getApplicationContext()).getBoolean("GameOn", false)) {
                    TwoPlayerBananagramsHelper helper = new TwoPlayerBananagramsHelper();
                    helper.getGameState(extras.getString("message"), getApplicationContext());
                    getGCMPreferences(getApplicationContext()).edit().putBoolean("NewGame", false)
                                                              .commit();
                    getGCMPreferences(getApplicationContext()).edit()
                                                              .putBoolean("Multiplayer", true)
                                                              .commit();
                    startGameNotification("NUMAD14F-DharamManiar", "Bananagrams",
                                          getGCMPreferences(getApplicationContext())
                                                  .getString("friend", "friend") +
                                          " made a new word!");
                }
            }
            if (extras.getString("notifType").equals("GameState")) {
                TwoPlayerBananagramsHelper helper = new TwoPlayerBananagramsHelper();
                helper.getGameState(extras.getString("message"), getApplicationContext());
            }
            if (extras.getString("notifType").equals("Shake")) {
                getGCMPreferences(getApplicationContext()).edit().putBoolean("Shake", true)
                                                          .commit();
            }
        }
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

    public void sendNotification(String alertText, String titleText,
                                 String contentText) {
        mNotificationManager = (NotificationManager) this
                .getSystemService(Context.NOTIFICATION_SERVICE);
        Intent notificationIntent;
        notificationIntent = new Intent(this,
                                        edu.neu.madcourse.dharammaniar.communication.CommunicationActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        notificationIntent.putExtra("show_response", "show_response");
        PendingIntent intent = PendingIntent.getActivity(this, 0, new Intent(
                                                                 this, CommunicationActivity.class),
                                                         PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                this)
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle(titleText)
                .setStyle(
                        new NotificationCompat.BigTextStyle()
                                .bigText(contentText))
                .setContentText(contentText).setTicker(alertText)
                .setAutoCancel(true);
        mBuilder.setContentIntent(intent);
        Notification note = mBuilder.build();
        note.defaults |= Notification.DEFAULT_VIBRATE;
        note.defaults |= Notification.DEFAULT_SOUND;
        mNotificationManager.notify(NOTIFICATION_ID, note);
    }

    public void startGameNotification(String alertText, String titleText,
                                      String contentText) {
        mNotificationManager = (NotificationManager) this
                .getSystemService(Context.NOTIFICATION_SERVICE);
        Intent notificationIntent;
        notificationIntent = new Intent(this,
                                        edu.neu.madcourse.dharammaniar.communication.CommunicationActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        notificationIntent.putExtra("show_response", "show_response");
        PendingIntent intent = PendingIntent.getActivity(this, 0, new Intent(
                                                                 this,
                                                                 TwoPlayerBananagramsGame.class),
                                                         PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                this)
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle(titleText)
                .setStyle(
                        new NotificationCompat.BigTextStyle()
                                .bigText(contentText))
                .setContentText(contentText).setTicker(alertText)
                .setAutoCancel(true);
        mBuilder.setContentIntent(intent);
        Notification note = mBuilder.build();
        note.defaults |= Notification.DEFAULT_VIBRATE;
        note.defaults |= Notification.DEFAULT_SOUND;
        mNotificationManager.notify(NOTIFICATION_ID, note);
    }

    private SharedPreferences getGCMPreferences(Context context) {
        return getSharedPreferences("GCMPreferences",
                                    Context.MODE_PRIVATE);
    }
}
