package com.tlvince.streammedia;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class AsyncGetJSON extends AsyncTask<String, Integer, Long> {
    private static final String TAG = "getJSON";

    public AsyncResponse delegate = null;
    private Context context;
    private Activity activity;
    private TextView textView;
    private CharSequence defaultText;

    public AsyncGetJSON( Activity mAct, TextView text ) {
        activity = mAct;
        context = mAct.getApplicationContext();

        textView = text;
        defaultText = textView.getText();
    }

    @Override
    protected Long doInBackground(String... params) {
        URL url;
        HttpURLConnection urlConnection = null;

        try {
            url = new URL(params[0]);

            Log.i(TAG, "url: " + url.toString());

            publishProgress(1);
            urlConnection = (HttpURLConnection) url
                    .openConnection();

            if (urlConnection.getResponseCode() == 500){
                publishProgress(0xFF);
                return null;
            }

            InputStream in = new BufferedInputStream(urlConnection.getInputStream());

            JSONObject json = new JSONObject(convertStreamToString(in));

            Log.i(TAG, "out: " + json.toString());

            String downloadURL = json.getJSONObject("info").get("url").toString();
            String title = json.getJSONObject("info").get("title").toString();

            Log.i(TAG, "downloadUrl: " + downloadURL);

            publishProgress(2);

            int vlcRequestCode = 42;
            Uri uri = Uri.parse(downloadURL);
            Intent vlcIntent = new Intent(Intent.ACTION_VIEW);
            vlcIntent.setPackage("org.videolan.vlc");
            vlcIntent.setDataAndTypeAndNormalize(uri, "video/*");
            vlcIntent.putExtra("title", title);
            activity.startActivityForResult(vlcIntent, vlcRequestCode);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            urlConnection.disconnect();
        }

        return null;
    }
    private int findIdToDownload(JSONArray json,String ext, int height ) {

        for (int i = 0; i < json.length(); i++) {
            try {
                JSONObject row = json.getJSONObject(i);
                if (row.getInt("height") == height && row.getString("ext").equals(ext)) {
                    return i;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return -1;
    }

    protected void onPostExecute(Long result) {
        if( result != null ) {
            Log.i(TAG, "Downloaded " + result + " bytes");
        }

        delegate.processFinish();
    }

    protected void onProgressUpdate(Integer... progress) {
        if (progress[0] == 1) {
            Toast.makeText(context, "Getting Video URL", Toast.LENGTH_SHORT).show();
            textView.setText(defaultText + "Getting Video URL...\n");
        } else if (progress[0] == 2) {
            Toast.makeText(context, "Starting VLC", Toast.LENGTH_LONG).show();
            textView.setText(defaultText + "Starting VLC...\n");
        } else if (progress[0] == 0xFF) {
            Toast.makeText(context, "Unsupported site?" , Toast.LENGTH_LONG).show();
            textView.setText(defaultText + "Unsupported site?\n");
        }
    }

    private static String convertStreamToString(java.io.InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }


}
