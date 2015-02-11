package edu.neu.madcourse.dharammaniar.twoplayerbananagrams;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import edu.neu.madcourse.dharammaniar.R;

/**
 * Created by Dharam on 10/29/2014.
 */
public class TwoPlayerBananagramsMenu extends Activity implements View.OnClickListener {

    private Button singleGame;
    private Button twoGame;
    private Button highScore;
    private Button acknowledgement;
    private Button quit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_two_player_bananagrams_menu);
        initializeMembers();
    }

    private void initializeMembers() {
        singleGame = (Button) findViewById(
                R.id.activity_two_player_bananagrams_menu_new_single_game);
        singleGame.setOnClickListener(this);
        twoGame = (Button) findViewById(R.id.activity_two_player_bananagrams_menu_new_two_game);
        twoGame.setOnClickListener(this);
        highScore = (Button) findViewById(R.id.activity_two_player_bananagrams_menu_high_score);
        highScore.setOnClickListener(this);
        acknowledgement = (Button) findViewById(
                R.id.activity_two_player_bananagrams_acknowledgement);
        acknowledgement.setOnClickListener(this);
        quit = (Button) findViewById(R.id.activity_two_player_bananagrams_quit);
        quit.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        if (v.getId() == singleGame.getId()) {
            getGCMPreferences(getApplicationContext()).edit().putBoolean("NewGame", true).commit();
            getGCMPreferences(getApplicationContext()).edit().putBoolean("Multiplayer", false)
                                                      .commit();
            getGCMPreferences(getApplicationContext()).edit().putLong("timer", 600000).commit();
            startActivity(
                    new Intent(getApplicationContext(), TwoPlayerBananagramsGame.class));
        }
        if (v.getId() == twoGame.getId()) {
            getGCMPreferences(getApplicationContext()).edit().putBoolean("NewGame", true).commit();
            getGCMPreferences(getApplicationContext()).edit().putBoolean("Multiplayer", true)
                                                      .commit();
            getGCMPreferences(getApplicationContext()).edit().putLong("timer", 600000).commit();
            startActivity(
                    new Intent(getApplicationContext(), TwoPlayerBananagramsFindFriends.class));
        }
        if (v.getId() == highScore.getId()) {
            startActivity(
                    new Intent(getApplicationContext(), TwoPlayerBananagramsDisplayScores.class));
        }
        if (v.getId() == acknowledgement.getId()) {
            startActivity(
                    new Intent(getApplicationContext(), TwoPlayerBananagramsAck.class));
        }
        if (v.getId() == quit.getId()) {
            this.finish();
        }

    }

    private SharedPreferences getGCMPreferences(Context context) {
        return getSharedPreferences("GCMPreferences",
                                    Context.MODE_PRIVATE);
    }
}
