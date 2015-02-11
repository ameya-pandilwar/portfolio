package edu.neu.madcourse.dharammaniar.logit;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;
import edu.neu.madcourse.dharammaniar.R;

/**
 * Created by Ameya on 04-12-2014.
 */
public class LogItEntryListAdapter extends ArrayAdapter<LogItEntry> {
    private int resource;
    private LayoutInflater inflater;
    private Context context;

    ImageView editIcon;
    ImageView discardIcon;

    public LogItEntryListAdapter(Context ctx, int resourceId, List<LogItEntry> objects) {
        super(ctx, resourceId, objects);
        resource = resourceId;
        inflater = LayoutInflater.from(ctx);
        context = ctx;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = (RelativeLayout) inflater.inflate(resource, null);
        final LogItEntry entry = getItem(position);
        ImageView entryCategory = (ImageView) convertView.findViewById(R.id.logItEntryCategory);
        Drawable image = entry.getCategory();
        entryCategory.setImageDrawable(image);

        TextView entryActivity = (TextView) convertView.findViewById(R.id.logItEntryActivity);
        entryActivity.setText(entry.getActivity());

        TextView entryStartTime = (TextView) convertView.findViewById(R.id.logItEntryStartTime);
        entryStartTime.setText(String.valueOf(entry.getStartTime()));

        TextView entryEndTime = (TextView) convertView.findViewById(R.id.logItEntryEndTime);
        entryEndTime.setText(String.valueOf(entry.getEndTime()));

        TextView entryLatitude = (TextView) convertView.findViewById(R.id.logItEntryLatitude);
        entryLatitude.setText(String.valueOf(entry.getLatitude()));

        TextView entryLongitude = (TextView) convertView.findViewById(R.id.logItEntryLongitude);
        entryLongitude.setText(String.valueOf(entry.getLongitude()));

        TextView entryTimeSpent = (TextView) convertView.findViewById(R.id.logItEntryTimeSpent);
        entryTimeSpent.setText(entry.getTimeSpent());

        editIcon = (ImageView) convertView.findViewById(R.id.logItEditEntry);
        discardIcon = (ImageView) convertView.findViewById(R.id.logItDiscardEntry);

        editIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, LogItEditEntryScreen.class);
                intent.putExtra("entry-category", entry.getCategoryString());
                intent.putExtra("entry-activity", entry.getActivity());
                intent.putExtra("entry-starttime", entry.getStartTime());
                intent.putExtra("entry-endtime", entry.getEndTime());
                intent.putExtra("entry-date", entry.getDate());
                context.startActivity(intent);
            }
        });

        discardIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, LogItDeleteEntryScreen.class);
                intent.putExtra("entry-category", entry.getCategoryString());
                intent.putExtra("entry-activity", entry.getActivity());
                intent.putExtra("entry-starttime", entry.getStartTime());
                intent.putExtra("entry-endtime", entry.getEndTime());
                intent.putExtra("entry-date", entry.getDate());
                context.startActivity(intent);
            }
        });

        return convertView;
    }

}
