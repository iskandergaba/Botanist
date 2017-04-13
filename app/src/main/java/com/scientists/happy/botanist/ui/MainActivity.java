// Main page
// @author: Christopher Besser, Iskander Gaba, Antonio Muscarella, and Wendy Zhang
package com.scientists.happy.botanist.ui;
import android.content.DialogInterface;
import android.content.Intent;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import com.scientists.happy.botanist.R;
import com.scientists.happy.botanist.data.DatabaseManager;

import java.util.ArrayList;

import za.co.riggaroo.materialhelptutorial.TutorialItem;
public class MainActivity extends AppCompatActivity {
    private static final int VIEW_ACCOUNT = 1;
    private DatabaseManager mDatabase;
    /**
     * Launch app
     * @param savedInstanceState - app state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDatabase = DatabaseManager.getInstance();
        mDatabase.showTutorial(this, loadTutorialItems(), false);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            /**
             * Handle action button click
             * @param view - current view
             */
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, AddPlantActivity.class));
            }
        });
        GridView gridView = (GridView) findViewById(R.id.gridview);
        gridView.setEmptyView(findViewById(R.id.empty_grid_view));
        mDatabase.populatePlantGrid(this, gridView);
        mDatabase.getIndexOfLastDailyTip(this, getResources().getStringArray(R.array.daily_tips_values));
        mDatabase.getDateOfLastDailyTip(this);
    }

    /**
     * Handle options menu
     * @param menu - options menu
     * @return Returns success code
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /**
     * Handle selected option
     * @param item - selected option
     * @return Returns success code
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }
        else if (id == R.id.action_account) {
            startActivityForResult(new Intent(MainActivity.this, AccountActivity.class), VIEW_ACCOUNT);
            return true;
        }
        else if (id == R.id.action_shop) {
            startActivity(new Intent(MainActivity.this, ShopActivity.class));
            return true;
        }
        else if (id == R.id.action_about) {
            startActivity(new Intent(MainActivity.this, AboutActivity.class));
            return true;
        }
        else if (id == R.id.action_help) {
            mDatabase.showTutorial(this, loadTutorialItems(), false);
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Launched activity ended
     * @param requestCode - activity launch request code
     * @param resultCode - activity launch result
     * @param data - activity result data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if ((requestCode == VIEW_ACCOUNT) && (resultCode == RESULT_OK)) {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
        }
    }

    /**

     * Generate the daily tip card view
     * @param dailyTip - the text to be displayed in the daily tip card view
     */
    public void generateDailyTipCardView(String dailyTip) {
        ((TextView) findViewById(R.id.daily_tip_text)).setText(dailyTip);
        findViewById(R.id.daily_tip_dismiss_button).setOnClickListener(new View.OnClickListener() {

            /**
             * change visibility of CardView to gone if user clicked "Dismiss" button
             * @param v - the view
             */
            @Override
            public void onClick(View v) {
                findViewById(R.id.daily_tip_cardview).setVisibility(View.GONE);
            }
        });
    }

    /**
     * Hide/show the daily tip card view depending on whether or not Firebase has that the user
     * saw a daily tip today already
     * @param visible - boolean corresponding to whether or not the daily tip should be visible
     */
    public void displayDailyTipCardView(boolean visible) {
        if (visible) {
            findViewById(R.id.daily_tip_cardview).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.daily_tip_cardview).setVisibility(View.GONE);
        }
    }
      
     /*
     * Fetch assets for the tutorial
     * @return - Returns the list of tutorial items
     */
    private ArrayList<TutorialItem> loadTutorialItems() {
        TutorialItem tutorialItem1 = new TutorialItem(getString(R.string.tutorial_title_0), getString(R.string.tutorial_contents_0),
                R.color.colorPrimary, R.drawable.tutorial_0,  R.drawable.tutorial_0);
        TutorialItem tutorialItem2 = new TutorialItem(getString(R.string.tutorial_title_1), getString(R.string.tutorial_contents_1),
                R.color.colorPrimary, R.drawable.tutorial_1,  R.drawable.tutorial_1);
        TutorialItem tutorialItem3 = new TutorialItem(getString(R.string.tutorial_title_2), getString(R.string.tutorial_contents_2),
                R.color.colorPrimary, R.drawable.tutorial_2,  R.drawable.tutorial_2);
        ArrayList<TutorialItem> tutorialItems = new ArrayList<>();
        tutorialItems.add(tutorialItem1);
        tutorialItems.add(tutorialItem2);
        tutorialItems.add(tutorialItem3);
        return tutorialItems;
    }
}