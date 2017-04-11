package com.scientists.happy.botanist.ui;
/*
Copyright 2016 Iskander Gaba

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import com.scientists.happy.botanist.R;
import java.util.ArrayList;
import za.co.riggaroo.materialhelptutorial.TutorialItem;
import za.co.riggaroo.materialhelptutorial.tutorial.MaterialTutorialActivity;
public class AboutActivity extends AppCompatActivity {
    private static final int REQUEST_CODE = 1234;
    /**
     * The activity is launched
     * @param savedInstanceState - current app state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
    }

    /**
     * User pressed back
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
            loadTutorial();
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Load the tutorial
     */
    public void loadTutorial() {
        Intent mainAct = new Intent(this, MaterialTutorialActivity.class);
        mainAct.putParcelableArrayListExtra(MaterialTutorialActivity.MATERIAL_TUTORIAL_ARG_TUTORIAL_ITEMS, getTutorialItems(this));
        startActivityForResult(mainAct, REQUEST_CODE);
    }

    /**
     * Fetch assets for the tutorial
     * @param context - current app context
     * @return - Returns the list of tutorial items
     */
    private ArrayList<TutorialItem> getTutorialItems(Context context) {
        TutorialItem tutorialItem1 = new TutorialItem(context.getString(R.string.tutorial_title_0), context.getString(R.string.tutorial_contents_0),
                R.color.colorPrimary, R.drawable.tutorial_0,  R.drawable.tutorial_0);
        TutorialItem tutorialItem2 = new TutorialItem(context.getString(R.string.tutorial_title_1), context.getString(R.string.tutorial_contents_1),
                R.color.colorPrimary, R.drawable.tutorial_1,  R.drawable.tutorial_1);
        TutorialItem tutorialItem3 = new TutorialItem(context.getString(R.string.tutorial_title_2), context.getString(R.string.tutorial_contents_2),
                R.color.colorPrimary, R.drawable.tutorial_2,  R.drawable.tutorial_2);
        ArrayList<TutorialItem> tutorialItems = new ArrayList<>();
        tutorialItems.add(tutorialItem1);
        tutorialItems.add(tutorialItem2);
        tutorialItems.add(tutorialItem3);
        return tutorialItems;
    }
}