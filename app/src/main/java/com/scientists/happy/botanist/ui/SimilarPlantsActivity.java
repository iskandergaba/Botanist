// Show users similar plants
// @author: Christopher Besser and Antonio Muscarella
package com.scientists.happy.botanist.ui;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListAdapter;
import android.widget.ListView;
import com.scientists.happy.botanist.R;
import com.scientists.happy.botanist.data.DatabaseManager;
public class SimilarPlantsActivity extends AppCompatActivity {
    private DatabaseManager mDatabase;
    /**
     * The activity is launched
     * @param savedInstanceState - current app state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_similar_plants);
        mDatabase = DatabaseManager.getInstance();
        ListView list = (ListView) findViewById(R.id.similar_plants);
        list.setEmptyView(findViewById(R.id.empty_list_view));
        String group = (String) getIntent().getExtras().get("group");
        String species = (String) getIntent().getExtras().get("species");
        ListAdapter adapter = mDatabase.getSimilarPlants(this, group, species);
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
}