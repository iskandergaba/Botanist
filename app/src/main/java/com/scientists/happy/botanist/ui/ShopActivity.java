// Shop for botanist accessories
// @author: Wendy Zhang
package com.scientists.happy.botanist.ui;
import android.content.Intent;
import android.net.Uri;
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
    private static final String SHOP_FERTILIZER_URL = "https://www.amazon.com/Best-Sellers-Patio-Lawn-Garden-Fertilizers/zgbs/lawn-garden/3752891";
    private static final String SHOP_ACCESSORIES_URL = "https://www.amazon.com/Best-Sellers-Patio-Lawn-Garden-Plant-Container-Accessories/zgbs/lawn-garden/3480695011";
    private static final String SHOP_POT_URL = "https://www.amazon.com/Best-Sellers-Patio-Lawn-Garden-Gardening-Pots-Planters-Accessories/zgbs/lawn-garden/3480694011";
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
        View fertilizeButton = findViewById(R.id.shop_fertilizer_button);
        fertilizeButton.setOnClickListener(new View.OnClickListener() {
            /**
             * User clicked buy now
             * @param v - current view
             */
            @Override
            public void onClick(View v) {
                openWebPage(SHOP_FERTILIZER_URL);
            }
        });
        View accessoryButton = findViewById(R.id.shop_accessories_button);
        accessoryButton.setOnClickListener(new View.OnClickListener() {
            /**
             * User clicked buy now
             * @param v - current view
             */
            @Override
            public void onClick(View v) {
                openWebPage(SHOP_ACCESSORIES_URL);
            }
        });
        View potButton = findViewById(R.id.shop_pot_button);
        potButton.setOnClickListener(new View.OnClickListener() {
            /**
             * User clicked buy now
             * @param v - current view
             */
            @Override
            public void onClick(View v) {
                openWebPage(SHOP_POT_URL);
            }
        });
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

    private void openWebPage(String url) {
        Intent viewIntent = new Intent("android.intent.action.VIEW", Uri.parse(url));
        startActivity(viewIntent);
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