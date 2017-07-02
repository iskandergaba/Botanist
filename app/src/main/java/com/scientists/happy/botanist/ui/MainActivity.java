// Main page
// @author: Christopher Besser, Iskander Gaba, Antonio Muscarella, and Wendy Zhang
package com.scientists.happy.botanist.ui;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridView;

import com.scientists.happy.botanist.R;
import com.scientists.happy.botanist.controller.MainController;
import com.scientists.happy.botanist.data.DatabaseManager;
import com.scientists.happy.botanist.utils.AppRater;
public class MainActivity extends AppCompatActivity {
    private static final int VIEW_ACCOUNT = 1;
    private MainController mController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayUseLogoEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
            getSupportActionBar().setIcon(R.drawable.ic_logo_botanist);
        }
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            /**
             * Handle action button click
             * @param view - current view
             */
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, NewPlantActivity.class));
            }
        });
        View tipView = findViewById(R.id.daily_tip_cardview);
        tipView.findViewById(R.id.daily_tip_dismiss_button).setOnClickListener(new View.OnClickListener() {
            /**
             * change visibility of CardView to gone if user clicked "Dismiss" button
             * @param v - the view
             */
            @Override
            public void onClick(View v) {
                findViewById(R.id.daily_tip_cardview).setVisibility(View.GONE);
            }
        });
        GridView gridView = (GridView) findViewById(R.id.plants_grid);
        gridView.setEmptyView(findViewById(R.id.empty_grid_view));
        mController = new MainController(this);
        AppRater.INSTANCE.appLaunched(this);
    }

    @Override
    protected void onStart() {
        mController.load();
        super.onStart();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if ((requestCode == VIEW_ACCOUNT) && (resultCode == RESULT_OK)) {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            case R.id.action_account:
                startActivityForResult(new Intent(MainActivity.this, AccountActivity.class), VIEW_ACCOUNT);
                return true;
            case R.id.action_shop:
                startActivity(new Intent(MainActivity.this, ShopActivity.class));
                return true;
            case R.id.action_about:
                startActivity(new Intent(MainActivity.this, AboutActivity.class));
                return true;
            case R.id.action_help:
                DatabaseManager.getInstance().showTutorial(this, mController.loadTutorialItems(), true);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}