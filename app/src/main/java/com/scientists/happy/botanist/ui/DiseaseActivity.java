// View diseases a plant can get
// @author: Antonio Muscarella
package com.scientists.happy.botanist.ui;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListAdapter;
import android.widget.ListView;
import com.scientists.happy.botanist.R;
import com.scientists.happy.botanist.data.DatabaseManager;
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
        ListView list = (ListView) findViewById(R.id.diseases);
        list.setEmptyView(findViewById(R.id.empty_list));
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
}