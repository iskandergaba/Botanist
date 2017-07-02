package com.scientists.happy.botanist.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.scientists.happy.botanist.R;
import com.scientists.happy.botanist.data.DatabaseManager;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ImageView logo = (ImageView) findViewById(R.id.logo_splash);
        Animation myFadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.blink_and_fade);
        logo.startAnimation(myFadeInAnimation);
        // Instantiate the database
        DatabaseManager.getInstance().splashLoadAutocomplete(this);
    }
}