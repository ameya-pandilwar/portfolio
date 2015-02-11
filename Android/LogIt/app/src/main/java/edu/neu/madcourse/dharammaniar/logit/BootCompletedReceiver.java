package edu.neu.madcourse.dharammaniar.logit;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;

/**
 * Created by Dharam on 11/25/2014.
 */
public class BootCompletedReceiver extends BroadcastReceiver {

    private AlarmManager am;
    private PendingIntent minuteService;

    @Override
    public void onReceive(Context context, Intent arg1) {
        // TODO Auto-generated method stub
        am = (AlarmManager) context
                .getSystemService(Context.ALARM_SERVICE);
        minuteService = PendingIntent.getService(context, 45678,
                                                 new Intent(context,
                                                            MinuteService.class), 0);
        am.cancel(minuteService);

        long firstTime = SystemClock.elapsedRealtime();
        am.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, firstTime, 1000 * 60, minuteService);
    }

}