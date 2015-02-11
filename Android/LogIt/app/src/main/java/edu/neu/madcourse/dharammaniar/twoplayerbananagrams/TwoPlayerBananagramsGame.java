package edu.neu.madcourse.dharammaniar.twoplayerbananagrams;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
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

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import edu.neu.madcourse.dharammaniar.R;
import edu.neu.madcourse.dharammaniar.communication.GcmNotification;

/**
 * Created by Dharam on 10/29/2014.
 */
public class TwoPlayerBananagramsGame extends Activity implements View.OnClickListener {

    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    GoogleCloudMessaging gcm;
    SharedPreferences prefs;
    Context context;
    String regid;
    private TwoPlayerBananagramsHelper helper;
    private GridView board;
    private GridView tiles;
    private String[] boardtiles;
    private String[] usertiles;
    private Button peel;
    private Button pause;
    private Button dump;
    private Button quit;
    private TextView opponentName;
    private TextView opponentScore;
    private LinearLayout opponentDetails;
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
    private long timeLeft;
    private int points;
    private String gameState;
    private ButtonAdapter boardButtonAdapter;
    private String[] opponentTiles;
    private Activity activity;
    private SensorManager mSensorManager;
    private float mAccel; // acceleration apart from gravity
    private float mAccelCurrent; // current acceleration including gravity
    private float mAccelLast; // last acceleration including gravity
    private final SensorEventListener mSensorListener = new SensorEventListener() {

        public void onSensorChanged(SensorEvent se) {
            float x = se.values[0];
            float y = se.values[1];
            float z = se.values[2];
            mAccelLast = mAccelCurrent;
            mAccelCurrent = (float) Math.sqrt((double) (x * x + y * y + z * z));
            float delta = mAccelCurrent - mAccelLast;
            mAccel = mAccel * 0.9f + delta; // perform low-cut filter
            if (mAccel > 12) {
                String gameState = helper
                        .saveGameState(boardtiles, usertiles, bunch, points, timeLeft);
                sendMessage("Shake", gameState);
            }
        }

        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_two_player_bananagrams_grid);
        initiateMembers();

    }

    @Override
    protected void onResume() {
        super.onResume();
        getGCMPreferences(getApplicationContext()).edit().putBoolean("GameOn", true).commit();
        mSensorManager.registerListener(mSensorListener,
                                        mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                                        SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getGCMPreferences(getApplicationContext()).edit().putBoolean("GameOn", false).commit();
    }

    private void initiateMembers() {

        activity = this;
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensorManager.registerListener(mSensorListener,
                                        mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                                        SensorManager.SENSOR_DELAY_NORMAL);
        mAccel = 0.00f;
        mAccelCurrent = SensorManager.GRAVITY_EARTH;
        mAccelLast = SensorManager.GRAVITY_EARTH;
        regid = getGCMPreferences(getApplicationContext()).getString("registration_id", "");

        helper = new TwoPlayerBananagramsHelper();
        mp = MediaPlayer.create(this, R.raw.beep);
        beats = MediaPlayer.create(this, R.raw.beats);
        beats.setLooping(true);
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        detectedWords = new ArrayList<String>();

        pointsTextView = (TextView) findViewById(R.id.points);
        pointsTextView.setText("Points - 0");
        points = 0;
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
        opponentTiles = getOpponentBoardTiles();
        boardButtonAdapter = new ButtonAdapter(this, boardtiles, true, opponentTiles);
        board.setAdapter(boardButtonAdapter);
        tiles.setAdapter(new ButtonAdapter(this, usertiles, false, opponentTiles));
        board.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                System.out.println("position = " + position);
                vibrator.vibrate(100);
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

        bunch = helper.initializeBunch(getApplicationContext());
        randomGenerator = new Random();

        peel = (Button) findViewById(R.id.button_peel);
        peel.setOnClickListener(this);
        pause = (Button) findViewById(R.id.button_pause);
        pause.setOnClickListener(this);
        dump = (Button) findViewById(R.id.button_dump);
        dump.setOnClickListener(this);
        quit = (Button) findViewById(R.id.button_quit);
        quit.setOnClickListener(this);

        sharedPreferences = getSharedPreferences("GCMPreferences", MODE_PRIVATE);
        editor = sharedPreferences.edit();

        usertiles = helper.selectLetters(bunch, randomGenerator);
        tiles.setAdapter(new ButtonAdapter(this, usertiles, false, opponentTiles));
        gameState = helper.saveGameState(boardtiles, usertiles, bunch, points, timeLeft);

        opponentName = (TextView) findViewById(R.id.opponent_name);
        opponentScore = (TextView) findViewById(R.id.opponent_points);
        opponentDetails = (LinearLayout) findViewById(R.id.opponent_details);

        String opponentNameString = getGCMPreferences(getApplicationContext())
                .getString("friend", "opponent");
        int opponentPoints = getGCMPreferences(getApplicationContext()).getInt("opponentPoints", 0);
        if (!getGCMPreferences(getApplicationContext())
                .getBoolean("Multiplayer", false)) {
            opponentName.setVisibility(View.INVISIBLE);
            opponentScore.setVisibility(View.INVISIBLE);
        } else {
            opponentName.setText("Opponent: " + opponentNameString);
            opponentScore.setText("Points: " + opponentPoints);
            if (getGCMPreferences(getApplicationContext()).getBoolean("NewGame", false)) {
                sendMessage("NewGame", gameState);
            }
        }

        time = (TextView) findViewById(R.id.time);
        millis = sharedPreferences.getLong("timer", 600000);
        timer = new CountDownTimer(millis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                int minutes = (int) (millisUntilFinished / 1000) / 60;
                int seconds = (int) millisUntilFinished / 1000 - (minutes * 60);
                millis = millisUntilFinished;
                time.setText("Time - " + minutes + ":" + seconds);
                timeLeft = millisUntilFinished;
                if (!isInternetAvailable()) {
                    pauseGame();
                }
                if (getGCMPreferences(getApplicationContext()).getBoolean("Shake", false)) {
                    shakeUserTiles();
                    getGCMPreferences(getApplicationContext()).edit().putBoolean("Shake", false)
                                                              .commit();
                }
                if (getGCMPreferences(getApplicationContext())
                        .getBoolean("Multiplayer", false)) {
                    opponentTiles = getOpponentBoardTiles();
                    boardButtonAdapter = new ButtonAdapter(activity, boardtiles, true,
                                                           opponentTiles);
                    board.setAdapter(boardButtonAdapter);
                    bunch = helper.getBunch(getApplicationContext());
                    opponentScore
                            .setText("Points: " +
                                     getGCMPreferences(activity).getInt("opponentPoints", 0));
                    String gameState = helper
                            .saveGameState(boardtiles, usertiles, bunch, points, timeLeft);
                    sendMessage("GameState", gameState);
                }
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
                updateScore();
            }
        };
        timer.start();
        beats.start();
    }

    private void shakeUserTiles() {
        vibrator.vibrate(500);
        usertiles = helper.shake(usertiles, randomGenerator);
        tiles.setAdapter(new ButtonAdapter(getApplicationContext(), usertiles, false,
                                           opponentTiles));
    }

    private String[] getOpponentBoardTiles() {
        bunch = helper.getBunch(getApplicationContext());
        String opponentBoardTilesString = getGCMPreferences(getApplicationContext())
                .getString("opponentBoardTiles", "");
        if (!opponentBoardTilesString.equals("")) {
            String opponentBoardTiles[] = opponentBoardTilesString.split("`");
            return opponentBoardTiles;
        } else {
            String opponentBoardTiles[] = new String[150];
            for (int i = 0 ; i < 150 ; i++) {
                opponentBoardTiles[i] = " ";
            }
            return opponentBoardTiles;
        }
    }

    private SharedPreferences getGCMPreferences(Context context) {
        return getSharedPreferences("GCMPreferences",
                                    Context.MODE_PRIVATE);
    }

    @Override
    protected void onPause() {
        mSensorManager.unregisterListener(mSensorListener);
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

        points = helper.calculatePoints(formedWords);
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
                    String gameState = helper
                            .saveGameState(boardtiles, usertiles, bunch, points, timeLeft);
                    sendMessage("NewWord", gameState);
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

        if (v.getId() == quit.getId()) {
            this.finish();
        }

        if (v.getId() == peel.getId()) {
            bunch = helper.getBunch(getApplicationContext());
            usertiles = helper.peel(usertiles, bunch, randomGenerator);
            tiles.setAdapter(new ButtonAdapter(getApplicationContext(), usertiles, false,
                                               opponentTiles));
        }

        if (v.getId() == dump.getId()) {
            if (currentUserTile != null) {
                bunch = helper.getBunch(getApplicationContext());
                bunch.add(usertiles[currentUserTilePosition]);
                usertiles[currentUserTilePosition] = " ";
                usertiles = helper.dump(usertiles, bunch, randomGenerator);
                tiles.setAdapter(new ButtonAdapter(getApplicationContext(), usertiles, false,
                                                   opponentTiles));
            }
        }

        if (v.getId() == pause.getId()) {
            if (pause.getText().toString().equals("Finish")) {
                this.finish();
            }

            if (pause.getText().toString().equals("Pause")) {
                pauseGame();
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
                        timeLeft = millisUntilFinished;
                        if (getGCMPreferences(getApplicationContext())
                                .getBoolean("Multiplayer", false)) {
                            opponentTiles = getOpponentBoardTiles();
                            boardButtonAdapter = new ButtonAdapter(activity, boardtiles, true,
                                                                   opponentTiles);
                            board.setAdapter(boardButtonAdapter);
                            opponentScore
                                    .setText("Points: " + getGCMPreferences(activity)
                                            .getInt("opponentPoints", 0));
                            String gameState = helper
                                    .saveGameState(boardtiles, usertiles, bunch, points, timeLeft);
                            sendMessage("GameState", gameState);
                        }
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
                        updateScore();
                    }
                }.start();
                beats.start();
            }
        }
    }

    private void pauseGame() {
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
    }

    private void sendMessage(final String messageTpye, final String message) {
        if (regid == null || regid.equals("")) {
            Toast.makeText(this, "You must register first", Toast.LENGTH_LONG)
                 .show();
            return;
        }

        if (!checkPlayServices()) {
            Toast.makeText(this, "Device not supported", Toast.LENGTH_LONG).show();
            return;
        }

        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg = "";

                List<String> regIds = new ArrayList<String>();
                String reg_device;
                Map<String, String> msgParams;
                msgParams = new HashMap<String, String>();
                msgParams.put("data.username",
                              getGCMPreferences(getApplicationContext()).getString("username", ""));
                msgParams.put("data.gcmid", regid);
                msgParams.put("data.notifType", messageTpye);
                msgParams.put("data.message", message);
                GcmNotification gcmNotification = new GcmNotification();
                regIds.clear();
                reg_device = getGCMPreferences(getApplicationContext())
                        .getString("friend_id", "friend_id");
                regIds.add(reg_device);
                gcmNotification.sendNotification(msgParams, regIds, getApplicationContext());
                msg = "sending information...";
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
            }
        }.execute(null, null, null);
    }

    private boolean isInternetAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(
                Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni == null) {
            // There are no active networks.
            return false;
        } else {
            return true;
        }
    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                                                      PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                finish();
            }
            return false;
        }
        return true;
    }

    private void updateScore() {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                try {
                    HttpGet httpGet = new HttpGet(
                            "http://www.dharammaniar.com/numad/score.php?tag=enterScore&username=" +
                            getGCMPreferences(getApplicationContext())
                                    .getString("username", "username")
                            + "&score=" + points);
                    HttpParams httpParameters = new BasicHttpParams();
                    int timeoutConnection = 3000;
                    HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
                    int timeoutSocket = 5000;
                    HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
                    DefaultHttpClient httpClient = new DefaultHttpClient(httpParameters);
                    HttpResponse httpResponse = httpClient.execute(httpGet);
                    HttpEntity httpEntity = httpResponse.getEntity();
                    InputStream is = httpEntity.getContent();
                    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(is, "iso-8859-1"), 200);
                    String response = reader.readLine();
                    System.out.println("JSON Response = " + response);
                    msg = response;

                } catch (IOException ex)

                {
                    msg = "Error :" + ex.getMessage();
                }

                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
                if (msg.equals("true")) {
                    activity.finish();
                }
            }
        }.execute(null, null, null);
    }

    public class ButtonAdapter extends BaseAdapter {

        private Context mContext;
        private String tiles[];
        private Boolean areBoardTiles;
        private String opponentTiles[];

        public ButtonAdapter(Context c, String[] tiles, Boolean areBoardTiles,
                             String[] opponentTiles) {
            mContext = c;
            this.tiles = tiles;
            this.areBoardTiles = areBoardTiles;
            this.opponentTiles = opponentTiles;
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
                if (tiles[position].equals(" ") && (!opponentTiles[position].equals(" "))) {
                    btn.setBackgroundResource(R.drawable.custom_opponent_tile);
                } else {
                    btn.setBackgroundResource(R.drawable.custom_tile);
                }
            } else {
                btn.setBackgroundResource(R.drawable.custom_tile1);
            }
            btn.setId(position);
            return btn;
        }
    }
}


