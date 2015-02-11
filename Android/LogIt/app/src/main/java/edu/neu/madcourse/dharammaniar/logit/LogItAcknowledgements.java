package edu.neu.madcourse.dharammaniar.logit;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import edu.neu.madcourse.dharammaniar.R;

/**
 * Created by Dharam on 12/7/2014.
 */
public class LogItAcknowledgements extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_it_ackowledgements);
    }

    public void onClickBack(View view) {
        finish();
    }
}
