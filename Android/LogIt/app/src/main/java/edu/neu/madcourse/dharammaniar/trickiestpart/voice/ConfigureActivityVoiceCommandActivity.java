package edu.neu.madcourse.dharammaniar.trickiestpart.voice;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import edu.neu.madcourse.dharammaniar.R;

public class ConfigureActivityVoiceCommandActivity extends Activity {

	private static final int START_KEYWORD_REQUEST = 888;
	private static final int STOP_KEYWORD_REQUEST = 999;
	SharedPreferences preferences;
	Context context;

	EditText startKeyword, stopKeyword;
	TextView activityHeading;
	String activity;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.configure_activity_voice_commands);
		context = this;

		startKeyword = (EditText) findViewById(R.id.editText1);
		stopKeyword = (EditText) findViewById(R.id.editText2);
		
		activityHeading = (TextView) findViewById(R.id.activityHeading);
		
		Intent intent = getIntent();
		activity = intent.getStringExtra("activity");
		
		loadKeywords();
	}

	public void onClickRecordStartKeyword(View view) {
		configureStartKeyword();
	}

	public void onClickRecordStopKeyword(View view) {
		configureStopKeyword();
	}

	private void configureStartKeyword() {
		Intent listenIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
		listenIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getClass().getPackage().getName());
		listenIntent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Say keyword to start activity!");
		listenIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
		listenIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 10);
		startActivityForResult(listenIntent, START_KEYWORD_REQUEST);
	}
	
	private void configureStopKeyword() {
		Intent listenIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
		listenIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getClass().getPackage().getName());
		listenIntent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Say keyword to stop activity!");
		listenIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
		listenIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 10);
		startActivityForResult(listenIntent, STOP_KEYWORD_REQUEST);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == START_KEYWORD_REQUEST && resultCode == RESULT_OK) {
			ArrayList<String> suggestedWords = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
			startKeyword.setText(suggestedWords.get(0));
		}
		if (requestCode == STOP_KEYWORD_REQUEST && resultCode == RESULT_OK) {
			ArrayList<String> suggestedWords = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
			stopKeyword.setText(suggestedWords.get(0));
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	public void onClickSaveActivityKeywords(View view) {
		if (startKeyword.getText().toString() != "" && stopKeyword.getText().toString() != "") {
			saveKeywords(startKeyword.getText().toString(), stopKeyword.getText().toString());
		}
	}
	
	private void saveKeywords(String start, String stop) {
		preferences = this.getSharedPreferences("Project Voice Command", Context.MODE_PRIVATE);
		Editor editor = preferences.edit();
		editor.putString(activity+"-start", start);
		editor.putString(activity+"-stop", stop);
		editor.commit();
		Toast.makeText(context, "Keywords Saved For "+activity, Toast.LENGTH_SHORT).show();
		finish();
	}
	
	private void loadKeywords() {
		activityHeading.setText(activity);
		preferences = this.getSharedPreferences("Project Voice Command", Context.MODE_PRIVATE);
		String start = preferences.getString(activity+"-start", "");
		String stop = preferences.getString(activity+"-stop", "");
		startKeyword.setText(start);
		stopKeyword.setText(stop);
	}

}