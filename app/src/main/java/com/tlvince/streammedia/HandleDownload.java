package com.tlvince.streammedia;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class HandleDownload extends AppCompatActivity implements AsyncResponse {

    public static final String PREFS_NAME = "MyPrefsFile";
    private static final String TAG = "HandleDownload";
    private TextView text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_handle_download);

        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        int numClicks = settings.getInt("numClicks", 0);
        text = (TextView) findViewById(R.id.shareWidget);
        text.setText("Sit Tight\n\nWe're readying the Download....\n");

        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if ("text/plain".equals(type)) {
                String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
                Log.d(TAG, "action: " + action);
                Log.d(TAG, "type: " + type);
                Log.d(TAG, "sharedText: " + sharedText);

                if(numClicks < 5) {
                    if (sharedText.startsWith("https://youtu.be/") || sharedText.toLowerCase().contains("youtube.com")) {
                        Toast.makeText(this, "Youtube disabled by 'default'", Toast.LENGTH_LONG).show();
                        text.setText("Youtube is disabled.\n\nSee https://github.com/zeronickname/VideoDownloader");
                        return;
                    }
                }

                Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
                calendar.setTime(new Date());               //Set the Calendar to now
                int hour = calendar.get(Calendar.HOUR_OF_DAY); //Get the hour from the calendar

                String url = "http://";
                // We're running on a free heroku instance. THey need to sleep for atleast 6 hrs in a day
                // SO lets just run two free instances and swap between them depending on teh time of the day, giving each instance a chance to sleep for 12hrs
                if(hour >= 0 && hour <= 12)
                {
                    url += "youtube-dl55.";
                } else {
                    url += "youtube-dl99.";
                }
                url += "herokuapp.com/api/info?url=" + sharedText;
                AsyncGetJSON asyncTask =new AsyncGetJSON(this, text);
                asyncTask.delegate = this;
                asyncTask.execute(url);

            }
        }

    }

    public void processFinish(){
        finish();
    }
}
