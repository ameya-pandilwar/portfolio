package edu.neu.madcourse.dharammaniar.logit;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;

import edu.neu.madcourse.dharammaniar.R;

/**
 * Created by Ameya on 25-11-2014.
 */
public class LogItMainScreen extends Activity {

    Context context;
    private AlarmManager am;
    private PendingIntent minuteService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_it_main_screen);


        am = (AlarmManager) getApplicationContext()
                .getSystemService(Context.ALARM_SERVICE);
        minuteService = PendingIntent.getService(getApplicationContext(), 45678,
                new Intent(getApplicationContext(),
                        MinuteService.class), 0);
        am.cancel(minuteService);

        long firstTime = SystemClock.elapsedRealtime();
        am.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, firstTime, 1000 * 60, minuteService);


        context = this;
    }

    public void onClickManageActivities(View view) {
        Intent intent = new Intent(context, LogItManageActivitiesScreen.class);
        startActivity(intent);
    }

    public void onClickSetupDefaultActivities(View view) {
        Intent intent = new Intent(context, LogItSetupDefaultActivitiesScreen.class);
        startActivity(intent);
    }

    public void onClickRecordActivityManually(View view) {
        Intent intent = new Intent(context, LogItRecordManualEntryScreen.class);
        startActivity(intent);
    }

    public void onClickViewLogs(View view) {
        Intent intent = new Intent(context, LogItLogsScreen.class);
        startActivity(intent);
    }

    public void onClickAcknowledgements(View view) {
        Intent intent = new Intent(context, LogItAcknowledgements.class);
        startActivity(intent);
    }

    public void onClickQuit(View view) {
        finish();
    }
}
