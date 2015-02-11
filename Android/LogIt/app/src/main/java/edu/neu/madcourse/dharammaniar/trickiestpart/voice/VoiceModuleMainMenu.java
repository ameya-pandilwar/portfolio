package edu.neu.madcourse.dharammaniar.trickiestpart.voice;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.view.View;
import android.widget.Toast;

import edu.neu.madcourse.dharammaniar.R;

public class VoiceModuleMainMenu extends Activity implements OnInitListener {

	private static final int VR_REQUEST = 999;
	private int MY_DATA_CHECK_CODE = 0;
	SharedPreferences preferences;
	Context context;
	String activities;

	private TextToSpeech messageTTS;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.voice_main_menu);

		loadDefaultActivities();

		context = this;

		Intent checkTTSIntent = new Intent(); 
		checkTTSIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA); 
		startActivityForResult(checkTTSIntent, MY_DATA_CHECK_CODE);
	}

	public void onClickAddEntryUsingVoice(View view) {
		listenToSpeech();
	}

	public void onClickConfigureVoiceCommands(View view) {
		Intent intent = new Intent(this, DefaultVoiceCommandActivity.class);
		startActivity(intent);
	}

	public void onClickViewEntries(View view) {
		Intent intent = new Intent(this, ViewEntriesActivity.class);
		startActivity(intent);
	}

	private void listenToSpeech() {
		Intent listenIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
		listenIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getClass().getPackage().getName());
		listenIntent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Describe your current activity!");
		listenIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
		listenIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 10);
		startActivityForResult(listenIntent, VR_REQUEST);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == VR_REQUEST && resultCode == RESULT_OK) {
			ArrayList<String> suggestedWords = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
			checkForActivityKeywords(suggestedWords);
		}

		if (requestCode == MY_DATA_CHECK_CODE) { 
			if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) 
				messageTTS = new TextToSpeech(this, this); 
			else { 
				Intent installTTSIntent = new Intent(); 
				installTTSIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA); 
				startActivity(installTTSIntent); 
			} 
		}

		super.onActivityResult(requestCode, resultCode, data);
	}

	public void onInit(int initStatus) {
		if (initStatus == TextToSpeech.SUCCESS)  
			messageTTS.setLanguage(Locale.US);
	}

	public void checkForActivityKeywords(ArrayList<String> words) {
		preferences = this.getSharedPreferences("Project Voice Command", Context.MODE_PRIVATE);
		String start, stop;
		boolean match = false;
		String entry = "";
		for (String string : words) {
			String[] activitiesSplit = activities.split(",");
			if (!match) {
				for (String activitySplit : activitiesSplit) {
					start = preferences.getString(activitySplit+"-start", "");
					stop = preferences.getString(activitySplit+"-stop", "");
					if (string.equalsIgnoreCase(start)) {
						messageTTS.speak("Adding entry for starting "+activitySplit+" activity", TextToSpeech.QUEUE_FLUSH, null);
						match = true;
						preferences = this.getSharedPreferences("Project Voice Command", Context.MODE_PRIVATE);
						String entries = preferences.getString("entries", "");
						if (entries != "") {
							entries = entries + ",";
						}
						Editor editor = preferences.edit();
						String timestamp = new SimpleDateFormat("MMM dd. yyyy ; HH:mm ; ").format(new Date());
						editor.putString("entries", entries+timestamp+activitySplit);
						editor.commit();
						entry = activitySplit;
						break;
					}
					if (string.equalsIgnoreCase(stop)) {
						messageTTS.speak("Adding entry for stopping "+activitySplit+" activity", TextToSpeech.QUEUE_FLUSH, null);
						preferences = this.getSharedPreferences("Project Voice Command", Context.MODE_PRIVATE);
						String entries = preferences.getString("entries", "");
						if (entries != "") {
							List<String> entriesList = Arrays.asList(entries.split(","));
							String latestEntry = entriesList.get(entriesList.size()-1);
							String[] latestEntrySplit = latestEntry.split(";");
							if (latestEntrySplit[2].trim().equalsIgnoreCase(activitySplit)) {
								String endtime = new SimpleDateFormat("HH:mm ; ").format(new Date());
								String updatedEntry = latestEntrySplit[0].trim() +" ; "+ latestEntrySplit[1].trim() + " - " + endtime +" "+ latestEntrySplit[2].trim();
								entries = entries.replace(latestEntry, updatedEntry);
								Editor editor = preferences.edit();
								editor.putString("entries", entries);
								editor.commit();
							}
						}
						match = true;
						entry = activitySplit;
						break;
					}
				}
			} else {
				break;
			}
		}

		if (!match) {
			messageTTS.speak("No activities detected for keyword", TextToSpeech.QUEUE_FLUSH, null);
		} else {
			Toast.makeText(context, "Entry Added Successfully For "+entry, Toast.LENGTH_SHORT).show();
		}
	}

	public void loadDefaultActivities() {
		preferences = this.getSharedPreferences("Project Voice Command", Context.MODE_PRIVATE);
		activities = preferences.getString("activities", "");
		if (activities == "") {
			Editor editor = preferences.edit();
			editor.putString("activities", "Eating,Sleeping,Driving,Working,Shopping");
			editor.commit();
		}
	}

	@Override
	protected void onDestroy() {
		if (messageTTS != null) {
			messageTTS.stop();
			messageTTS.shutdown();
		}
		super.onDestroy();
	}
}