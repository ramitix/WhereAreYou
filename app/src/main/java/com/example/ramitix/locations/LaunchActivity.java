package com.example.ramitix.locations;

/**
 * Created by ramitix on 8/14/16.
 */

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;

public class LaunchActivity extends Activity {

    /** Duration of wait **/
    private final int SPLASH_DISPLAY_LENGTH = 2000;

    /** Called when the activity is first created. */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme_BrandedLaunch);
        super.onCreate(savedInstanceState);


        //setContentView(R.layout.activity_start);

        /* New Handler to start the Menu-Activity
         * and close this Splash-Screen after some seconds.*/
        new Handler().postDelayed(new Runnable(){
            @Override
           public void run() {
                /* Create an Intent that will start the Menu-Activity. */
                startActivity(new Intent(LaunchActivity.this, MainActivity.class));
                LaunchActivity.this.finish();

            }
        }, SPLASH_DISPLAY_LENGTH);
    }
}
