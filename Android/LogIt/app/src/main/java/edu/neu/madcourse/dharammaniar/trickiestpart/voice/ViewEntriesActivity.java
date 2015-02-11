package edu.neu.madcourse.dharammaniar.trickiestpart.voice;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import edu.neu.madcourse.dharammaniar.R;

public class ViewEntriesActivity extends Activity {

	SharedPreferences preferences;
	Context context;

	private ListView wordList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.view_entries);
		context = this;

		wordList = (ListView) findViewById(R.id.word_list);

		displayEntries();
	}

	private void displayEntries() {
		preferences = this.getSharedPreferences("Project Voice Command", Context.MODE_PRIVATE);
		String entries = preferences.getString("entries", "");
		if (entries != "") {
			String[] entriesArray = entries.split(",");
			List<String> entriesList = Arrays.asList(entriesArray);
			wordList.setAdapter(new ArrayAdapter<String> (this, android.R.layout.simple_list_item_1, entriesList));
		}
	}

	public void onClickClear(View view) {
		preferences = this.getSharedPreferences("Project Voice Command", Context.MODE_PRIVATE);
		Editor editor = preferences.edit();
		editor.putString("entries", "");
		editor.commit();
		wordList.setAdapter(new ArrayAdapter<String> (this, android.R.layout.simple_list_item_1, new ArrayList<String>()));
	}

}