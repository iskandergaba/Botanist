// Add a plant
// @author: Christopher Besser and Iskander Gaba
package com.scientists.happy.botanist.ui;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.scientists.happy.botanist.R;
import com.scientists.happy.botanist.controller.NewPlantController;
public class NewPlantActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_plant);
        NewPlantController controller = new NewPlantController(this);
        controller.load();
        overridePendingTransition(R.anim.slide_up, R.anim.hold);
    }

    @Override
    protected void onPause() {
        overridePendingTransition(R.anim.hold, R.anim.slide_down);
        super.onPause();
    }

    @Override
    public boolean onSupportNavigateUp() {
        super.onBackPressed();
        return true;
    }
}