package edu.neu.madcourse.dharammaniar.trickiestpart.time;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;

import edu.neu.madcourse.dharammaniar.R;
import edu.neu.madcourse.dharammaniar.trickiestpart.Constants;

public class TimeModuleMainMenu extends Activity {

	SharedPreferences preferences;
	Context context;
	String activities;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.time_main_menu);

		loadDefaultActivities();

		context = this;
	}

	public void onClickConfigureTimeBasedActivities(View view) {
		Intent intent = new Intent(this, DefaultTimeBasedActivity.class);
		startActivity(intent);
	}

	public void loadDefaultActivities() {
		preferences = this.getSharedPreferences(Constants.APPLICATION_NAME, Context.MODE_PRIVATE);
		activities = preferences.getString("activities", "");
		if (activities == "") {
			Editor editor = preferences.edit();
			editor.putString("activities", Constants.DEFAULT_ACTIVITIES);
			editor.commit();
		}
	}

}