// View diseases a plant can get
// @author: Antonio Muscarella
package com.scientists.happy.botanist.ui;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.scientists.happy.botanist.R;
import com.scientists.happy.botanist.data.DatabaseManager;
public class StatsActivity extends AppCompatActivity {
    private DatabaseManager mDatabase;
    /**
     * The activity is being created
     * @param savedInstanceState - current activity state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);
        mDatabase = DatabaseManager.getInstance();
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