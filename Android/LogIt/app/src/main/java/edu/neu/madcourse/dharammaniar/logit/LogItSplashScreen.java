package edu.neu.madcourse.dharammaniar.logit;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import edu.neu.madcourse.dharammaniar.R;

public class LogItSplashScreen extends Activity {

    SharedPreferences preferences;
    private EditText splashScreenName;
    private TextView splashScreenMessage;
    private Button splashScreenNext;

    private boolean mActive = true;
    private int mSplashTime = 2000;

    boolean firstLaunch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_it_splash_screen);

        splashScreenName = (EditText) findViewById(R.id.splashScreenName);
        splashScreenMessage = (TextView) findViewById(R.id.splashScreenMessage);
        splashScreenNext = (Button) findViewById(R.id.splashScreenNext);
        splashScreenNext.setVisibility(View.INVISIBLE);

        preferences = this.getSharedPreferences(LogItConstants.APPLICATION_NAME, Context.MODE_PRIVATE);

        firstLaunch = preferences.getBoolean("FirstLaunch", true);

        if (firstLaunch) {

            splashScreenName.addTextChangedListener(new TextWatcher() {

                public void afterTextChanged(Editable s) {
                }

                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (splashScreenName.getText().toString() != "") {
                        splashScreenNext.setVisibility(View.VISIBLE);
                    } else {
                        splashScreenNext.setVisibility(View.INVISIBLE);
                    }
                }
            });

        } else {
            String name = preferences.getString("Name", "");
            splashScreenMessage.setText("\nHi " + name + "!\n\n" + splashScreenMessage.getText());
            splashScreenName.setVisibility(View.INVISIBLE);
            splashScreenNext.setVisibility(View.INVISIBLE);

            Thread splashThread = new Thread() {
                @Override
                public void run() {
                    try {
                        int waited = 0;
                        while (mActive && (waited < mSplashTime)) {
                            sleep(100);
                            if (mActive) {
                                waited += 100;
                            }
                        }
                    } catch (InterruptedException e) {

                    } finally {
                        if (firstLaunch) {
                            Intent intent = new Intent(getApplicationContext(),
                                    LogItWearConnectivityTutorialScreen.class);
                            startActivity(intent);
                        } else {
                            Intent intent = new Intent(getApplicationContext(),
                                    LogItMainScreen.class);
                            startActivity(intent);
                        }
                        finish();
                    }
                }
            };
            splashThread.start();
        }

    }

    public void onClickNext(View view) {
        String name = splashScreenName.getText().toString();
        if (name != "") {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("Name", name);
            editor.commit();
            Intent intent = new Intent(LogItSplashScreen.this, LogItWearConnectivityTutorialScreen.class);
            startActivity(intent);
        } else {
            Toast.makeText(this, "Enter Your Name", Toast.LENGTH_SHORT).show();
        }
    }

}
