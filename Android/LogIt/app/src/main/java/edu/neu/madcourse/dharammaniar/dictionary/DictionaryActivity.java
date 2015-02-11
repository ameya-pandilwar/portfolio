package edu.neu.madcourse.dharammaniar.dictionary;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.AssetManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import edu.neu.madcourse.dharammaniar.R;

/**
 * Created by Dharam on 9/20/2014.
 */
public class DictionaryActivity extends Activity implements View.OnClickListener {

    private EditText input;
    private Button clear;
    private Button back;
    private Button acknowledgements;
    private ArrayList<String> matchingWords;

    private ListView listView;
    private ArrayAdapter<String> adapter;
    private Activity activity;
    private MediaPlayer mp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dictionary);
        activity = this;
        input = (EditText) findViewById(R.id.activity_dictionary_edittext_input);
        listView = (ListView) findViewById(R.id.activity_dictionary_listview_words);
        clear = (Button) findViewById(R.id.activity_dictionary_button_clear);
        clear.setOnClickListener(this);
        back = (Button) findViewById(R.id.activity_dictionary_button_back);
        back.setOnClickListener(this);
        acknowledgements = (Button) findViewById(R.id.activity_dictionary_button_acknowledgements);
        acknowledgements.setOnClickListener(this);
        matchingWords = new ArrayList<String>();
        adapter = new ArrayAdapter<String>(activity,
                                           android.R.layout.simple_list_item_1,
                                           android.R.id.text1,
                                           matchingWords);
        mp = MediaPlayer.create(this, R.raw.beep);
        input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                AssetManager am = getAssets();
                try {
                    if (input.length() > 1) {
                        String word = input.getText().toString().toLowerCase();
                        InputStream is = am.open(word.substring(0, 2) + ".txt");
                        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                        List<String> words = new ArrayList<String>();
                        String line;
                        while ((line = reader.readLine()) != null) {
                            words.add(line);
                        }
                        if (words.contains(word)) {
                            if (!matchingWords.contains(word)) {
                                matchingWords.add(word);
                                listView.setAdapter(adapter);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        mp.start();
                                    }
                                });
                            }
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == clear.getId()) {
            input.setText("");
            matchingWords.clear();
            listView.setAdapter(adapter);
        }
        if (v.getId() == back.getId()) {
            this.finish();
        }
        if (v.getId() == acknowledgements.getId()) {
            final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
            alertDialog.setTitle("Acknowledgements");
            alertDialog.setMessage("1) WordList:\nhttp://wordlist.sourceforge.net/ \n" +
                                   "2) Beep Sound:\nhttp://www.soundjay.com/");
            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Close",
                                  new DialogInterface.OnClickListener() {
                                      public void onClick(DialogInterface dialog, int which) {
                                          alertDialog.dismiss();
                                      }
                                  });
            alertDialog.show();
        }

    }
}
