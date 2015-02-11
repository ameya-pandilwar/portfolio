package edu.neu.madcourse.dharammaniar.twoplayerbananagrams;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import edu.neu.madcourse.dharammaniar.R;


public class TwoPlayerBananagramsAck extends Activity {

    private TextView ack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bananagrams_ackowledgements);
        setTitle("Acknowledgements");
        ack = (TextView) findViewById(R.id.activity_bananagrams_ack_tv);
        ack.setText("1) WordList:\n" +
                    "http://wordlist.sourceforge.net/ \n" +
                    "2) Sound:\n" +
                    "http://www.soundjay.com/\n" +
                    "3) Bananagrams Rules:\n" +
                    "http://www.toycrossing.com/bananagrams/rules.shtml\n" +
                    "4) GCM:\n" +
                    "GCM Demo provided on Piazza \n" +
                    "5) Server Connectivity:\n" +
                    "Own Server with php functions and mysql database"
        );
    }

}
