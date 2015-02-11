package edu.neu.madcourse.dharammaniar.bananagrams;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import edu.neu.madcourse.dharammaniar.R;

/**
 * Created by Dharam on 10/1/2014.
 */
public class BananagramsGridActivity extends Activity implements View.OnClickListener {

    private BananagramsHelper helper;

    private GridView board;
    private GridView tiles;
    private String[] boardtiles;
    private String[] usertiles;
    private Button peel;
    private Button pause;
    private Button dump;

    private LinearLayout usertilesLayout;
    private LinearLayout boardLayout;

    private TextView currentBoardTile1;
    private TextView currentBoardTile2;
    private TextView currentUserTile;
    private int currentBoardTile1Position;
    private int currentBoardTile2Position;
    private int currentUserTilePosition;

    private List<String> detectedWords;

    private TextView pointsTextView;
    private TextView time;

    private MediaPlayer mp;
    private MediaPlayer beats;
    private CountDownTimer timer;
    private long millis;

    private Vibrator vibrator;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private List<String> bunch;
    private Random randomGenerator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bananagrams_grid);
        initiateMembers();

    }

    private void initiateMembers() {
        helper = new BananagramsHelper();
        mp = MediaPlayer.create(this, R.raw.beep);
        beats = MediaPlayer.create(this,R.raw.beats);
        beats.setLooping(true);
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        detectedWords = new ArrayList<String>();

        pointsTextView = (TextView) findViewById(R.id.points);
        pointsTextView.setText("Points - 0");

        usertilesLayout = (LinearLayout) findViewById(R.id.user_tiles_layout);
        boardLayout = (LinearLayout) findViewById(R.id.board_layout);

        boardtiles = new String[150];
        usertiles = new String[10];
        for (int i = 0; i < 150; i++) {
            boardtiles[i] = " ";
        }
        for (int i = 0; i < 10; i++) {
            usertiles[i] = " ";
        }
        board = (GridView) findViewById(R.id.board);
        tiles = (GridView) findViewById(R.id.tiles);
        board.setAdapter(new ButtonAdapter(this, boardtiles, true));
        tiles.setAdapter(new ButtonAdapter(this, usertiles, false));
        board.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                vibrator.vibrate(100);
                Toast.makeText(getApplicationContext(), "You Clicked at " + boardtiles[+position],
                               Toast.LENGTH_SHORT).show();
                if (currentBoardTile1 == null) {
                    currentBoardTile1 = (TextView) view;
                    currentBoardTile1Position = position;
                    currentBoardTile1.setBackgroundResource(R.drawable.custom_selected);
                } else {
                    currentBoardTile2 = (TextView) view;
                    currentBoardTile2Position = position;
                    currentBoardTile2.setBackgroundResource(R.drawable.custom_selected);
                }

                if (currentBoardTile1 != null && currentUserTile != null) {
                    moveTile();
                }
                if (currentBoardTile1 != null && currentBoardTile2 != null) {
                    moveTile();
                }

            }
        });
        tiles.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                vibrator.vibrate(100);
                Toast.makeText(getApplicationContext(), "You Clicked at " + usertiles[+position],
                               Toast.LENGTH_SHORT).show();
                if (currentUserTile == null) {
                    currentUserTile = (TextView) view;
                    currentUserTilePosition = position;
                    currentUserTile
                            .setBackgroundResource(R.drawable.custom_selected);
                } else {
                    if (currentUserTile == (TextView) view) {
                        currentUserTile
                                .setBackgroundResource(R.drawable.custom_tile1);
                        currentUserTile = null;
                    }
                }
                if (currentBoardTile1 != null && currentUserTile != null) {
                    moveTile();
                }
                if (currentBoardTile1 != null && currentBoardTile2 != null) {
                    moveTile();
                }
            }
        });

        bunch = helper.initializeBunch();
        randomGenerator = new Random();

        peel = (Button) findViewById(R.id.button_peel);
        peel.setOnClickListener(this);
        pause = (Button) findViewById(R.id.button_pause);
        pause.setOnClickListener(this);
        dump = (Button) findViewById(R.id.button_dump);
        dump.setOnClickListener(this);

        sharedPreferences = getSharedPreferences("Bananagrams", MODE_PRIVATE);
        editor = sharedPreferences.edit();

        usertiles = helper.selectLetters(bunch, randomGenerator);
        tiles.setAdapter(new ButtonAdapter(this, usertiles, false));
        helper.saveUserTiles(usertiles, editor);

        time = (TextView) findViewById(R.id.time);
        millis = sharedPreferences.getLong("timer", 550000);
        timer = new CountDownTimer(millis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                int minutes = (int) (millisUntilFinished / 1000) / 60;
                int seconds = (int) millisUntilFinished / 1000 - (minutes * 60);
                millis = millisUntilFinished;
                time.setText("Time - " + minutes + ":" + seconds);
            }

            @Override
            public void onFinish() {
                boardLayout.setVisibility(View.INVISIBLE);
                usertilesLayout.setVisibility(View.INVISIBLE);
                peel.setVisibility(View.INVISIBLE);
                dump.setVisibility(View.INVISIBLE);
                timer.cancel();
                System.out.print("Saving millis: " + millis);
                editor.putBoolean("pause", false);
                editor.putLong("timer", millis);
                editor.commit();
                pause.setText("Finish");
            }
        };
        timer.start();
        beats.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        boardLayout.setVisibility(View.INVISIBLE);
        usertilesLayout.setVisibility(View.INVISIBLE);
        peel.setVisibility(View.INVISIBLE);
        dump.setVisibility(View.INVISIBLE);
        timer.cancel();
        System.out.print("Saving millis: " + millis);
        editor.putLong("timer", millis);
        editor.putBoolean("pause", true);
        editor.commit();
        beats.pause();
        pause.setText("Resume");
    }

    private void moveTile() {

        if (currentBoardTile1 != null && currentUserTile != null) {
            String text1 = currentBoardTile1.getText().toString();
            String text2 = currentUserTile.getText().toString();
            currentBoardTile1.setText(text2);
            currentUserTile.setText(text1);
            boardtiles[currentBoardTile1Position] = text2;
            usertiles[currentUserTilePosition] = text1;
            currentBoardTile1.setBackgroundResource(R.drawable.custom_tile);
            currentUserTile.setBackgroundResource(R.drawable.custom_tile1);
        }

        if (currentBoardTile1 != null && currentBoardTile2 != null) {
            String text1 = currentBoardTile1.getText().toString();
            String text2 = currentBoardTile2.getText().toString();
            currentBoardTile1.setText(text2);
            currentBoardTile2.setText(text1);
            boardtiles[currentBoardTile1Position] = text2;
            boardtiles[currentBoardTile2Position] = text1;
            currentBoardTile1.setBackgroundResource(R.drawable.custom_tile);
            currentBoardTile2.setBackgroundResource(R.drawable.custom_tile);
        }

        currentBoardTile1 = null;
        currentBoardTile2 = null;
        currentUserTile = null;

        formWords();
    }

    private void formWords() {
        List<String> formedWords = new ArrayList<String>();
        for (int i = 0; i < 150; i++) {
            if (!boardtiles[i].equals(" ")) {
                if (i < 10) {  //First Row
                    if (i == 0) {
                        String check1 = horizontalCheck(i);
                        if (check1 != null) {
                            formedWords.add(check1);
                        }
                        String check2 = verticalCheck(i);
                        if (check2 != null) {
                            formedWords.add(check2);
                        }
                    } else {
                        if (boardtiles[i - 1].equals(" ")) {
                            String check3 = horizontalCheck(i);
                            if (check3 != null) {
                                formedWords.add(check3);
                            }
                        }
                        String check4 = verticalCheck(i);
                        if (check4 != null) {
                            formedWords.add(check4);
                        }
                    }
                } else {
                    if (i % 10 == 0) {  //First Column
                        if (boardtiles[i - 10].equals(" ")) {
                            //Not a part of word so check vertical
                            String check5 = verticalCheck(i);
                            if (check5 != null) {
                                formedWords.add(check5);
                            }
                        }
                        //if (buttons.get(i - 1).getText() != " ") {//Horizontal Check
                        String check6 = horizontalCheck(i);
                        if (check6 != null) {
                            formedWords.add(check6);
                        }
                        //}
                    } else {
                        if (i % 9 == 0) { //Last Column
                            if (boardtiles[i - 10].equals(" ")) {
                                //Not a part of word so check vertical
                                String check7 = verticalCheck(i);
                                if (check7 != null) {
                                    formedWords.add(check7);
                                }
                            }
                        } else {
                            if (i >= 140) {  //Last Row
                                if (boardtiles[i - 1].equals(" ")) {
                                    String check8 = verticalCheck(i);
                                    if (check8 != null) {
                                        formedWords.add(check8);
                                    }
                                }
                            } else {  //Central Tile
                                if (boardtiles[i - 1].equals(" ")) {
                                    String check9 = horizontalCheck(i);
                                    if (check9 != null) {
                                        formedWords.add(check9);
                                    }
                                }
                                if (boardtiles[i - 10].equals(" ")) {
                                    String check10 = verticalCheck(i);
                                    if (check10 != null) {
                                        formedWords.add(check10);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        int points = helper.calculatePoints(formedWords);
        pointsTextView.setText("Points = " + points);
    }

    private String horizontalCheck(int i) {
        String word = "" + boardtiles[i];
        int j = i;
        if (i == 0) {
            while (j < 10) {
                if (!boardtiles[++j].equals(" ")) {
                    word = word + boardtiles[j];
                } else {
                    break;
                }
            }
        } else {
            System.out.println("i = " + i);
            System.out.println("j = " + j);
            while (j < i + (10 - (i % 10))) {
                if (!boardtiles[++j].equals(" ")) {
                    word = word + boardtiles[j];
                } else {
                    break;
                }
            }
        }
        System.out.println("Checking Horizontal Words = " + word);
        return checkWord(word);
    }

    private String verticalCheck(int i) {
        String word = "" + boardtiles[i];
        int j = i + 10;
        while (j < 150) {
            if (!boardtiles[j].equals(" ")) {
                word = word + boardtiles[j];
                j = j + 10;
            } else {
                break;
            }
        }
        System.out.println("Checking Vertical Words = " + word);
        return checkWord(word);

    }

    private String checkWord(String input) {
        List<String> pointWords = new ArrayList<String>();
        AssetManager am = getAssets();
        try {
            if (input.length() > 1) {
                String word = input.toLowerCase();
                InputStream is = am.open(word.substring(0, 2) + ".txt");
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                List<String> words = new ArrayList<String>();
                String line;
                while ((line = reader.readLine()) != null) {
                    words.add(line);
                }
                if (words.contains(word)) {
                    pointWords.add(word);
                    if (!detectedWords.contains(word)) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mp.start();
                                vibrator.vibrate(500);
                            }
                        });
                        detectedWords.add(word);
                    }
                    System.out.println("WORD FOUND = " + word);
                    return word;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void onClick(View v) {
        vibrator.vibrate(100);
        if (v.getId() == peel.getId()) {
            Toast.makeText(getApplicationContext(), "Peel", Toast.LENGTH_SHORT).show();
            usertiles = helper.peel(usertiles, bunch, randomGenerator);
            tiles.setAdapter(new ButtonAdapter(getApplicationContext(), usertiles, false));
        }

        if (v.getId() == dump.getId()) {
            Toast.makeText(getApplicationContext(), "Dump", Toast.LENGTH_SHORT).show();
            if (currentUserTile != null) {
                bunch.add(usertiles[currentUserTilePosition]);
                usertiles[currentUserTilePosition] = " ";
                usertiles = helper.dump(usertiles, bunch, randomGenerator);
                tiles.setAdapter(new ButtonAdapter(getApplicationContext(), usertiles, false));
            }
        }

        if (v.getId() == pause.getId()) {
            Toast.makeText(getApplicationContext(), "Pause", Toast.LENGTH_SHORT).show();
            if (pause.getText().toString().equals("Finish")) {
                this.finish();
            }

            if (pause.getText().toString().equals("Pause")) {
                boardLayout.setVisibility(View.INVISIBLE);
                usertilesLayout.setVisibility(View.INVISIBLE);
                peel.setVisibility(View.INVISIBLE);
                dump.setVisibility(View.INVISIBLE);
                timer.cancel();
                System.out.print("Saving millis: " + millis);
                editor.putLong("timer", millis);
                editor.putBoolean("pause", true);
                editor.commit();
                pause.setText("Resume");
                if (beats.isPlaying()) {
                    beats.pause();
                }
            } else {
                boardLayout.setVisibility(View.VISIBLE);
                usertilesLayout.setVisibility(View.VISIBLE);
                peel.setVisibility(View.VISIBLE);
                dump.setVisibility(View.VISIBLE);
                pause.setText("Pause");
                editor.putBoolean("pause", false);
                editor.commit();
                System.out.println("Timer" + sharedPreferences.getLong("timer", 600000));
                millis = sharedPreferences.getLong("timer", 600000);
                timer = new CountDownTimer(millis, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        int minutes = (int) (millisUntilFinished / 1000) / 60;
                        int seconds = (int) millisUntilFinished / 1000 - (minutes * 60);
                        millis = millisUntilFinished;
                        time.setText("Time - " + minutes + ":" + seconds);
                    }

                    @Override
                    public void onFinish() {
                        boardLayout.setVisibility(View.INVISIBLE);
                        usertilesLayout.setVisibility(View.INVISIBLE);
                        peel.setVisibility(View.INVISIBLE);
                        dump.setVisibility(View.INVISIBLE);
                        timer.cancel();
                        System.out.print("Saving millis: " + millis);
                        editor.putLong("timer", millis);
                        editor.commit();
                        pause.setText("Finish");
                    }
                }.start();
                beats.start();
            }
        }
    }

    public class ButtonAdapter extends BaseAdapter {

        private Context mContext;
        private String tiles[];
        private Boolean areBoardTiles;

        public ButtonAdapter(Context c, String[] tiles, Boolean areBoardTiles) {
            mContext = c;
            this.tiles = tiles;
            this.areBoardTiles = areBoardTiles;
        }

        @Override
        public int getCount() {
            return tiles.length;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView btn;
            if (convertView == null) {
                // if it's not recycled, initialize some attributes
                btn = new TextView(mContext);
                int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 30,
                                                             getResources().getDisplayMetrics());
                int width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 30,
                                                            getResources().getDisplayMetrics());
                btn.setLayoutParams(new GridView.LayoutParams(height, width));
            } else {
                btn = (TextView) convertView;
            }
            btn.setText(tiles[position]);
            btn.setGravity(Gravity.CENTER);
            btn.setTextColor(Color.WHITE);
            if (areBoardTiles) {
                btn.setBackgroundResource(R.drawable.custom_tile);
            } else {
                btn.setBackgroundResource(R.drawable.custom_tile1);
            }
            btn.setId(position);
            return btn;
        }
    }
}


