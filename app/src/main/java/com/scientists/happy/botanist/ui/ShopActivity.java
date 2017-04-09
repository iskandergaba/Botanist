package com.scientists.happy.botanist.ui;
/*
@Wendy
*/

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.scientists.happy.botanist.R;


public class ShopActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop);

        View poopButton = findViewById(R.id.shop_button);
        poopButton.setOnClickListener(new View.OnClickListener() {
            /**
             * User clicked update height
             *
             * @param v - current view
             */
            @Override
            public void onClick(View v) {
                doStuff();
            }
        });
    }

    private void doStuff() {
        // do stuff tho
    }

    @Override
    public boolean onSupportNavigateUp() {
        super.onBackPressed();
        return true;
    }
}