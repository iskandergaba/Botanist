// View diseases a plant can get
// @author: Antonio Muscarella
package com.scientists.happy.botanist.ui;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListAdapter;
import android.widget.ListView;
import com.scientists.happy.botanist.R;
import com.scientists.happy.botanist.data.DatabaseManager;
import java.util.ArrayList;
import za.co.riggaroo.materialhelptutorial.TutorialItem;
public class DiseaseActivity extends AppCompatActivity {
    private DatabaseManager mDatabase;
    /**
     * The activity is launched
     * @param savedInstanceState - current app state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_disease);
        mDatabase = DatabaseManager.getInstance();
        mDatabase.showTutorial(this, loadTutorialItem(), false);
        ListView list = (ListView) findViewById(R.id.diseases);
        list.setEmptyView(findViewById(R.id.empty_list_view));
        String group = (String) getIntent().getExtras().get("group");
        ListAdapter adapter = mDatabase.getDiseases(this, group);
        list.setAdapter(adapter);
    }

    /**
     * User navigated up from the activity
     * @return returns true
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
            mDatabase.showTutorial(this, loadTutorialItem(), true);
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Fetch assets for the tutorial
     * @return - Returns the list of tutorial items
     */
    private ArrayList<TutorialItem> loadTutorialItem() {
        TutorialItem tutorialItem1 = new TutorialItem(getString(R.string.diseases_tutorial_title_0), getString(R.string.diseases_tutorial_contents_0),
                R.color.colorAccent, R.drawable.diseases_tutorial_0,  R.drawable.diseases_tutorial_0);
        ArrayList<TutorialItem> tutorialItems = new ArrayList<>();
        tutorialItems.add(tutorialItem1);
        return tutorialItems;
    }
}