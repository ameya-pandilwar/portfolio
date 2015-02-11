package edu.neu.madcourse.dharammaniar.logit;

import android.graphics.drawable.Drawable;

/**
 * Created by Ameya on 04-12-2014.
 */
public class LogItEntry {

    private Drawable category;
    private String activity;
    private String startTime;
    private String endTime;
    private double latitude;
    private double longitude;
    private String date;
    private String categoryString;
    private String timeSpent;

    public Drawable getCategory() {
        return category;
    }

    public void setCategory(Drawable category) {
        this.category = category;
    }

    public String getActivity() {
        return activity;
    }

    public void setActivity(String activity) {
        this.activity = activity;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCategoryString() {
        return categoryString;
    }

    public void setCategoryString(String categoryString) {
        this.categoryString = categoryString;
    }

    public String getTimeSpent() {
        return timeSpent;
    }

    public void setTimeSpent(String timeSpent) {
        this.timeSpent = timeSpent;
    }

    public LogItEntry() {
    }

    public LogItEntry(Drawable category, String activity, String startTime, String endTime, double latitude, double longitude) {
        this.category = category;
        this.activity = activity;
        this.startTime = startTime;
        this.endTime = endTime;
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
