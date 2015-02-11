package edu.neu.madcourse.dharammaniar.trickiestpart.voice;

import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import edu.neu.madcourse.dharammaniar.R;
import edu.neu.madcourse.dharammaniar.trickiestpart.Constants;

public class DefaultVoiceCommandsActivity extends Activity {

	SharedPreferences preferences;
	Context context;

	private ListView wordList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.default_activities);
		context = this;

		wordList = (ListView) findViewById(R.id.word_list);

		wordList.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				TextView activity = (TextView) view;
				Intent intent = new Intent(context, ConfigureActivityVoiceCommandActivity.class);
				intent.putExtra("activity", activity.getText().toString());
				startActivity(intent);
			}
		});

		displayDefaultActivities();
	}

	private void displayDefaultActivities() {
		preferences = this.getSharedPreferences(Constants.APPLICATION_NAME, Context.MODE_PRIVATE);
		String activities = preferences.getString("activities", "");
		if (activities != "") {
			String[] activitiesArray = activities.split(",");
			List<String> activitiesList = Arrays.asList(activitiesArray);
			wordList.setAdapter(new ArrayAdapter<String> (this, android.R.layout.simple_list_item_1, activitiesList));
		}
	}

}