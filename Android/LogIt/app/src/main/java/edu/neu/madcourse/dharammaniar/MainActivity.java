package edu.neu.madcourse.dharammaniar;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import edu.neu.madcourse.dharammaniar.bananagrams.BananagramsMainActivity;
import edu.neu.madcourse.dharammaniar.communication.FindUserActivity;
import edu.neu.madcourse.dharammaniar.communication.RegistrationActivity;
import edu.neu.madcourse.dharammaniar.dictionary.DictionaryActivity;
import edu.neu.madcourse.dharammaniar.logit.FinalProject;
import edu.neu.madcourse.dharammaniar.sudoku.SudokuMainActivity;
import edu.neu.madcourse.dharammaniar.trickiestpart.TrickiestPart;
import edu.neu.madcourse.dharammaniar.twoplayerbananagrams.TwoPlayerBananagramsMenu;
import edu.neu.madcourse.dharammaniar.twoplayerbananagrams.TwoPlayerBananagramsRegistration;


public class MainActivity extends Activity implements View.OnClickListener {

    private Button about;
    private Button error;
    private Button sudoku;
    private Button dictionary;
    private Button bananagrams;
    private Button communication;
    private Button twoPlayerBananagrams;
    private Button trickiestPart;
    private Button finalProject;
    private Button quit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Dharam Maniar");
        initializeMembers();
    }

    private void initializeMembers() {
        about = (Button) findViewById(R.id.activity_main_button_about);
        about.setOnClickListener(this);
        error = (Button) findViewById(R.id.activity_main_button_error);
        error.setOnClickListener(this);
        sudoku = (Button) findViewById(R.id.activity_main_button_sudoku);
        sudoku.setOnClickListener(this);
        dictionary = (Button) findViewById(R.id.activity_main_button_dictionary);
        dictionary.setOnClickListener(this);
        bananagrams = (Button) findViewById(R.id.activity_main_button_bananagrams);
        bananagrams.setOnClickListener(this);
        communication = (Button) findViewById(R.id.activity_main_button_communication);
        communication.setOnClickListener(this);
        twoPlayerBananagrams = (Button) findViewById(R.id.activity_main_button_two_player_bananagrams);
        twoPlayerBananagrams.setOnClickListener(this);
        trickiestPart = (Button) findViewById(R.id.activity_main_button_trickiest);
        trickiestPart.setOnClickListener(this);
        finalProject = (Button) findViewById(R.id.activity_main_button_final_project);
        finalProject.setOnClickListener(this);
        quit = (Button) findViewById(R.id.activity_main_button_quit);
        quit.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == about.getId()) {
            Intent AboutActivity = new Intent(getApplicationContext(),
                                              edu.neu.madcourse.dharammaniar.about.AboutActivity.class);
            startActivity(AboutActivity);
        }
        if (v.getId() == error.getId()) {
            MainActivity mainActivity = null;
            mainActivity.initializeMembers();
        }
        if (v.getId() == sudoku.getId()) {
            Intent sudokuActivity = new Intent(getApplicationContext(), SudokuMainActivity.class);
            startActivity(sudokuActivity);
        }
        if (v.getId() == dictionary.getId()) {
            Intent dictionaryActivity = new Intent(getApplicationContext(),
                                                   DictionaryActivity.class);
            startActivity(dictionaryActivity);
        }
        if (v.getId() == bananagrams.getId()) {
            Intent bananagramsActivity = new Intent(getApplicationContext(),
                                                    BananagramsMainActivity.class);
            startActivity(bananagramsActivity);
        }
        if (v.getId() == communication.getId()) {
            if (getUsername(getApplicationContext()).equals("username")) {
                Intent communicationActivity = new Intent(getApplicationContext(),
                                                          RegistrationActivity.class);
                startActivity(communicationActivity);
            } else {
                Intent findUserActivity = new Intent(getApplicationContext(),
                                                     FindUserActivity.class);
                startActivity(findUserActivity);
            }
        }
        if(v.getId() == twoPlayerBananagrams.getId()) {
            if(getUsername(getApplicationContext()).equals("username")) {
                startActivity(new Intent(getApplicationContext(),TwoPlayerBananagramsRegistration.class));
            }
            else {
                startActivity(new Intent(getApplicationContext(),TwoPlayerBananagramsMenu.class));
            }
        }
        if(v.getId() == trickiestPart.getId()) {
            startActivity(new Intent(getApplicationContext(),TrickiestPart.class));
        }
        if(v.getId() == finalProject.getId()) {
            startActivity(new Intent(getApplicationContext(),FinalProject.class));
        }
        if (v.getId() == quit.getId()) {
            this.finish();
        }
    }

    private String getUsername(Context context) {
        final SharedPreferences prefs = getGCMPreferences(context);
        return prefs.getString("username", "username");
    }

    private SharedPreferences getGCMPreferences(Context context) {
        return getSharedPreferences("GCMPreferences",
                                    Context.MODE_PRIVATE);
    }
}
