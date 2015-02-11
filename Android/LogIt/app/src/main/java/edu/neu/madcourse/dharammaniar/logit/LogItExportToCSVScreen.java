package edu.neu.madcourse.dharammaniar.logit;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.Gravity;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;
import edu.neu.madcourse.dharammaniar.R;

/**
 * Created by Ameya on 07-12-2014.
 */
public class LogItExportToCSVScreen extends Activity {

    private static final String TAG = "MAD14F";
    private DatePicker fromDate, toDate;
    Context context;
    String message = "";
    String exportFileName;
    String from, to;
    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_it_export_to_csv_screen);

        context = this;

        fromDate = (DatePicker) findViewById(R.id.exportFromDate);
        toDate = (DatePicker) findViewById(R.id.exportToDate);
        preferences = this.getSharedPreferences(LogItConstants.APPLICATION_NAME, Context.MODE_PRIVATE);

        exportFileName = generateFileName();
    }

    private String generateFileName() {
        String name = "export.csv";
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy_HH-mm-ss");
        name = "LogIt_" + sdf.format(new Date());
        return name;
    }

    public void onClickExportToCSV(View view) {
        boolean readyForExport = prepareFileForExport();

        if (readyForExport) {
            Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
            emailIntent.setType("plain/text");
            emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{""});
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "LogIt - Exported Data From " + from + " To " + to);
            emailIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file:///mnt/sdcard/.logit/export/" + exportFileName + ".csv"));
            String name = preferences.getString("Name", "");
            String body = "Hi " + name + ",\n\n";
            body = body.concat("Please find attached the exported entries of LogIt from " + from + " to " + to + ".\n\n");
            body = body.concat("Thanks & Regards,\nTeam LogIt.");
            emailIntent.putExtra(Intent.EXTRA_TEXT, body);
            startActivity(Intent.createChooser(emailIntent, "Send .csv file via..."));
        } else {
            Toast toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
            TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
            if( v != null) v.setGravity(Gravity.CENTER);
            toast.show();
        }
    }

    public String getDateInStringFormat(int dayOfMonth, int monthOfYear, int year) {
        String day, month;
        if (dayOfMonth < 10) {
            day = "0" + String.valueOf(dayOfMonth);
        } else {
            day = String.valueOf(dayOfMonth);
        }
        if ((monthOfYear + 1) < 10) {
            month = "0" + String.valueOf(monthOfYear + 1);
        } else {
            month = String.valueOf(monthOfYear + 1);
        }
        return day + "-" + month + "-" + String.valueOf(year);
    }

    private boolean prepareFileForExport() {
        boolean result = false;

        from = getDateInStringFormat(fromDate.getDayOfMonth(), fromDate.getMonth(), fromDate.getYear());
        to = getDateInStringFormat(toDate.getDayOfMonth(), toDate.getMonth(), toDate.getYear());

        boolean valid = false;
        try {
            valid = areDatesEligibleForExport(from, to);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (valid) {
            prepareFile(getListOfFiles(from, to));
            result = true;
        }

        return result;
    }

    private List<String> getListOfFiles(String from, String to) {
        List<String> files = new ArrayList<String>();

        files.add(from);
        while (!from.equalsIgnoreCase(to)) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            Calendar c = Calendar.getInstance();
            try {
                c.setTime(sdf.parse(from));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            c.add(Calendar.DATE, 1);
            String next = sdf.format(c.getTime());
            if (next.equalsIgnoreCase(to)) {
                break;
            } else {
                files.add(next);
                from = next;
            }
        }
        files.add(to);

        return files;
    }

    private void prepareFile(List<String> files) {
        List<String[]> stringEntries = new ArrayList<String[]>();
        File activityDirectory = new File(Environment.getExternalStorageDirectory() + "/.logit/export/");
        if (!activityDirectory.exists()) {
            activityDirectory.mkdirs();
        }
        File exportFile = new File(Environment.getExternalStorageDirectory() + "/.logit/export/" + exportFileName +".csv");
        if (!exportFile.exists()) {
            try {
                exportFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        CSVWriter mEntryWriter = null;
        String[] entryString = new String[7];

        try {
            mEntryWriter = new CSVWriter(new FileWriter(exportFile, true));
            entryString[0] = "Date";
            entryString[1] = "Category";
            entryString[2] = "Activity";
            entryString[3] = "Start Time";
            entryString[4] = "End Time";
            entryString[5] = "Latitude";
            entryString[6] = "Longitude";
            mEntryWriter.writeNext(entryString);
        } catch(IOException e) {
            e.printStackTrace();
        } finally {
            if (mEntryWriter != null) {
                try {
                    mEntryWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        for (String file : files) {
            File activityFile = new File(Environment.getExternalStorageDirectory() + "/.logit/activities/" + file + ".csv");
            try {
                CSVReader mActivityReader = new CSVReader(new FileReader(activityFile));
                mEntryWriter = new CSVWriter(new FileWriter(exportFile, true));
                stringEntries = mActivityReader.readAll();

                entryString = new String[7];

                for (String[] str : stringEntries) {
                    entryString[0] = file;                          // date
                    entryString[1] = str[0];                        // category
                    entryString[2] = str[1];                        // activity
                    entryString[3] = convertIntoTimeFormat(str[2]); // start time
                    entryString[4] = convertIntoTimeFormat(str[3]); // end time
                    entryString[5] = str[4];                        // latitude
                    entryString[6] = str[5];                        // longitude
                    mEntryWriter.writeNext(entryString);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (mEntryWriter != null) {
                    try {
                        mEntryWriter.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private String convertIntoTimeFormat(String timeInMillis) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        Date date = new Date();
        date.setTime(Long.valueOf(timeInMillis).longValue());
        return sdf.format(date);
    }

    private boolean areDatesEligibleForExport(String from, String to) throws ParseException {
        boolean result = false;
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        long timeInMillis = sdf.parse(to).getTime() - sdf.parse(from).getTime();
        if (timeInMillis >= 0) {
            if (timeInMillis <= (5 * 86400000)) {
                result = true;
            } else {
                message = "Difference Between Start Date & End Date\nCannot Exceed 5 Days";
            }
        } else {
            message = "Start Date Should Be Less Than Or Equal\nTo The End Date";
        }
        return result;
    }

    public void onClickBack(View view) {
        finish();
    }
}
