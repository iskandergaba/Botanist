// Add a plant
// @author: Christopher Besser and Iskander Gaba
package com.scientists.happy.botanist.ui;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.scientists.happy.botanist.R;
import com.scientists.happy.botanist.controller.NewPlantController;
public class NewPlantActivity extends AppCompatActivity {

    private NewPlantController mController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_plant);
        mController = new NewPlantController(this);
        mController.showTutorial(false);
        overridePendingTransition(R.anim.slide_up, R.anim.hold);
    }

    @Override
    protected void onStart() {
        mController.load();
        super.onStart();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_help, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_help) {
            mController.showTutorial(true);
        }
        return super.onOptionsItemSelected(item);
    }
}