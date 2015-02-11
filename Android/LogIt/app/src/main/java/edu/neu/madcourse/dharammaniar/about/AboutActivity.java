package edu.neu.madcourse.dharammaniar.about;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.widget.TextView;

import edu.neu.madcourse.dharammaniar.R;


public class AboutActivity extends Activity {

    private TextView imei;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        TelephonyManager telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        imei = (TextView) findViewById(R.id.activity_about_textview_imei);
        imei.setText("IMEI: " + telephonyManager.getDeviceId());
    }

}
