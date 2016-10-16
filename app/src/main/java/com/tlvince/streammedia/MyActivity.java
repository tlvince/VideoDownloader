package com.tlvince.streammedia;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;


public class MyActivity extends AppCompatActivity {
    public static final String PREFS_NAME = "MyPrefsFile";
    private static final String TAG = "MyActivity";
    private int numClicks = 0;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        numClicks = settings.getInt("numClicks", 0);

        setContentView(R.layout.activity_my);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_my, menu);
        return true;
    }


    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop(){
        super.onStop();

    }

    public void hideApp(MenuItem item) {
        PackageManager p = getPackageManager();
        p.setComponentEnabledSetting(getComponentName(), PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);

        Toast.makeText(this, "Done! (you may need to reboot)", Toast.LENGTH_LONG).show();
    }

    public void showAbout(MenuItem item){
        AboutDialog myDiag = new AboutDialog();
        myDiag.show(getSupportFragmentManager(), "Diag");
        numClicks++;

        if(numClicks == 5) {
            Toast.makeText(this, "It's not changed :)", Toast.LENGTH_LONG).show();
        }

        // We need an Editor object to make preference changes.
        // All objects are from android.context.Context
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt("numClicks", numClicks);

        // Commit the edits!
        editor.apply();
    }



}

