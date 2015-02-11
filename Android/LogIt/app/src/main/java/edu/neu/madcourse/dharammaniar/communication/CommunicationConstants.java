package edu.neu.madcourse.dharammaniar.communication;

/**
 * Created by Dharam on 10/21/2014.
 */
public class CommunicationConstants
{
    public static final String TAG = "GCM_Globals";
    public static final String GCM_SENDER_ID = "825921457670";
    public static final String BASE_URL = "https://android.googleapis.com/gcm/send";
    public static final String PREFS_NAME = "GCM_Communication";
    public static final String GCM_API_KEY = "AIzaSyBubj705R46yWoArAqDrQqroY2CSdeqw4w";
    public static final int SIMPLE_NOTIFICATION = 22;
    public static final long GCM_TIME_TO_LIVE = 60L * 60L * 24L * 7L * 4L; // 4 Weeks
    public static int mode = 0;
}