package com.scientists.happy.botanist.ui;
/*
@Wendy
*/
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.scientists.happy.botanist.R;
import com.scientists.happy.botanist.data.DatabaseManager;

import java.util.ArrayList;

import za.co.riggaroo.materialhelptutorial.TutorialItem;
public class ShopActivity extends AppCompatActivity {

    private DatabaseManager mDatabase;

    /**
     * Launch the activity
     * @param savedInstanceState - current app state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop);
        mDatabase = DatabaseManager.getInstance();
        mDatabase.showTutorial(this, loadTutorialItems(), false);
        View shopButton = findViewById(R.id.shop_button);
        shopButton.setOnClickListener(new View.OnClickListener() {
            /**
             * User clicked the poop button
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

    /**
     * User pressed the back button
     * @return Returns true
     */
    @Override
    public boolean onSupportNavigateUp() {
        super.onBackPressed();
        return true;
    }

    /**
     * Handle options menu
     * @param menu - options menu
     * @return Returns success code
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_help, menu);
        return true;
    }

    /**
     * Handle selected option
     * @param item - selected option
     * @return Returns success code
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_help) {
            mDatabase.showTutorial(this, loadTutorialItems(), true);
        }
        return super.onOptionsItemSelected(item);
    }

    /**
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