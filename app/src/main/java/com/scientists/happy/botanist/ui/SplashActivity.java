package com.scientists.happy.botanist.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.scientists.happy.botanist.data.DatabaseManager;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Instantiate the database
        DatabaseManager.getInstance();
        startActivity(new Intent(this, LoginActivity.class));
        finish();

    }
}