package com.scientists.happy.botanist.ui;
/*
@Wendy
*/

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import com.scientists.happy.botanist.R;


public class ShopActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop);
    }

    @Override
    public boolean onSupportNavigateUp() {
        super.onBackPressed();
        return true;
    }
}