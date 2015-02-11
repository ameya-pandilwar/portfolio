package edu.neu.madcourse.dharammaniar.trickiestpart;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;

import edu.neu.madcourse.dharammaniar.R;
import edu.neu.madcourse.dharammaniar.trickiestpart.location.LocationActivity;
import edu.neu.madcourse.dharammaniar.trickiestpart.time.TimeBasedService;
import edu.neu.madcourse.dharammaniar.trickiestpart.time.TimeModuleMainMenu;
import edu.neu.madcourse.dharammaniar.trickiestpart.voice.VoiceModuleMainMenu;
import edu.neu.madcourse.dharammaniar.trickiestpart.wear.Wear;

/**
 * Created by Dharam on 11/20/2014.
 */
public class TrickiestPart extends Activity implements View.OnClickListener {

    Context context;
    private Button wear;
    private Button voice;
    private Button location;
    private Button time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.activity_trickiest_main);
        wear = (Button) findViewById(R.id.activity_trickiest_main_wear);
        wear.setOnClickListener(this);
        voice = (Button) findViewById(R.id.activity_trickiest_main_audio);
        voice.setOnClickListener(this);
        location = (Button) findViewById(R.id.activity_trickiest_main_location);
        location.setOnClickListener(this);
        time = (Button) findViewById(R.id.activity_trickiest_main_time);
        time.setOnClickListener(this);

        initiateTimeBasedService();
    }

    private void initiateTimeBasedService() {
        PendingIntent minuteService = PendingIntent.getService(this, 10000, new Intent(this, TimeBasedService.class), 0);
        AlarmManager am = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        am.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(), 1000 * 60, minuteService);
    }

    @Override
    public void onClick(View v) {

        if (v.getId() == wear.getId()) {
            startActivity(new Intent(getApplicationContext(), Wear.class));
        }
        if (v.getId() == voice.getId()) {
            startActivity(new Intent(getApplicationContext(), VoiceModuleMainMenu.class));
        }
        if (v.getId() == location.getId()) {
            startActivity(new Intent(getApplicationContext(), LocationActivity.class));
        }
        if (v.getId() == time.getId()) {
            startActivity(new Intent(getApplicationContext(), TimeModuleMainMenu.class));
        }
    }
}
