package edu.neu.madcourse.dharammaniar.communication;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import edu.neu.madcourse.dharammaniar.R;

/**
 * Created by Dharam on 10/28/2014.
 */
public class Acknowledgement extends Activity{

    private TextView ack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_communication_ackowledgements);
        ack = (TextView) findViewById(R.id.activity_communication_ack_tv);
        ack.setText("1) GCM:\n" +
                    "GCM Demo provided on Piazza \n" +
                    "2) Server Connectivity:\n" +
                    "Own Server with php functions and mysql database/\n"
        );
    }
}
