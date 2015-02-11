package edu.neu.madcourse.dharammaniar.logit;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import edu.neu.madcourse.dharammaniar.R;

/**
 * Created by Dharam on 12/7/2014.
 */
public class FinalProject extends Activity implements View.OnClickListener {

    private Button startApp;
    private TextView description;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_final_project);
        startApp = (Button) findViewById(R.id.activity_final_project_startApp);
        startApp.setOnClickListener(this);
        description = (TextView) findViewById(R.id.activity_final_project_description);

        description.setText(
                "Ever wonder where all of your time is going? What are the time consuming activities " +
                "you do throughout the day? How much time do you dedicate the important activities " +
                "and how much time is wasted? With LogIt, you will now be able to manage your time " +
                "very easily. This app will enable you to log the activities you do during the day. " +
                "The intuitive and unobtrusive data collection process will be seamlessly integrated " +
                "with a review and reorganize utility." +
                "\n\n" +
                "LogIt is better than all the current life labeling apps on the play store because it allows " +
                "the user to keep a track of all the activities the user is doing throughout the day without " +
                "spending too much time recording them." +
                "\n\n" +
                "The major features of logit are:" +
                "\n" +
                "- One-tap logging using Android Wear \n" +
                "- Voice logging using Android Wear \n" +
                "- Activity notification feature   \n" +
                "- Output in different forms\n" +
                "- Track number of hours per activity\n" +
                "\n\n" +
                "Team Members:\n" +
                "- Dharam Maniar - maniar.d@husky.neu.edu\n" +
                "- Ameya Pandilwar - pandilwar.a@husky.neu.edu");
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == startApp.getId()) {
            startActivity(new Intent(getApplicationContext(), LogItSplashScreen.class));
        }
    }
}
