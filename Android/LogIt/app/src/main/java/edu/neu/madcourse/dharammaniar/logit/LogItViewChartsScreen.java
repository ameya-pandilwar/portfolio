package edu.neu.madcourse.dharammaniar.logit;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.Spinner;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.interfaces.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.Legend;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import au.com.bytecode.opencsv.CSVReader;
import edu.neu.madcourse.dharammaniar.R;

/**
 * Created by Ameya on 29-11-2014.
 */
public class LogItViewChartsScreen extends Activity implements OnChartValueSelectedListener {

    Context context;
    DatePicker datePicker;

    private PieChart mChart;

    List<String> categories;
    List<String> activities;

    Map<String, Float> categoriesTime;
    Map<String, Float> activitiesTime;

    float categoriesTotal, activitiesTotal;

    private Spinner pieChartSpinner;
    String date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_it_view_charts_screen);

        context = this;
        datePicker = (DatePicker) findViewById(R.id.viewChartsScreenDatePicker);

        int year = datePicker.getYear();
        int month = datePicker.getMonth();
        int day = datePicker.getDayOfMonth();

        prepareDateInFormat(day, month, year);

        pieChartSpinner = (Spinner) findViewById(R.id.pieChartSpinner);
        List<String> pieChartSpinnerList = new ArrayList<String>();
        pieChartSpinnerList.add("Categories");
        pieChartSpinnerList.add("Activities");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, pieChartSpinnerList);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        pieChartSpinner.setAdapter(dataAdapter);

        mChart = (PieChart) findViewById(R.id.chart1);

        // change the color of the center-hole
        mChart.setHoleColor(Color.rgb(235, 235, 235));
        mChart.setHoleRadius(30f);
        mChart.setDescription("");
        mChart.setDrawYValues(true);
        mChart.setDrawCenterText(true);
        mChart.setDrawHoleEnabled(true);
        mChart.setRotationAngle(0);
        mChart.setTransparentCircleRadius(35f);

        mChart.setValueTextColor(Color.BLACK);

        // draws the corresponding description value into the slice
        mChart.setDrawXValues(true);

        // enable rotation of the chart by touch
        mChart.setRotationEnabled(true);

        // display percentage values
        mChart.setUsePercentValues(true);

        // add a selection listener
        mChart.setOnChartValueSelectedListener(this);

        mChart.setTouchEnabled(true);
        mChart.setCenterText(date);

        loadDataFromEntries(date);

        setData(categories.size(), categoriesTotal, categories, categoriesTime);
        mChart.animateXY(1500, 1500);

        Legend l = mChart.getLegend();
        l.setTextColor(Color.BLACK);

        pieChartSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String item = String.valueOf(pieChartSpinner.getSelectedItem());
                if (item.equalsIgnoreCase("Categories")) {
                    setData(categories.size(), categoriesTotal, categories, categoriesTime);
                } else if (item.equalsIgnoreCase("Activities")) {
                    setData(activities.size(), activitiesTotal, activities, activitiesTime);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        datePicker.init(year, month, day, new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                prepareDateInFormat(dayOfMonth, monthOfYear, year);
                loadDataFromEntries(date);
                mChart.setCenterText(date);
                pieChartSpinner.setSelection(0);
                setData(categories.size(), categoriesTotal, categories, categoriesTime);
            }
        });
    }

    private void prepareDateInFormat(int dayOfMonth, int monthOfYear, int year) {
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
        date = day + "-" + month + "-" + String.valueOf(year);
    }

    private void setData(int count, float range, List<String> list, Map<String, Float> listTime) {
        ArrayList<Entry> yValues = new ArrayList<Entry>();

        for (int i = 0; i < count; i++) {
            yValues.add(new Entry(listTime.get(list.get(i)), i));
        }
        ArrayList<String> xValues = new ArrayList<String>();
        for (int i = 0; i < count; i++) {
            xValues.add(list.get(i));
        }
        PieDataSet pieDataSet = new PieDataSet(yValues, "");
        pieDataSet.setSliceSpace(3f);

        ArrayList<Integer> colors = new ArrayList<Integer>();
        for (int c : ColorTemplate.PASTEL_COLORS)
            colors.add(c);
        for (int c : ColorTemplate.JOYFUL_COLORS)
            colors.add(c);
        for (int c : ColorTemplate.COLORFUL_COLORS)
            colors.add(c);
        for (int c : ColorTemplate.LIBERTY_COLORS)
            colors.add(c);
        for (int c : ColorTemplate.VORDIPLOM_COLORS)
            colors.add(c);

        colors.add(ColorTemplate.getHoloBlue());
        pieDataSet.setColors(colors);
        PieData data = new PieData(xValues, pieDataSet);
        mChart.setData(data);

        // undo all highlights
        mChart.highlightValues(null);
        mChart.invalidate();
    }

    private void loadDataFromEntries(String date) {
        categories = new ArrayList<String>();
        activities = new ArrayList<String>();
        categoriesTime = new HashMap<String, Float>();
        activitiesTime = new HashMap<String, Float>();

        float categoryTime, activityTime;

        String next[] = {};
        File activityDirectory = new File(Environment.getExternalStorageDirectory() + "/.logit/");
        if (!activityDirectory.exists()) {
            activityDirectory.mkdirs();
        }
        File activityFile = new File(Environment.getExternalStorageDirectory() + "/.logit/activities/" + date + ".csv");
        try {
            CSVReader mActivityReader = new CSVReader(new FileReader(activityFile));
            while (true) {
                next = mActivityReader.readNext();
                if (next != null) {
                    if (!categories.contains(next[0])) {
                        categories.add(next[0]);
                    }
                    if (!activities.contains(next[1])) {
                        activities.add(next[1]);
                    }

                    if (categoriesTime.containsKey(next[0])) {
                        categoryTime = categoriesTime.get(next[0]);
                        categoryTime += (Long.valueOf(next[3]) - Long.valueOf(next[2]));
                    } else {
                        categoryTime = (Long.valueOf(next[3]) - Long.valueOf(next[2]));
                    }
                    categoriesTime.put(next[0], categoryTime);
                    categoriesTotal += categoryTime;

                    if (activitiesTime.containsKey(next[1])) {
                        activityTime = activitiesTime.get(next[1]);
                        activityTime += (Long.valueOf(next[3]) - Long.valueOf(next[2]));
                    } else {
                        activityTime = (Long.valueOf(next[3]) - Long.valueOf(next[2]));
                    }
                    activitiesTime.put(next[1], activityTime);
                    activitiesTotal += activityTime;

                } else {
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onValueSelected(Entry e, int dataSetIndex) {
    }

    @Override
    public void onNothingSelected() {
    }

    public void onClickBack(View view) {
        finish();
    }

}
