package edu.neu.madcourse.dharammaniar.bananagrams;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import edu.neu.madcourse.dharammaniar.R;


public class BananagramsMainActivity extends Activity implements View.OnClickListener {

    private Button resume;
    private Button easy;
    private Button medium;
    private Button difficult;
    private Button acknowledgement;
    private Button quit;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bananagrams_main);
        setTitle("Word Game");
        initializeMembers();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initializeMembers();
    }

    private void initializeMembers() {

        sharedPreferences = getSharedPreferences("Bananagrams", MODE_PRIVATE);
        editor = sharedPreferences.edit();

        resume = (Button) findViewById(R.id.activity_bananagrams_main_resume);
        resume.setOnClickListener(this);
        easy = (Button) findViewById(R.id.activity_bananagrams_easy);
        easy.setOnClickListener(this);
        medium= (Button) findViewById(R.id.activity_bananagrams_medium);
        medium.setOnClickListener(this);
        difficult= (Button) findViewById(R.id.activity_bananagrams_difficult);
        difficult.setOnClickListener(this);
        acknowledgement = (Button) findViewById(R.id.activity_bananagrams_acknowledgement);
        acknowledgement.setOnClickListener(this);
        quit = (Button) findViewById(R.id.activity_bananagrams_quit);
        quit.setOnClickListener(this);

        if(!sharedPreferences.getBoolean("pause",false))
        {
            resume.setVisibility(View.INVISIBLE);
        }
        else
        {
            resume.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void onClick(View v) {
        if(v.getId() == resume.getId()) {
            Intent resumeActivity = new Intent(getApplicationContext(),BananagramsGridActivity.class);
            editor.putString("game","resume");
            editor.commit();
            startActivity(resumeActivity);
        }
        if(v.getId() == easy.getId()) {
            Intent easyGame = new Intent(getApplicationContext(),BananagramsGridActivity.class);
            editor.putLong("timer",600000);
            editor.putString("game","easy");
            editor.commit();
            startActivity(easyGame);
        }
        if(v.getId() == medium.getId()) {
            Intent mediumGame = new Intent(getApplicationContext(),BananagramsGridActivity.class);
            editor.putLong("timer",300000);
            editor.putString("game","medium");
            editor.commit();
            startActivity(mediumGame);
        }
        if(v.getId() == difficult.getId()) {
            Intent difficultActivity = new Intent(getApplicationContext(),BananagramsGridActivity.class);
            editor.putLong("timer",300000);
            editor.putString("game","difficult");
            editor.commit();
            startActivity(difficultActivity);
        }
        if(v.getId() == acknowledgement.getId()) {
            Intent acknowledgmentActivity = new Intent(getApplicationContext(),BananagramsAck.class);
            startActivity(acknowledgmentActivity);
        }
        if(v.getId() == quit.getId()) {
            this.finish();
        }
    }
}
