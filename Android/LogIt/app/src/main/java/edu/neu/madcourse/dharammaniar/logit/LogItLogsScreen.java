package edu.neu.madcourse.dharammaniar.logit;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import edu.neu.madcourse.dharammaniar.R;

/**
 * Created by Ameya on 29-11-2014.
 */
public class LogItLogsScreen extends Activity {

    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_it_logs_screen);

        context = this;
    }

    public void onClickViewCharts(View view) {
        Intent intent = new Intent(this, LogItViewChartsScreen.class);
        startActivity(intent);
    }

    public void onClickExportToCSV(View view) {
        Intent intent = new Intent(this, LogItExportToCSVScreen.class);
        startActivity(intent);
    }

    public void onClickBack(View view) {
        finish();
    }

}
